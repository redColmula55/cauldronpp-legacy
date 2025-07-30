package rc55.mc.cauldronpp.mixin.early;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccessor {

    @Invoker("getMovingObjectPositionFromPlayer")
    MovingObjectPosition accessMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean useLiquids);
}
