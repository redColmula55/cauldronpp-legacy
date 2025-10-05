package rc55.mc.cauldronpp.api;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Utils {

    private Utils() {}

    public static <M> M make(M map, Consumer<M> consumer) {
        consumer.accept(map);
        return map;
    }

    public static <K, V> Map<K, V> makeMap(Consumer<Map<K, V>> consumer) {
        return make(new HashMap<>(), consumer);
    }

    public static Item getItemFromStack(ItemStack stack) {
        return stack == null ? Item.getItemFromBlock(Blocks.air) : stack.getItem();
    }

    public static void addStackToPlayerInv(World world, EntityPlayer player, ItemStack stack) {
        if (player.capabilities.isCreativeMode && !player.inventory.hasItem(getItemFromStack(stack))) {
            EntityItem entityitem = player.dropPlayerItemWithRandomChoice(stack, false);
            entityitem.delayBeforeCanPickup = 0;
            entityitem.func_145797_a(player.getDisplayName());
            world.updateEntity(entityitem);
        } else {
            if (player.getHeldItem() != null) {
                EntityItem entityitem = player.dropPlayerItemWithRandomChoice(stack, false);
                entityitem.delayBeforeCanPickup = 0;
                entityitem.func_145797_a(player.getDisplayName());
                world.updateEntity(entityitem);
            } else player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
        }
    }
}
