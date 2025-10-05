package rc55.mc.cauldronpp.tileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import rc55.mc.cauldronpp.api.CppCauldronBehavior;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.api.CppPotionHelper;

public class CppCauldronTileEntity extends TileEntity {

    private CppCauldronLiquidType liquidType = CppCauldronLiquidType.NONE;
    private byte potionType;
    private int liquidLevel;
    private int liquidData;

    public CppCauldronTileEntity() {}

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.liquidType = CppCauldronLiquidType.byId(compound.getByte("LiquidType"));
        this.potionType = compound.getByte("PotionType");
        this.liquidLevel = compound.getInteger("LiquidLevel");
        this.liquidData = compound.getInteger("LiquidData");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setByte("LiquidType", this.liquidType.getId());
        compound.setByte("PotionType", this.potionType);
        compound.setInteger("LiquidLevel", this.liquidLevel);
        compound.setInteger("LiquidData", this.liquidData);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), nbt);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
    }

    public CppCauldronLiquidType getLiquidType() {
        return this.liquidType;
    }

    public byte getPotionType() {
        return this.potionType;
    }

    public int getLiquidLevel() {
        return this.liquidLevel;
    }

    public int getLiquidData() {
        return this.liquidData;
    }

    @SideOnly(Side.CLIENT)
    public int getFluidRenderColor() {
        switch (this.liquidType) {
            case WATER:
                return 0x3F76E4;//Blocks.water.colorMultiplier(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            case POTION:
                return CppPotionHelper.getPotionColor(this.liquidData);
            case COLORED_WATER:
                return this.liquidData;
            default:
                return 16777215;
        }
    }

    public double getRenderFluidHeight() {
        return this.liquidLevel * 0.1875 + 0.375;
    }

    public void setLiquidType(CppCauldronLiquidType type) {
        this.liquidType = type;
    }

    public void setPotionType(byte type) {
        this.potionType = type;
    }

    public void setLiquidLevel(int level) {
        this.liquidLevel = level;
    }

    public void setLiquidData(int data) {
        this.liquidData = data;
    }

    public boolean applyMaterial(String material) {
        if (!this.canBrew()) return false;
        int newData = CppPotionHelper.applyMaterial(this.getLiquidData(), material);
        if (newData != this.liquidData) {
            this.liquidData = newData;
            return true;
        } else return false;
    }

    public boolean isWater() {
        return this.liquidType == CppCauldronLiquidType.WATER;
    }

    public boolean canBrew() {
        return (this.isWater() || this.liquidType == CppCauldronLiquidType.POTION) && !this.isEmpty();
    }

    public boolean isEmpty() {
        return this.liquidLevel <= 0 || this.liquidType == CppCauldronLiquidType.NONE;
    }

    public boolean canIncrease(int amount) {
        return this.getLiquidLevel() + amount <= CppCauldronBehavior.MAX_AMOUNT;
    }

    public boolean canDecrease(int amount) {
        return this.getLiquidLevel() - amount >= 0;
    }

    public void increase(int amount) {
        this.liquidLevel = Math.min(this.liquidLevel + amount, CppCauldronBehavior.MAX_AMOUNT);
        this.markDirty();
    }

    public void decrease(int amount) {
        this.liquidLevel = Math.max(this.liquidLevel - amount, 0);
        if (this.isEmpty()) {
            this.liquidData = 0;
            this.liquidType = CppCauldronLiquidType.NONE;
        }
        this.markDirty();
    }

    public void reset() {
        this.liquidLevel = 0;
        this.liquidData = 0;
        this.liquidType = CppCauldronLiquidType.NONE;
        this.markDirty();
    }
}
