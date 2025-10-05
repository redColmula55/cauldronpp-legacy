package rc55.mc.cauldronpp.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.api.CppCauldronBehavior;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.api.Utils;
import rc55.mc.cauldronpp.tileEntity.CppCauldronTileEntity;

import java.util.Random;

public class CppCauldronBlock extends BlockCauldron implements ITileEntityProvider {

    public CppCauldronBlock() {
        super();
        this.isBlockContainer = true;
        this.setHardness(2.0F);
        this.setBlockTextureName("cauldron");
        this.setBlockName(Cauldronpp.MODID + ".cauldron");
        this.setCreativeTab(CreativeTabs.tabBrewing);
        this.setTickRandomly(true);
    }

    //根据metadata计算光照
    //meta=0时为0, meta=1时为15
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) == 0 ? super.getLightValue(world, x, y, z) : 15;
    }

    //方块实体相关
    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        super.breakBlock(world, x, y, z, blockBroken, meta);
        world.removeTileEntity(x, y, z);
    }
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventData) {
        super.onBlockEventReceived(world, x, y, z, eventId, eventData);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity != null && tileEntity.receiveClientEvent(eventId, eventData);
    }
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new CppCauldronTileEntity();
    }

    //渲染
    @Override
    public int getRenderType() {
        return Block.getIdFromBlock(this);
    }
    @Override
    public boolean renderAsNormalBlock() {
        return super.renderAsNormalBlock();
    }
    @Override
    @SideOnly(Side.CLIENT)
    public String getTextureName() {
        return this.textureName == null ? "MISSING_ICON_BLOCK_" + getIdFromBlock(Blocks.cauldron) + "_" + super.getUnlocalizedName() : this.textureName;
    }

    //物品形式
    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock(this);
    }

    //比较器输出
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        if (world.getTileEntity(x, y, z) instanceof CppCauldronTileEntity) {
            CppCauldronTileEntity tileEntity = (CppCauldronTileEntity) world.getTileEntity(x, y, z);
            return tileEntity.isEmpty() ? 0 : tileEntity.getLiquidLevel() + (tileEntity.getLiquidType().getId() - 1) * 3;
        }
        return 0;
    }

    //右键交互
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (world.isRemote) {
            world.markAndNotifyBlock(x, y, z, world.getChunkFromBlockCoords(x, z), this, this, 2);
            return true;
        } else {
            if (world.getTileEntity(x, y, z) instanceof CppCauldronTileEntity) {
                CppCauldronTileEntity cauldron = (CppCauldronTileEntity) world.getTileEntity(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);
                return CppCauldronBehavior.behaviorMap.getOrDefault(Utils.getItemFromStack(player.getHeldItem()), CppCauldronBehavior.EMPTY)
                    .interact(world, x, y, z, meta, cauldron, player, player.getHeldItem());
            } else return super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);
        }
    }

    //实体锅内行为
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (world.getTileEntity(x, y, z) instanceof CppCauldronTileEntity) {
            CppCauldronTileEntity cauldron = (CppCauldronTileEntity) world.getTileEntity(x, y, z);
            int level = cauldron.getLiquidLevel();
            float liquidHeight = (float) y + (6.0F + (float) (3 * level)) / 16.0F;

            if (!world.isRemote && level > 0 && entity.boundingBox.minY <= (double) liquidHeight) {
                if ((cauldron.isWater() || cauldron.getLiquidType() == CppCauldronLiquidType.COLORED_WATER) && entity.isBurning()) {//灭火
                    entity.extinguish();
                    cauldron.decrease(1);
                    CppCauldronBehavior.update(world, x, y, z, 0, cauldron);
                } else if (cauldron.getLiquidType() == CppCauldronLiquidType.LAVA) {
                    if (!entity.isImmuneToFire()) {
                        entity.attackEntityFrom(DamageSource.lava, 4.0F);
                        entity.setFire(15);
                    }
                }
            }
        }
    }

    //下雨时逐渐填满
    @Override
    public void fillWithRain(World world, int x, int y, int z) {
        if (world.rand.nextInt(20) == 1) {
            if (world.getTileEntity(x, y, z) instanceof CppCauldronTileEntity) {
                CppCauldronTileEntity cauldron = (CppCauldronTileEntity) world.getTileEntity(x, y, z);
                if (cauldron.isEmpty()) cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                if (cauldron.canIncrease(1)) cauldron.increase(1);
            }
        }
    }
}
