package rc55.mc.cauldronpp;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.client.renderer.CppCauldronTileEntityRenderer;
import rc55.mc.cauldronpp.item.CauldronppItems;
import rc55.mc.cauldronpp.tileEntity.CauldronppTileEntity;
import rc55.mc.cauldronpp.tileEntity.CppCauldronTileEntity;

@Mod(modid = Cauldronpp.MODID, version = Cauldronpp.VERSION)
public class Cauldronpp {

    public static final String MODID = "cauldronpp";
    public static final String VERSION = "0.0.1-1.7.10";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {}

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CauldronppItems.regItems();
        CauldronppBlocks.regBlocks();
        CauldronppTileEntity.regTileEntity();

        ClientRegistry.bindTileEntitySpecialRenderer(CppCauldronTileEntity.class, new CppCauldronTileEntityRenderer());
        LOGGER.info("Cauldron++ init complete.");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    @EventHandler
    public void client(FMLClientHandler handler) {}
}
