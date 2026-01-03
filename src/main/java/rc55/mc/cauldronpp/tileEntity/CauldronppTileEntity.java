package rc55.mc.cauldronpp.tileEntity;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.tileentity.TileEntity;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppTileEntity {

    private static void register(Class<? extends TileEntity> tileEntityClass, String name) {
        GameRegistry.registerTileEntity(tileEntityClass, name);
    }

    public static void regTileEntity() {
        register(CppCauldronTileEntity.class, "CppCauldron");
        Cauldronpp.LOGGER.info("Cauldron++ tile entity registered.");
    }
}
