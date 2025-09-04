package rc55.mc.cauldronpp.mixin.late;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@LateMixin
public class LateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.cauldronpp.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return Collections.emptyList();
    }
}
