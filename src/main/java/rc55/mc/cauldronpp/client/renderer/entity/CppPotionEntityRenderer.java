package rc55.mc.cauldronpp.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import rc55.mc.cauldronpp.entity.CppPotionEntity;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class CppPotionEntityRenderer extends Render {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float pitch) {
        CppPotionEntity potion = (CppPotionEntity) entity;
        IIcon icon = potion.getBottleRenderIcon();
        if (icon != null) {
            GL11.glPushMatrix();

            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            this.bindEntityTexture(potion);
            Tessellator tessellator = Tessellator.instance;

            //内容物
            int i = potion.getRenderColor();
            float r = (float)(i >> 16 & 255) / 255.0f;
            float g = (float)(i >> 8 & 255) / 255.0f;
            float b = (float)(i & 255) / 255.0f;
            GL11.glColor3f(r, g, b);
            GL11.glPushMatrix();
            this.render(tessellator, Objects.requireNonNull(ItemPotion.func_94589_d("overlay"), "Internal error"));
            GL11.glPopMatrix();

            //瓶子
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            this.render(tessellator, icon);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);

            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TextureMap.locationItemsTexture;
    }

    private void render(Tessellator tessellator, IIcon icon) {
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 1.0f;
        float f5 = 0.5f;
        float f6 = 0.25f;

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 1.0f, 0.0f);

        tessellator.addVertexWithUV(0.0 - f5, 0.0 - f6, 0.0, f, f3);
        tessellator.addVertexWithUV(f4 - f5, 0.0 - f6, 0.0, f1, f3);
        tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0, f1, f2);
        tessellator.addVertexWithUV(0.0 - f5, f4 - f6, 0.0, f, f2);

        tessellator.draw();
    }
}
