package rc55.mc.cauldronpp.mixin.early;

import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Potion.class)
public interface PotionAccessor {
    @Accessor("isBadEffect")
    boolean getIsBadEffect();
}
