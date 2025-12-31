package rc55.mc.cauldronpp.mixin.early;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rc55.mc.cauldronpp.api.CppPotionHelper;
import rc55.mc.cauldronpp.api.Utils;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.List;
import java.util.Objects;

@Mixin(EntityPotion.class)
public abstract class EntityPotionMixin {

    @Shadow private ItemStack potionDamage;

    //使数据值为0的有效果药水生效
    @Inject(at = @At("HEAD"), method = "onImpact", cancellable = true)
    public void onImpact(MovingObjectPosition pos, CallbackInfo ci) {
        EntityPotion self = (EntityPotion)(Object)this;
        if (Utils.getItemFromStack(this.potionDamage) != CauldronppItems.CPP_SPLASH_POTION || self.worldObj.isRemote) {
            return;
        }

        self.worldObj.playAuxSFX(2002, (int) Math.round(self.posX), (int) Math.round(self.posY), (int) Math.round(self.posZ), self.getPotionDamage());
        self.setDead();
        ci.cancel();

        List<PotionEffect> list = CppPotionHelper.getEffects(self.getPotionDamage());

        if (Objects.isNull(list) || list.isEmpty()) {
            return;
        }

        AxisAlignedBB axisalignedbb = self.boundingBox.expand(4.0D, 2.0D, 4.0D);
        List<EntityLivingBase> list1 = self.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

        if (Objects.isNull(list1) || list1.isEmpty()) {
            return;
        }

        for (EntityLivingBase entity : list1) {
            double d0 = self.getDistanceSqToEntity(entity);

            if (d0 >= 16.0D) {
                continue;
            }

            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

            if (entity == pos.entityHit) {
                d1 = 1.0D;
            }

            for (PotionEffect potioneffect : list) {
                int i = potioneffect.getPotionID();

                if (Potion.potionTypes[i].isInstant()) {
                    Potion.potionTypes[i].affectEntity(self.getThrower(), entity, potioneffect.getAmplifier(), d1);
                    continue;
                }

                int j = (int) (d1 * (double) potioneffect.getDuration() + 0.5D);
                if (j > 20) {
                    entity.addPotionEffect(new PotionEffect(i, j, potioneffect.getAmplifier()));
                }
            }
        }
    }
}
