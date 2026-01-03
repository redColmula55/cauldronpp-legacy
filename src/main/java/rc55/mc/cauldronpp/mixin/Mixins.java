package rc55.mc.cauldronpp.mixin;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public enum Mixins implements IMixins {
    EARLY(new MixinBuilder("Cauldron++ Early Mixin")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> true)
        .addCommonMixins("ItemAccessor", "ItemGlassBottleMixin", "PotionAccessor"));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return this.builder;
    }
}
