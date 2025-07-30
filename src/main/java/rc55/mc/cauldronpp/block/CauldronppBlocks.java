package rc55.mc.cauldronpp.block;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppBlocks {

    public static final Block CAULDRON = register("cauldron", new CppCauldronBlock());

    private static Block register(String name, Block block) {
        return GameRegistry.registerBlock(block, name);
    }

    public static void regBlocks() {
        Cauldronpp.LOGGER.info("Cauldron++ blocks registered.");
    }
}
