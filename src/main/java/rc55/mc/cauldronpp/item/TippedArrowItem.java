package rc55.mc.cauldronpp.item;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.CauldronppConfig;
import rc55.mc.cauldronpp.api.CppPotionHelper;

import java.util.List;
import java.util.Map;

public class TippedArrowItem extends Item {

    private IIcon icon = null;
    private IIcon overlayIcon = null;

    public TippedArrowItem() {
        this.setTextureName(Cauldronpp.MODID + ":tipped_arrow");
        this.setHasSubtypes(true);
        this.setMaxStackSize(64);
        this.setUnlocalizedName(Cauldronpp.MODID + ".tipped_arrow");
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.icon = Items.arrow.getIconFromDamage(0);
        this.overlayIcon = register.registerIcon(this.getIconString() + "_overlay");
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return this.icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int level) {
        return level == 0 ? super.getIconFromDamageForRenderPass(level, damage) : this.overlayIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int level) {
        return level > 0 ? this.getColor(stack) : 0xFFFFFF;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocalFormatted(this.getUnlocalizedName(),
            StatCollector.translateToLocal(CppPotionHelper.getPotionPrefixTranslationKey(stack.getItemDamage())));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean bl) {
        List<PotionEffect> effects = this.getEffects(stack);
        HashMultimap<String, AttributeModifier> attributeModifierToNameMap = HashMultimap.create();

        if (effects != null && !effects.isEmpty()) {
            for (PotionEffect effect : effects) {
                String s1 = StatCollector.translateToLocal(effect.getEffectName()).trim();
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                Map<IAttribute, AttributeModifier> map = potion.func_111186_k();

                if (map != null && !map.isEmpty()) {
                    for (Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributeModifier = entry.getValue();
                        AttributeModifier newAttributeModifier = new AttributeModifier(attributeModifier.getName(),
                            potion.func_111183_a(effect.getAmplifier(), attributeModifier), attributeModifier.getOperation());
                        attributeModifierToNameMap.put(entry.getKey().getAttributeUnlocalizedName(), newAttributeModifier);
                    }
                }

                if (effect.getAmplifier() > 0) {
                    s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + effect.getAmplifier()).trim();
                }

                if (effect.getDuration() > 20) {
                    s1 = s1 + " (" + Potion.getDurationString(effect) + ")";
                }

                if (potion.isBadEffect()) {
                    infoList.add(EnumChatFormatting.RED + s1);
                } else {
                    infoList.add(EnumChatFormatting.GRAY + s1);
                }
            }
        } else {
            String s = StatCollector.translateToLocal("potion.empty").trim();
            infoList.add(EnumChatFormatting.GRAY + s);
        }

        if (!attributeModifierToNameMap.isEmpty()) {
            infoList.add("");
            infoList.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("item.cauldronpp.potion.description.when_use"));

            for (Map.Entry<String, AttributeModifier> entry : attributeModifierToNameMap.entries()) {
                AttributeModifier attributeModifier = entry.getValue();
                double d0 = attributeModifier.getAmount();
                double d1;

                if (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) {
                    d1 = attributeModifier.getAmount();
                } else {
                    d1 = attributeModifier.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    infoList.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributeModifier.getOperation(),
                        new Object[]{ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + entry.getKey())}));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    infoList.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributeModifier.getOperation(),
                        new Object[]{ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + entry.getKey())}));
                }
            }
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        if (CauldronppConfig.enableTippedArrow && tab == this.getCreativeTab()) {
            items.add(new ItemStack(item, 1, 32767));
            items.add(new ItemStack(item, 1, 16123));
            items.add(new ItemStack(item, 1, 16085));
            items.add(new ItemStack(item, 1, 22809));
        }
    }

    public List<PotionEffect> getEffects(ItemStack stack) {
        return CppPotionHelper.getEffects(stack.getItemDamage());
    }

    @SideOnly(Side.CLIENT)
    public int getColor(int damage) {
        return CppPotionHelper.getPotionColor(damage);
    }

    @SideOnly(Side.CLIENT)
    public int getColor(ItemStack stack) {
        return this.getColor(stack.getItemDamage());
    }
}
