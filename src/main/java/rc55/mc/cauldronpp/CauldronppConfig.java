package rc55.mc.cauldronpp;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class CauldronppConfig extends Configuration {

    /// 是否启用滞留药水
    public static boolean enableLingeringPotion;

    /// 是否启用药箭
    public static boolean enableTippedArrow;

    public static final Configuration CONFIG_BASE = new CauldronppConfig();

    public CauldronppConfig() {
        super(new File(Loader.instance().getConfigDir(), Cauldronpp.MODID + ".cfg"));
        this.syncConfig();
    }

    /// 同步新配置/价值默认配置
    public void syncConfig() {
        enableLingeringPotion = this.getBoolean("lingeringPotion", "items", false, "Whether to enable lingering potions");
        enableTippedArrow = this.getBoolean("tippedArrow", "items", false, "Whether to enable tipped arrows");

        Cauldronpp.LOGGER.info("Synced Cauldron++ configs.");

        this.save();
    }

    @Override
    public void load() {
        super.load();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent event) {
        if (Cauldronpp.MODID.equals(event.modID)) {
            this.syncConfig();
        }
    }
}
