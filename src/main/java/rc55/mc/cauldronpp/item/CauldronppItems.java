package rc55.mc.cauldronpp.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

public class CauldronppItems {

    public static final Item WATER_BOTTLE = register("water_bottle", new WaterBottleItem());
    public static final Item CPP_POTION = register("potion", new CppPotionItem(false));
    public static final Item CPP_SPLASH_POTION = register("splash_potion", new CppPotionItem(true));

    private static Item register(String name, Item item) {
        return GameRegistry.registerItem(item, name, Cauldronpp.MODID);
    }

    public static void regItems() {
        addRecipes();
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
    }
}
