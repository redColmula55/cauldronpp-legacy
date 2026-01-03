package rc55.mc.cauldronpp.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.CauldronppConfig;
import rc55.mc.cauldronpp.api.CppPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * 滞留药水产生的效果云雾
 * @see rc55.mc.cauldronpp.item.CppPotionItem
 */
public class LingeringEffectCloudEntity extends Entity implements IEntityAdditionalSpawnData {

    /// 默认直径（大小）
    public static final float DEFAULT_SIZE = 6.0f;
    /// 最小直径
    public static final float MIN_SIZE = 0.9f;

    public static final int SIZE_FLAG = 10;

    /// 粒子效果的密度，越小越密
    public static final double PARTICLE_DENSITY = 0.3;

    /// 固定碰撞箱高度
    public static final float HEIGHT = 0.5F;

    private Entity owner = null;
    private @Nonnull String ownerName = "";

    private double fixedPosY;

    /// 药水数据值，用于计算效果
    private int damage = 0;
    private List<PotionEffect> effects = new ArrayList<>();

    public LingeringEffectCloudEntity(World world) {
        super(world);
        this.yOffset = 0;
        this.setSize(DEFAULT_SIZE, HEIGHT);
        //禁用就移除
        if (!CauldronppConfig.enableLingeringPotion) this.setDead();
    }

    public LingeringEffectCloudEntity(World world, double x, double y, double z, int damage) {
        this(world);
        this.setPosition(x, y, z);
        this.fixedPosY = y;
        this.damage = damage;
        this.effects = CppPotionHelper.getEffects(this.damage);
    }

    public LingeringEffectCloudEntity(World world, Entity owner, double x, double y, double z, int damage) {
        this(world, x, y, z, damage);
        this.owner = owner;
        if (owner instanceof EntityPlayer) this.ownerName = owner.getCommandSenderName();
    }

    public LingeringEffectCloudEntity(CppPotionEntity potion) {
        this(potion.worldObj, potion.getThrower(), potion.posX, potion.posY, potion.posZ, potion.getPotionDamage());
    }

    /// 当前大小
    public float getSize() {
        return this.dataWatcher.getWatchableObjectFloat(SIZE_FLAG);
    }

    public int getDamage() {
        return this.damage;
    }

    /// 包含的药水效果，可为空
    public List<PotionEffect> getEffects() {
        return this.effects;
    }

    /// 颜色
    public int getColor() {
        return CppPotionHelper.getPotionColor(this.getDamage());
    }

    /// 设置大小
    public LingeringEffectCloudEntity setSize(float size) {
        this.dataWatcher.updateObject(SIZE_FLAG, size);
        this.setSize(size, HEIGHT);
        return this;
    }
    public void decreaseSize(float size) {
        this.setSize(this.getSize() - size);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(SIZE_FLAG, DEFAULT_SIZE);
    }

    @Override
    public void onUpdate() {
        super.onEntityUpdate();

        if (this.posY != this.fixedPosY) {//卡进方块会被莫名其妙会被抬高一格不知道为什么屏蔽移动也不行只能这么写了真的离谱不过会导致碰撞箱抽搐不过懒得改了（
            this.setPosition(this.posX, this.fixedPosY, this.posZ);
        }

        //粒子效果
        this.spawnParticles();

        if (!this.worldObj.isRemote) {
            //每秒尝试给周围生物效果
            if (this.worldObj.getWorldTime() % 20 == 0) {
                List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox);
                for (EntityLivingBase entity : list) {
                    for (PotionEffect effect : this.getEffects()) {
                        entity.addPotionEffect(effect);
                    }
                }
                //应用效果后减少半径
                if (!list.isEmpty() && !this.effects.isEmpty()) {
                    this.decreaseSize(0.5f);
                }
            }
        }

        //自身大小不断缩小
        this.decreaseSize(0.0085f);
        if (this.getSize() < MIN_SIZE) this.kill();
        this.setSize(this.getSize(), HEIGHT);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.fixedPosY = nbt.getDouble("FixedY");
        this.setSize(nbt.getFloat("Size"));
        this.ownerName = nbt.getString("Owner");
        this.damage = nbt.getInteger("Damage");
        this.effects = CppPotionHelper.getEffects(this.damage);
    }
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setDouble("FixedY", this.fixedPosY);
        nbt.setFloat("Size", this.getSize());
        if (this.owner instanceof EntityPlayer && this.ownerName.isEmpty()) {
            this.ownerName = this.owner.getCommandSenderName();
        }
        nbt.setString("Owner", this.ownerName);
        nbt.setInteger("Damage", this.damage);
    }

    @Override
    public boolean canBePushed() {
        return true;
    }
    @Override
    public void applyEntityCollision(Entity entity) {
    }

    //固定位置
    @Override
    public void moveEntity(double x, double y, double z) {
    }
    @Override
    public void addVelocity(double x, double y, double z) {
    }
    //禁用火焰渲染
    @Override
    public boolean canRenderOnFire() {
        return false;
    }
    //禁用方块碰撞事件
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }
    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }
    //禁用脚步声  方法名 playStepSound
    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
    }

    /// 显示的粒子效果
    public void spawnParticles() {
        if ((this.worldObj.getWorldTime() & 7) != 0) return;

        final double radius = this.getSize() / 2.0;
        final float r = ((this.getColor() >> 16) & 255) / 255.0f;
        final float g = ((this.getColor() >> 8) & 255) / 255.0f;
        final float b = (this.getColor() & 255) / 255.0f;

        for (double x = this.posX - radius + PARTICLE_DENSITY; x < this.posX + radius - PARTICLE_DENSITY; x += PARTICLE_DENSITY) {
            for (double z = this.posZ - radius + PARTICLE_DENSITY; z < this.posZ + radius - PARTICLE_DENSITY; z += PARTICLE_DENSITY) {
                this.worldObj.spawnParticle("mobSpell", x + MathHelper.clamp_double(this.rand.nextGaussian(), -0.2, 0.2),
                    this.posY, z + MathHelper.clamp_double(this.rand.nextGaussian(), -0.2, 0.2), r, g, b);
            }
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(this.damage);
        buf.writeDouble(this.fixedPosY);
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.damage = buf.readInt();
        this.fixedPosY = buf.readDouble();
    }
}
