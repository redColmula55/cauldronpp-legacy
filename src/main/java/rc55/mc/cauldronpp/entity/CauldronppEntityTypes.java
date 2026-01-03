package rc55.mc.cauldronpp.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppEntityTypes {

    private static void register(Class<? extends Entity> entity, String name, int trackRange, int updateFrequency, boolean tickVelocity) {
        int id = EntityRegistry.findGlobalUniqueEntityId();
        name = Cauldronpp.MODID + "_" + name;
        EntityRegistry.registerGlobalEntityID(entity, name ,id);
        EntityRegistry.registerModEntity(entity, name, id, Cauldronpp.instance, trackRange, updateFrequency, tickVelocity);
        Cauldronpp.LOGGER.debug("Register entity {} with id {}", name, id);
    }

    public static void regEntity() {
        register(TippedArrowEntity.class, "TippedArrow", 64, 10, true);
        register(CppPotionEntity.class, "CppThrownPotion", 48, 10, true);
        register(LingeringEffectCloudEntity.class, "LingeringPotionEffectCloud", 64, 1, false);
        Cauldronpp.LOGGER.info("Cauldron++ entity registered.");
    }
}
