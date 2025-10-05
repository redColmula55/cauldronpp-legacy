package rc55.mc.cauldronpp.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.tileEntity.CppCauldronTileEntity;

public class CppCauldronTileEntityRenderer extends TileEntitySpecialRenderer {

    private static final ModelCppCauldronLiquid LIQUID_MODEL = new ModelCppCauldronLiquid();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f1) {
        if (tileEntity instanceof CppCauldronTileEntity) this.renderTileEntityAt(((CppCauldronTileEntity) tileEntity), x, y, z, f1);
    }

    private void renderTileEntityAt(CppCauldronTileEntity cauldron, double x, double y, double z, float f1) {
        Block block = cauldron.getBlockType();
        if (!cauldron.isEmpty() && block == CauldronppBlocks.CAULDRON) {

            this.bindTexture(cauldron.getLiquidType().getTexture());

            Tessellator tessellator = Tessellator.instance;
            GL11.glPushMatrix();
            GL11.glDepthMask(false);

            if (cauldron.getLiquidType().shouldRenderColor()) {
                int color = cauldron.getFluidRenderColor();
                float r = ((color >> 16) & 0xff) / 255.0f;
                float g = ((color >> 8) & 0xff) / 255.0f;
                float b = (color & 0xff) / 255.0f;
                GL11.glColor4f(r, g, b, 1.0f);
            }

            float f = 0.6666667F;
            GL11.glTranslatef((float) x + 0.5F, (float) (y + cauldron.getRenderFluidHeight()), (float) z + 0.5F);
            GL11.glScalef(-f, -f, -f * 2);

            LIQUID_MODEL.render();

            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }
}
