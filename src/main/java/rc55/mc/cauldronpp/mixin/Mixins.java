package rc55.mc.cauldronpp.mixin;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

import javax.annotation.Nonnull;

public enum Mixins implements IMixins {
    EARLY(new MixinBuilder("Cauldron++ Early Mixin")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> true)
        .addCommonMixins("EntityPotionMixin", "ItemAccessor", "ItemGlassBottleMixin"));

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
