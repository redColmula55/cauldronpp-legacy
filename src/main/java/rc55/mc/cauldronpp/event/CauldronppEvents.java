package rc55.mc.cauldronpp.event;

import net.minecraftforge.common.MinecraftForge;

public class CauldronppEvents {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new BowEvents());
    }
}
