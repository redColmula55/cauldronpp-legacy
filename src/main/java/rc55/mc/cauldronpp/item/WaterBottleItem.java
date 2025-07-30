package rc55.mc.cauldronpp.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import rc55.mc.cauldronpp.Cauldronpp;

public class WaterBottleItem extends ItemPotion {

    public WaterBottleItem() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(false);
        this.setCreativeTab(CreativeTabs.tabBrewing);
        this.setTextureName("potion");
        this.setUnlocalizedName(Cauldronpp.MODID + ".water_bottle");
    }

    @Override
    public List<PotionEffect> getEffects(ItemStack stack) {
        return null;
    }

    @Override
    public List<PotionEffect> getEffects(int damage) {
        return null;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocal(this.getUnlocalizedName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean p_77624_4_) {}

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        if (this.getCreativeTab() == tab) items.add(new ItemStack(item));
    }
}
