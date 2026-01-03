package rc55.mc.cauldronpp.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import rc55.mc.cauldronpp.Cauldronpp;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有描述（lore）的物品
 */
public class DescrptiveItem extends Item {

    private final List<String> descriptions = new ArrayList<>();

    public DescrptiveItem(String name) {
        this.setUnlocalizedName(Cauldronpp.MODID + "." + name);
        this.setTextureName(Cauldronpp.MODID + ":" + name);
    }

    /// 设置描述（lore），会被格式化为 {@code item.{MODID}.{itemName}.{desc}}
    public Item setDescription(String translationKey) {
        this.descriptions.add(translationKey);
        return this;
    }

    public Item setDescription(String translationKey, int line) {
        this.descriptions.add(line, translationKey);
        return this;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        //译名格式
        return StatCollector.translateToLocal(this.getUnlocalizedName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean bl) {
        this.descriptions.forEach(key -> infoList.add(StatCollector.translateToLocal(this.getUnlocalizedName() + "." + key)));
    }
}
