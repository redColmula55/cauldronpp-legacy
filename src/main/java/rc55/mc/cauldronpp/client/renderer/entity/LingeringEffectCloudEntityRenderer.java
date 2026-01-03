package rc55.mc.cauldronpp.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * 占位用
 * 用于禁止默认渲染的模型
 */
public class LingeringEffectCloudEntityRenderer extends Render {
    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float pitch) {
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return new ResourceLocation("");
    }
}
