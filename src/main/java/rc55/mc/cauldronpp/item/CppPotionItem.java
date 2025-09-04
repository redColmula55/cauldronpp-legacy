package rc55.mc.cauldronpp.item;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.api.CppPotionHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CppPotionItem extends ItemPotion {

    private final boolean splash;
    private IIcon icon;
    private IIcon overlayIcon;

    public CppPotionItem(boolean splash) {
        this.splash = splash;
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setTextureName("potion");
        this.setCreativeTab(CreativeTabs.tabBrewing);
        this.setUnlocalizedName("potion");
    }

    public boolean isSplash() {
        return this.splash;
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
        this.icon = register.registerIcon(this.getIconString() + "_" + (this.splash ? "bottle_splash" : "bottle_drinkable"));
        this.overlayIcon = register.registerIcon(this.getIconString() + "_" + "overlay");
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
        return StatCollector.translateToLocalFormatted(this.splash ? "item.cauldronpp.splash_potion" : "item.cauldronpp.potion",
            StatCollector.translateToLocal(CppPotionHelper.getPotionPrefixTranslationKey(potionData)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean p_77624_4_) {
        List<PotionEffect> list1 = this.getEffects(stack);
        HashMultimap hashmultimap = HashMultimap.create();
        Iterator iterator1;

        if (list1 != null && !list1.isEmpty()) {
            iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                PotionEffect potioneffect = (PotionEffect) iterator1.next();
                String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                Map map = potion.func_111186_k();

                if (map != null && map.size() > 0) {
                    for (Object o : map.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.func_111183_a(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        hashmultimap.put(((IAttribute) entry.getKey()).getAttributeUnlocalizedName(), attributemodifier1);
                    }
                }

                if (potioneffect.getAmplifier() > 0) {
                    s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                }

                if (potioneffect.getDuration() > 20) {
                    s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
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

        if (!hashmultimap.isEmpty()) {
            infoList.add("");
            infoList.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
            iterator1 = hashmultimap.entries().iterator();

            while (iterator1.hasNext()) {
                Map.Entry entry1 = (Map.Entry) iterator1.next();
                AttributeModifier attributemodifier2 = (AttributeModifier) entry1.getValue();
                double d0 = attributemodifier2.getAmount();
                double d1;

                if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    infoList.add(
                        EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[]{ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + (String) entry1.getKey())}));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    infoList.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[]{ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + (String) entry1.getKey())}));
                }
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (this.splash) {
            if (!player.capabilities.isCreativeMode) {
                --itemStackIn.stackSize;
            }

            worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(new EntityPotion(worldIn, player, itemStackIn));
            }
        } else {
            player.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }
        return itemStackIn;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        if (tab == this.getCreativeTab()) {
            items.add(new ItemStack(item, 1, 32767));
            items.add(new ItemStack(item, 1, 16123));
            items.add(new ItemStack(item, 1, 81621));
            items.add(new ItemStack(item, 1, 55577));
        }
    }
}
