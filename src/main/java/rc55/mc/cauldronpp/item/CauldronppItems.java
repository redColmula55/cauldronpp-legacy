package rc55.mc.cauldronpp.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.CauldronppConfig;
import rc55.mc.cauldronpp.api.CppPotionHelper;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

public class CauldronppItems {

    public static final Item WATER_BOTTLE = register("water_bottle", new WaterBottleItem().setContainerItem(Items.glass_bottle));
    public static final Item CPP_POTION = register("potion", new CppPotionItem(CppPotionHelper.DEFAULT_TYPE).setContainerItem(Items.glass_bottle));
    public static final Item CPP_SPLASH_POTION = register("splash_potion", new CppPotionItem(CppPotionHelper.SPLASH_TYPE).setContainerItem(Items.glass_bottle));
    public static final Item CPP_LINGERING_POTION = register("lingering_potion", new CppPotionItem(CppPotionHelper.LINGERING_TYPE).setContainerItem(Items.glass_bottle));
    public static final Item CPP_TIPPED_ARROW = register("tipped_arrow", new TippedArrowItem());
    public static final Item LINGERING_POTION_FERTILIZER = register("lingering_potion_fertilizer",
        new DescrptiveItem("lingering_potion_fertilizer").setDescription("desc").setCreativeTab(CauldronppConfig.enableLingeringPotion ? CreativeTabs.tabBrewing : null));

    private static Item register(String name, Item item) {
        return GameRegistry.registerItem(item, name, Cauldronpp.MODID);
    }

    public static void regItems() {
        addRecipes();

        ItemDispenserBehavior.registerItemDispenserBehavior();
        Cauldronpp.LOGGER.info("Cauldron++ items added.");
    }

    //合成配方
    private static void addRecipes() {
        GameRegistry.addRecipe(new ItemStack(CauldronppBlocks.CAULDRON),
            "a a",
            "aba",
            "aaa",
            'a', Items.iron_ingot,
            'b', Items.brewing_stand);
        GameRegistry.addShapelessRecipe(new ItemStack(CauldronppBlocks.CAULDRON), Items.brewing_stand, Items.cauldron);

        GameRegistry.addShapelessRecipe(new ItemStack(LINGERING_POTION_FERTILIZER), WATER_BOTTLE, Item.getItemFromBlock(Blocks.end_stone), Items.ender_eye);
    }
}
