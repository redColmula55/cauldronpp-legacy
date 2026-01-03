package rc55.mc.cauldronpp;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * 服务端侧
 */
@SuppressWarnings("unused")
public class CauldronppCommonProxy {

    public boolean isClient() {
        return false;
    }

    public void onPreInit(FMLPreInitializationEvent event) {
    }

    public void onInit(FMLInitializationEvent event) {
    }

    public void onPostInit(FMLPostInitializationEvent event) {
    }
}
