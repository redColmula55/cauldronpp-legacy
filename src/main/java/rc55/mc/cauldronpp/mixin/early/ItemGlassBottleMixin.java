package rc55.mc.cauldronpp.mixin.early;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rc55.mc.cauldronpp.api.Utils;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.Objects;

@Mixin(ItemGlassBottle.class)
public abstract class ItemGlassBottleMixin {
    //玻璃瓶右键装水
    @Inject(at = @At("HEAD"), method = "onItemRightClick", cancellable = true)
    public void onItemRightClick(ItemStack stack, World world, EntityPlayer player,
        CallbackInfoReturnable<ItemStack> cir) {
        ItemGlassBottle self = (ItemGlassBottle)(Object)this;
        MovingObjectPosition pos = ((ItemAccessor)self).accessMovingObjectPositionFromPlayer(world, player, true);
        if (Objects.isNull(pos) || pos.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return;
        }

        int x = pos.blockX;
        int y = pos.blockY;
        int z = pos.blockZ;
        if (!world.canMineBlock(player, x, y, z)
            || !player.canPlayerEdit(x, y, z, pos.sideHit, stack)) {
            return;
        }

        if (world.getBlock(x, y, z).getMaterial() != Material.water) {
            return;
        }

        stack.stackSize--;
        if (stack.stackSize <= 0) {
            cir.setReturnValue(new ItemStack(CauldronppItems.WATER_BOTTLE));
            return;
        }

        Utils.addStackToPlayerInv(world, player, new ItemStack(CauldronppItems.WATER_BOTTLE));
        cir.setReturnValue(stack);
    }
}
