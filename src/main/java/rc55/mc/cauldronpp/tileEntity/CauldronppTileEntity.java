package rc55.mc.cauldronpp.tileEntity;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.client.renderer.CppCauldronRenderHandler;

public class CauldronppTileEntity {

    private static void register(Class<? extends TileEntity> tileEntityClass, String name) {
        GameRegistry.registerTileEntity(tileEntityClass, name);
    }

    private static void register(Class<? extends TileEntity> tileEntityClass, String name, ISimpleBlockRenderingHandler blockRenderingHandler) {
        register(tileEntityClass, name);
        RenderingRegistry.registerBlockHandler(blockRenderingHandler);
    }

    public static void regTileEntity() {
        register(CppCauldronTileEntity.class, "CppCauldron", new CppCauldronRenderHandler());
        Cauldronpp.LOGGER.info("Cauldron++ tile entity registered.");
    }
}
