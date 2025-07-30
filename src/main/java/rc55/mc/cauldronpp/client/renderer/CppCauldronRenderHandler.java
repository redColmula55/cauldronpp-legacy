package rc55.mc.cauldronpp.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.block.CppCauldronBlock;
import rc55.mc.cauldronpp.tileEntity.CppCauldronTileEntity;

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

        IIcon inner = block.getIcon(2, metadata);
        float f6 = 0.125F;
        IIcon top = BlockCauldron.getCauldronIcon("inner");
        tessellator.startDrawingQuads();
        renderer.renderFaceXPos(block, -1.0 + f6, 0.0, 0.0, inner);
        renderer.renderFaceXNeg(block, 1.0F - f6, 0.0, 0.0, inner);
        renderer.renderFaceZPos(block, 0.0, 0.0, -1.0 + f6, inner);
        renderer.renderFaceZNeg(block, 0.0, 0.0, 1.0F - f6, inner);
        renderer.renderFaceYPos(block, 0.0, -1.0F + 0.25F, 0.0, top);
        renderer.renderFaceYNeg(block, 0.0, 1.0F - 0.75F, 0.0, top);
        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    //方块渲染
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        if (world.getBlock(x, y, z) instanceof CppCauldronBlock) {
            renderer.renderStandardBlock(Blocks.cauldron, x, y, z);

            CppCauldronBlock cauldronBlock = (CppCauldronBlock) block;
            CppCauldronTileEntity tileEntity;
            if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof CppCauldronTileEntity) {
                tileEntity = (CppCauldronTileEntity) world.getTileEntity(x, y, z);
            } else {
                Cauldronpp.LOGGER.error("Failed to render cauldronpp:cauldron at {}, {}, {}. Cause: No tile entity found!", x, y, z);
                return false;
            }
            Tessellator tessellator = Tessellator.instance;
            tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

            IIcon iicon1 = Blocks.cauldron.getBlockTextureFromSide(2);
            float f4 = 0.125F;
            renderer.renderFaceXPos(block, (float) x - 1.0F + f4, y, z, iicon1);
            renderer.renderFaceXNeg(block, (float) x + 1.0F - f4, y, z, iicon1);
            renderer.renderFaceZPos(block, x, y, (float) z - 1.0F + f4, iicon1);
            renderer.renderFaceZNeg(block, x, y, (float) z + 1.0F - f4, iicon1);
            IIcon iicon2 = BlockCauldron.getCauldronIcon("inner");
            renderer.renderFaceYPos(block, x, (float) y - 1.0F + 0.25F, z, iicon2);
            renderer.renderFaceYNeg(block, x, (float) y + 1.0F - 0.75F, z, iicon2);

            int l = tileEntity.getFluidRenderColor();
            float r = (float) (l >> 16 & 255) / 255.0F;
            float g = (float) (l >> 8 & 255) / 255.0F;
            float b = (float) (l & 255) / 255.0F;

            if (EntityRenderer.anaglyphEnable) {
                float f3 = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
                f4 = (r * 30.0F + g * 70.0F) / 100.0F;
                float f5 = (r * 30.0F + b * 70.0F) / 100.0F;
                r = f3;
                g = f4;
                b = f5;
            }

            tessellator.setColorOpaque_F(r, g, b);
            tessellator.setBrightness(15);

            int level = tileEntity.getLiquidLevel();
            if (!tileEntity.isEmpty()) {
//                IIcon liquidIcon = this.iconMap.getOrDefault(tileEntity.getLiquidType(), null);
//                if (liquidIcon != null) {
//                    renderer.renderFaceYPos(cauldronBlock, x, (float)y - 1.0F + BlockCauldron.getRenderLiquidLevel(level), z, liquidIcon);
//                }
            }
            return true;
        } else {
            Cauldronpp.LOGGER.error("Failed to render cauldronpp:cauldron at {}, {}, {}. Cause: Not a cauldron!", x, y, z);
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
