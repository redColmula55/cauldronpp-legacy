package rc55.mc.cauldronpp.client.renderer.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.block.CppCauldronBlock;

public class CppCauldronRenderHandler implements ISimpleBlockRenderingHandler {

    //物品渲染
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

        Tessellator tessellator = Tessellator.instance;
        block.setBlockBoundsForItemRender();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
        tessellator.draw();

        IIcon innerSide = block.getIcon(2, metadata);
        float f6 = 0.125F;
        IIcon innerBottom = BlockCauldron.getCauldronIcon("inner");
        tessellator.startDrawingQuads();
        renderer.renderFaceXPos(block, -1.0 + f6, 0.0, 0.0, innerSide);
        renderer.renderFaceXNeg(block, 1.0F - f6, 0.0, 0.0, innerSide);
        renderer.renderFaceZPos(block, 0.0, 0.0, -1.0 + f6, innerSide);
        renderer.renderFaceZNeg(block, 0.0, 0.0, 1.0F - f6, innerSide);
        renderer.renderFaceYPos(block, 0.0, -1.0F + 0.25F, 0.0, innerBottom);
        renderer.renderFaceYNeg(block, 0.0, 1.0F - 0.75F, 0.0, innerBottom);
        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    //方块渲染
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (world.getBlock(x, y, z) instanceof CppCauldronBlock) {
            renderer.renderStandardBlock(Blocks.cauldron, x, y, z);

            Tessellator tessellator = Tessellator.instance;
            tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

            IIcon innerSide = Blocks.cauldron.getBlockTextureFromSide(2);
            float f4 = 0.125F;
            renderer.renderFaceXPos(block, (float) x - 1.0F + f4, y, z, innerSide);
            renderer.renderFaceXNeg(block, (float) x + 1.0F - f4, y, z, innerSide);
            renderer.renderFaceZPos(block, x, y, (float) z - 1.0F + f4, innerSide);
            renderer.renderFaceZNeg(block, x, y, (float) z + 1.0F - f4, innerSide);
            IIcon innerBottom = BlockCauldron.getCauldronIcon("inner");
            renderer.renderFaceYPos(block, x, (float) y - 1.0F + 0.25F, z, innerBottom);
            renderer.renderFaceYNeg(block, x, (float) y + 1.0F - 0.75F, z, innerBottom);

            return true;
        } else {
            Cauldronpp.LOGGER.error("Failed to render block cauldronpp:cauldron at ({}, {}, {}): Wrong block!", x, y, z);
            return false;
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return CauldronppBlocks.CAULDRON.getRenderType();
    }
}
