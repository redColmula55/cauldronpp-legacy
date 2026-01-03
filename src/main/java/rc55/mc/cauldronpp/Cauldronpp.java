package rc55.mc.cauldronpp;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.entity.CauldronppEntityTypes;
import rc55.mc.cauldronpp.event.CauldronppEvents;
import rc55.mc.cauldronpp.item.CauldronppItems;
import rc55.mc.cauldronpp.tileEntity.CauldronppTileEntity;

@Mod(modid = Cauldronpp.MODID, version = Cauldronpp.VERSION, dependencies = "required-after:unimixins")
public class Cauldronpp {

    public static final String MODID = "cauldronpp";
    public static final String VERSION = "1.1.0-1.7.10";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static Cauldronpp instance;

    @SidedProxy(modId = MODID, clientSide = "rc55.mc.cauldronpp.client.CauldronppClientProxy", serverSide = "rc55.mc.cauldronpp.CauldronppCommonProxy")
    public static CauldronppCommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CauldronppConfig.CONFIG_BASE.load();
        proxy.onPreInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CauldronppItems.regItems();
        CauldronppBlocks.regBlocks();
        CauldronppTileEntity.regTileEntity();
        CauldronppEntityTypes.regEntity();
        CauldronppEvents.register();

        proxy.onInit(event);

        LOGGER.info("Cauldron++ init complete.");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.onPostInit(event);
    }
}
