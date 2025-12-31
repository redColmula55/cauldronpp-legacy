package rc55.mc.cauldronpp;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.item.CauldronppItems;
import rc55.mc.cauldronpp.proxy.IModProxy;
import rc55.mc.cauldronpp.tileEntity.CauldronppTileEntity;

@Mod(modid = Cauldronpp.MODID, version = Cauldronpp.VERSION, useMetadata = true)
public class Cauldronpp {
    public static final String MODID = "cauldronpp";
    public static final String VERSION = "1.0.1-1.7.10";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Instance
    public static Cauldronpp INSTANCE;

    @SidedProxy(
        modId = MODID,
        serverSide = "rc55.mc.cauldronpp.proxy.CommonProxy",
        clientSide = "rc55.mc.cauldronpp.proxy.ClientProxy"
    )
    public static IModProxy PROXY;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CauldronppItems.regItems();
        CauldronppBlocks.regBlocks();
        CauldronppTileEntity.regTileEntity();

        PROXY.clientRenderTile();

        LOGGER.info("Cauldron++ init complete.");
    }
}
