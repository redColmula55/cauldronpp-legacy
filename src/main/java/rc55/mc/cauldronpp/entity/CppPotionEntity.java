package rc55.mc.cauldronpp.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.CauldronppConfig;
import rc55.mc.cauldronpp.api.CppPotionHelper;
import rc55.mc.cauldronpp.item.CauldronppItems;
import rc55.mc.cauldronpp.item.CppPotionItem;

import java.util.List;
import java.util.Optional;

/**
 * 投掷的药水实体
 * @see CppPotionItem
 */
public class CppPotionEntity extends EntityPotion implements IThrowableEntity, IEntityAdditionalSpawnData {

    private byte potionType = CppPotionHelper.SPLASH_TYPE;
    private int damage = 0;

    @SuppressWarnings("unused")
    public CppPotionEntity(World world) {
        super(world);
    }

    public CppPotionEntity(World world, EntityLivingBase owner, ItemStack stack) {
        super(world, owner, stack);
        if (stack.getItem() instanceof CppPotionItem) {
            this.potionType = ((CppPotionItem) stack.getItem()).getType();
            this.damage = stack.getItemDamage();
            //禁用就移除
            if (!CauldronppConfig.enableLingeringPotion && this.potionType == CppPotionHelper.LINGERING_TYPE) this.setDead();
        }
    }

    public CppPotionEntity(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
        if (stack.getItem() instanceof CppPotionItem) {
            this.potionType = ((CppPotionItem) stack.getItem()).getType();
            this.damage = stack.getItemDamage();
            //禁用就移除
            if (!CauldronppConfig.enableLingeringPotion && this.potionType == CppPotionHelper.LINGERING_TYPE) this.setDead();
        }
    }

    /**
     * 药水类型
     * @see CppPotionHelper#SPLASH_TYPE
     */
    public byte getType() {
        return this.potionType;
    }
    /// 渲染颜色
    @SideOnly(Side.CLIENT)
    public int getRenderColor() {
        return CppPotionHelper.getPotionColor(this.damage);
    }

    /// 瓶身材质
    @SideOnly(Side.CLIENT)
    public IIcon getBottleRenderIcon() {
        return Optional.ofNullable(CppPotionHelper.getPotionItem(this.getType(), this.getPotionDamage()).getItem())
            .orElse(CauldronppItems.CPP_LINGERING_POTION).getIconFromDamage(0);
    }

    @Override
    protected void onImpact(MovingObjectPosition pos) {
        if (CauldronppConfig.enableLingeringPotion || this.potionType != CppPotionHelper.LINGERING_TYPE) {
            if (!this.worldObj.isRemote) {
                //逻辑
                if (this.potionType == CppPotionHelper.LINGERING_TYPE) {
                    //滞留-生成效果云
                    LingeringEffectCloudEntity effectCloud = new LingeringEffectCloudEntity(this);
                    this.worldObj.spawnEntityInWorld(effectCloud);
                    this.setDead();
                } else if (this.potionType == CppPotionHelper.SPLASH_TYPE) {
                    //喷溅-遵循原版逻辑
                    List<PotionEffect> effects = CppPotionHelper.getEffects(this.damage);
                    if (!effects.isEmpty()) {
                        List<EntityLivingBase> hitEntityList = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
                            this.boundingBox.expand(4.0, 2.0, 4.0));
                        if (hitEntityList != null && !hitEntityList.isEmpty()) {
                            for (EntityLivingBase entityHit : hitEntityList) {
                                double distanceSquared = this.getDistanceSqToEntity(entityHit);
                                //在生效范围内
                                if (distanceSquared < 16) {
                                    //效用时间随距离衰减
                                    double durationMultiplier = 1.0 - Math.sqrt(distanceSquared) / 4.0;
                                    if (entityHit == pos.entityHit) {
                                        durationMultiplier = 1.0;
                                    }
                                    //应用效果
                                    for (PotionEffect effect : effects) {
                                        int id = effect.getPotionID();
                                        if (Potion.potionTypes[id].isInstant()) {
                                            Potion.potionTypes[id].affectEntity(this.getThrower(), entityHit, effect.getAmplifier(), durationMultiplier);
                                        } else {
                                            int duration = (int)(durationMultiplier * (double)effect.getDuration() + 0.5);
                                            if (duration > 20) {
                                                entityHit.addPotionEffect(new PotionEffect(id, duration, effect.getAmplifier()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.setDead();
                }
            } else {
                //特效
                this.playAuxSfxOnImpact();
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.potionType = nbt.getByte("PotionType");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("PotionType", this.potionType);
    }

    @Override
    public void setThrower(Entity entity) {
        //未实现
    }

    //客户端/服务端数据同步
    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeByte(this.potionType);
        buf.writeInt(this.damage);
    }
    @Override
    public void readSpawnData(ByteBuf buf) {
        this.potionType = buf.readByte();
        this.damage = buf.readInt();
    }

    /// 击中方块的粒子/声音效果
    @SideOnly(Side.CLIENT)
    public void playAuxSfxOnImpact() {
        //瓶子碎裂
        String s = "iconcrack_" + Item.getIdFromItem(CppPotionHelper.getPotionItem(this.potionType, 0).getItem()) + "_" + this.getPotionDamage();
        for (int i = 0; i < 8; i++) {
            this.worldObj.spawnParticle(s, this.posX, this.posY, this.posZ, this.rand.nextGaussian() * 0.15, this.rand.nextDouble() * 0.2, this.rand.nextGaussian() * 0.15);
        }
        //粒子颜色
        final int color = this.getRenderColor();
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        //粒子
        for (int i = 0; i < 100; i++) {
            double multiplier = this.rand.nextDouble() * 4.0;
            double distance = this.rand.nextDouble() * Math.PI * 2.0;
            double velocityX = Math.cos(distance) * multiplier;
            double velocityY = 0.01 + this.rand.nextDouble() * 0.5;
            double velocityZ = Math.sin(distance) * multiplier;
            EntitySpellParticleFX particle = new EntitySpellParticleFX(this.worldObj, this.posX + velocityX * 0.1, this.posY + 0.3, this.posZ + velocityZ * 0.1, velocityX, velocityY, velocityZ);
            if (((CppPotionItem)CauldronppItems.CPP_POTION).isEffectInstant(this.damage)) {
                particle.setBaseSpellTextureIndex(144);
            }
            float colorOffset = 0.75f + this.rand.nextFloat() * 0.25f;
            particle.setRBGColorF(r * colorOffset, g * colorOffset, b * colorOffset);
            particle.multiplyVelocity((float) multiplier);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
        //音效
        this.worldObj.playSound(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "game.potion.smash",
            1.0f, this.rand.nextFloat() * 0.1f + 0.9f, false);
    }
}
