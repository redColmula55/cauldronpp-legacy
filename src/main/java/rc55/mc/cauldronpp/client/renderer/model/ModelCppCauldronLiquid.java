package rc55.mc.cauldronpp.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCppCauldronLiquid extends ModelBase {

    public final ModelRenderer liquid;

    public ModelCppCauldronLiquid() {
        this.liquid = new ModelRenderer(this, 0, 0);

        this.liquid.addBox(-8.0f, 0.0f, -4.0f, 16, 0, 8, 0);
        this.liquid.setTextureOffset(0, 0);
        this.liquid.setTextureSize(16, 16);
    }

    public void render() {
        this.liquid.render(0.0625f);
    }
}
