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
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.CauldronppConfig;
import rc55.mc.cauldronpp.api.CppPotionHelper;
import rc55.mc.cauldronpp.entity.CppPotionEntity;

import java.util.List;
import java.util.Map;

public class CppPotionItem extends ItemPotion {

    private final byte potionType;
    private IIcon icon;

    public CppPotionItem(byte type) {
        this.potionType = type;
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setTextureName("potion");
        this.setCreativeTab(CreativeTabs.tabBrewing);
        this.setUnlocalizedName("potion");
        this.setContainerItem(Items.glass_bottle);
    }

    /// @deprecated 被 {@link CppPotionItem#getType} 及 {@link CppPotionItem#isThrowable} 取代
    @Deprecated
    public boolean isSplash() {
        return this.potionType == CppPotionHelper.SPLASH_TYPE;
    }

    /// 类型
    public byte getType() {
        return this.potionType;
    }

    /// 是否可以右键扔出
    public boolean isThrowable() {
        return this.potionType == CppPotionHelper.SPLASH_TYPE || this.potionType == CppPotionHelper.LINGERING_TYPE;
    }

    @Override
    public List<PotionEffect> getEffects(ItemStack stack) {
        return CppPotionHelper.getEffects(stack.getItemDamage());
    }

    @Override
    public List<PotionEffect> getEffects(int damage) {
        return CppPotionHelper.getEffects(damage);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        switch (potionType) {
            case CppPotionHelper.DEFAULT_TYPE:
                this.icon = register.registerIcon(this.getIconString() + "_bottle_drinkable");
                break;
            case CppPotionHelper.SPLASH_TYPE:
                this.icon = register.registerIcon(this.getIconString() + "_bottle_splash");
                break;
            case CppPotionHelper.LINGERING_TYPE:
                this.icon = register.registerIcon(Cauldronpp.MODID + ":lingering_potion");
                break;
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return this.icon;
    }

    @Override
    public int getColorFromDamage(int damage) {
        return CppPotionHelper.getPotionColor(damage);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int potionData = stack.getItemDamage();
        String translation;
        switch (this.potionType) {
            case CppPotionHelper.SPLASH_TYPE:
                translation = "item.cauldronpp.splash_potion";
                break;
            case CppPotionHelper.LINGERING_TYPE:
                translation = "item.cauldronpp.lingering_potion";
                break;
            default:
                translation = "item.cauldronpp.potion";
        }
        return StatCollector.translateToLocalFormatted(translation,
            StatCollector.translateToLocal(CppPotionHelper.getPotionPrefixTranslationKey(potionData)));
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
            infoList.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal(this.isThrowable() ?
                "item.cauldronpp.potion.description.when_use" : "potion.effects.whenDrank"));

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
    public boolean isEffectInstant(int damage) {
        List<PotionEffect> effects = CppPotionHelper.getEffects(damage);
        for (PotionEffect effect : effects) {
            if (!Potion.potionTypes[effect.getPotionID()].isInstant()) return false;
        }
        return !effects.isEmpty();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (this.isThrowable()) {
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }

            world.playSoundAtEntity(player, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));

            if (!world.isRemote) {
                world.spawnEntityInWorld(new CppPotionEntity(world, player, stack));
            }
        } else {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        if (tab == this.getCreativeTab()) {
            if (CauldronppItems.CPP_LINGERING_POTION.equals(item) && !CauldronppConfig.enableLingeringPotion) return;
            items.add(new ItemStack(item, 1, 32767));
            items.add(new ItemStack(item, 1, 16123));
            items.add(new ItemStack(item, 1, 16085));
            items.add(new ItemStack(item, 1, 22809));
        }
    }
}
