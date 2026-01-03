package rc55.mc.cauldronpp.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import rc55.mc.cauldronpp.api.Utils;
import rc55.mc.cauldronpp.entity.TippedArrowEntity;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class BowEvents {

    /// 弓右键
    @SubscribeEvent
    public void handleBowChargeEvent(ArrowNockEvent event) {
        final EntityPlayer player = event.entityPlayer;
        final ItemStack stack = event.result;
        if (player.capabilities.isCreativeMode || player.inventory.hasItem(CauldronppItems.CPP_TIPPED_ARROW)) {
            player.setItemInUse(stack, Items.bow.getMaxItemUseDuration(stack));
        }
    }

    /// 弓射击
    @SubscribeEvent
    public void handleBowShootEvent(ArrowLooseEvent event) {
        final EntityPlayer player = event.entityPlayer;
        final ItemStack bow = event.bow;
        final World world = player.worldObj;

        for (ItemStack stack : player.inventory.mainInventory) {
            if (Items.arrow.equals(Utils.getItemFromStack(stack))) {//原版箭，遵循原版逻辑
                break;
            } else if (CauldronppItems.CPP_TIPPED_ARROW.equals(Utils.getItemFromStack(stack))) {//药箭
                float f = event.charge / 20.0F;
                f = (f * f + f * 2.0F) / 3.0F;
                if ((double)f < 0.1D) {
                    return;
                }
                if (f > 1.0F) {
                    f = 1.0F;
                }

                TippedArrowEntity arrowEntity = new TippedArrowEntity(world, player, stack, f * 2.0F);

                if (f == 1.0F) {
                    arrowEntity.setIsCritical(true);
                }

                int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bow);
                if (k > 0) {
                    arrowEntity.setDamage(arrowEntity.getDamage() + (double)k * 0.5 + 0.5);
                }

                int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bow);
                if (l > 0) {
                    arrowEntity.setKnockbackStrength(l);
                }

                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bow) > 0) {
                    arrowEntity.setFire(100);
                }

                bow.damageItem(1, player);
                world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                if (player.capabilities.isCreativeMode) {
                    arrowEntity.canBePickedUp = 2;
                } else {
                    player.inventory.consumeInventoryItem(CauldronppItems.CPP_TIPPED_ARROW);
                }

                if (!world.isRemote) {
                    world.spawnEntityInWorld(arrowEntity);
                }

                event.setCanceled(true);
                break;
            }
        }
    }
}
