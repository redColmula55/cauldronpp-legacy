package rc55.mc.cauldronpp.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.CauldronppConfig;
import rc55.mc.cauldronpp.api.CppPotionHelper;
import rc55.mc.cauldronpp.api.Utils;
import rc55.mc.cauldronpp.item.CauldronppItems;
import rc55.mc.cauldronpp.item.TippedArrowItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 对原版箭实体的重新实现
 * 逻辑与原版箭大体相同
 * @see net.minecraft.entity.projectile.EntityArrow
 */
@SuppressWarnings("unused")
public class TippedArrowEntity extends EntityArrow implements IProjectile, IEntityAdditionalSpawnData, IThrowableEntity {

    public static final int FLAG_CRITICAL = 16;

    private Block inBlock;
    private int inBlockData;
    private int inBlockX = -1;
    private int inBlockY = -1;
    private int inBlockZ = -1;
    private boolean inGround;
    private int ticksInGround;
    private int ticksInAir;
    private int arrowShake;

    @Nullable private ItemStack arrow;
    public int canBePickedUp;
    public Entity owner;
    private double damage = 2.0;
    private int knockbackStrength;

    public TippedArrowEntity(World world) {
        super(world);
        this.arrow = new ItemStack(Items.arrow);
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
        //禁用就移除
        if (!CauldronppConfig.enableTippedArrow) this.setDead();
    }

    public TippedArrowEntity(World world, double x, double y, double z, @Nonnull ItemStack arrow) {
        this(world);
        this.setPosition(x, y, z);
        this.yOffset = 0.0f;
        this.arrow = new ItemStack(arrow.getItem(), 1, arrow.getItemDamage());
    }

    public TippedArrowEntity(World world, EntityLivingBase owner, @Nonnull ItemStack arrow, float charge) {
        this(world);
        this.setLocationAndAngles(owner.posX, owner.posY + (double) owner.getEyeHeight(), owner.posZ, owner.rotationYaw, owner.rotationPitch);

        this.owner = owner;
        this.arrow = new ItemStack(arrow.getItem(), 1, arrow.getItemDamage());
        if (owner instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }

        this.posX -= MathHelper.cos(this.rotationYaw / 180.0f * (float)Math.PI) * 0.16f;
        this.posY -= 0.10000000149011612;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0f * (float)Math.PI) * 0.16f;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0f * (float)Math.PI);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0f * (float)Math.PI);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0f * (float)Math.PI);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, charge * 1.5f, 1.0f);
    }

    public boolean isInGround() {
        return this.inGround;
    }
    public ItemStack getArrow() {
        return this.arrow;
    }
    public boolean isCritical() {
        return this.dataWatcher.getWatchableObjectByte(FLAG_CRITICAL) == 1;
    }
    public double getDamage() {
        return this.damage;
    }
    public int getColor() {
        return CppPotionHelper.getPotionColor(Optional.ofNullable(this.arrow).orElse(new ItemStack(Items.arrow)).getItemDamage());
    }

    public int getArrowShake() {
        return this.arrowShake;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
    public void setKnockbackStrength(int strength) {
        this.knockbackStrength = strength;
    }
    public void setIsCritical(boolean critical) {
        this.dataWatcher.updateObject(FLAG_CRITICAL, (byte)(critical ? 1 : 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0f;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(FLAG_CRITICAL, (byte) 0);
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float charge, float f) {
        float f2 = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= f2;
        y /= f2;
        z /= f2;
        x += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)f;
        y += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)f;
        z += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)f;
        x *= charge;
        y *= charge;
        z *= charge;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f3 = MathHelper.sqrt_double(x * x + z * z);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, f3) * 180.0 / Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double(x * x + z * z);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, f) * 180.0 / Math.PI);
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    public void onUpdate() {
        super.onEntityUpdate();
        //粒子
        this.generateParticles();
        //更新移动向量
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0 / Math.PI);
        }
        //更新落地状态
        Block block = this.worldObj.getBlock(this.inBlockX, this.inBlockY, this.inBlockZ);
        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState(this.worldObj, this.inBlockX, this.inBlockY, this.inBlockZ);
            AxisAlignedBB blockBoundingBox = block.getCollisionBoundingBoxFromPool(this.worldObj, this.inBlockX, this.inBlockY, this.inBlockZ);
            //落地
            if (blockBoundingBox != null && blockBoundingBox.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }
        //视觉抖动
        if (this.arrowShake > 0) {
            this.arrowShake--;
        }
        //更新箭矢状态
        if (this.inGround) {//地面
            int blockMeta = this.worldObj.getBlockMetadata(this.inBlockX, this.inBlockY, this.inBlockZ);
            if (block == this.inBlock && blockMeta == this.inBlockData) {
                this.ticksInGround++;
                //一定时间后删除自身
                if (this.ticksInGround >= 1200) {
                    Cauldronpp.LOGGER.debug("Arrow {} reaching eol, deleting.", this);
                    this.setDead();
                }
            } else {//附着方块改变
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2f;
                this.motionY *= this.rand.nextFloat() * 0.2f;
                this.motionZ *= this.rand.nextFloat() * 0.2f;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {//空中/水中
            this.ticksInAir++;
            //获取攻击向量
            Vec3 hitVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 motionVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition objectPositionHit = this.worldObj.func_147447_a(hitVec, motionVec, false, true, false);
            hitVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            motionVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (objectPositionHit != null) {
                motionVec = Vec3.createVectorHelper(objectPositionHit.hitVec.xCoord, objectPositionHit.hitVec.yCoord, objectPositionHit.hitVec.zCoord);
            }
            //获取击中目标
            Entity target = null;
            double distance = 0.0;
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
                this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            for (Entity entity : list) {
                if (entity.canBeCollidedWith() && (entity != this.owner || this.ticksInAir >= 5)) {
                    final float f = 0.3f;
                    AxisAlignedBB entityHitbox = entity.boundingBox.expand(f, f, f);
                    MovingObjectPosition entityHitPos = entityHitbox.calculateIntercept(hitVec, motionVec);
                    if (entityHitPos != null) {
                        double distanceToCurrentTarget = hitVec.distanceTo(entityHitPos.hitVec);
                        if (distanceToCurrentTarget < distance || distance == 0.0) {//选取距离最近的
                            target = entity;
                            distance = distanceToCurrentTarget;
                        }
                    }
                }
            }
            //击中目标
            if (target != null) {
                objectPositionHit = new MovingObjectPosition(target);
            }
            if (objectPositionHit != null) {
                this.onHit(objectPositionHit);
            }
            //暴击粒子
            if (this.isCritical()) {
                for (int i = 0; i < 4; ++i) {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0,
                        this.posY + this.motionY * (double)i / 4.0, this.posZ + this.motionZ * (double)i / 4.0,
                        -this.motionX, -this.motionY + 0.2, -this.motionZ);
                }
            }
            //应用移动向量
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            //应用偏转量
            float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            for (this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0 / Math.PI);
                 this.rotationPitch - this.prevRotationPitch < -180.0f; this.prevRotationPitch -= 360.0f) {
                //啥也没有
            }
            while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
                this.prevRotationPitch += 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
                this.prevRotationYaw -= 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
                this.prevRotationYaw += 360.0f;
            }
            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
            //水阻力
            float motionMultiplier = 0.99f;
            final float gravity = 0.05f;
            if (this.isInWater()) {
                for (int l = 0; l < 4; ++l) {//生成水下气泡粒子
                    float f4 = 0.25f;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
                }
                motionMultiplier = 0.8f;
            }
            //灭火
            if (this.isWet()) {
                this.extinguish();
            }
            //施加阻力，移动
            this.motionX *= motionMultiplier;
            this.motionY *= motionMultiplier;
            this.motionZ *= motionMultiplier;
            this.motionY -= gravity;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        boolean pick = this.canPickUp(player);
        if (!this.worldObj.isRemote && pick) {
            if (this.canBePickedUp == 1 && !player.inventory.addItemStackToInventory(this.arrow))
                pick = false;

            if (pick) {
                this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setIntArray("PosInBlock", new int[]{this.inBlockX, this.inBlockY, this.inBlockZ});
        nbt.setInteger("InBlock", Block.getIdFromBlock(this.inBlock));
        nbt.setByte("InBlockData", (byte) this.inBlockData);
        nbt.setInteger("Life", this.ticksInGround);
        nbt.setByte("Shake", (byte) this.arrowShake);
        nbt.setByte("CanPickUpFlag", (byte) this.canBePickedUp);
        nbt.setDouble("Damage", this.damage);
        if (this.arrow != null) {
            this.arrow.writeToNBT(nbt.getCompoundTag("Arrow"));
        }
        nbt.setBoolean("InGround", this.inGround);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        int[] posInBlock = nbt.getIntArray("PosInBlock");
        if (posInBlock.length == 3) {
            this.inBlockX = posInBlock[0];
            this.inBlockY = posInBlock[1];
            this.inBlockZ = posInBlock[2];
        }
        this.ticksInGround = nbt.getInteger("Life");
        this.inBlock = Block.getBlockById(nbt.getInteger("InBlock"));
        this.inBlockData = nbt.getByte("InBlockData");
        this.arrowShake = nbt.getByte("Shake");
        this.arrow = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Arrow"));
        this.inGround = nbt.getBoolean("InGround");

        if (nbt.hasKey("Damage", 99)) {
            this.damage = nbt.getDouble("Damage");
        }

        if (nbt.hasKey("CanPickupFlag", 99)) {
            this.canBePickedUp = nbt.getByte("CanPickUpFlag");
        } else if (nbt.hasKey("Player", 99)) {
            this.canBePickedUp = nbt.getBoolean("Player") ? 1 : 0;
        }
    }

    //数据同步
    @Override
    public void writeSpawnData(ByteBuf buf) {
        if (this.arrow != null) {
            buf.writeInt(Item.getIdFromItem(this.arrow.getItem()));
            buf.writeInt(this.arrow.getItemDamage());
        } else {
            buf.writeInt(Item.getIdFromItem(Items.arrow));
            buf.writeInt(0);
        }
    }
    @Override
    public void readSpawnData(ByteBuf buf) {
        int id = buf.readInt();
        int damage = buf.readInt();
        this.arrow = new ItemStack(Item.getItemById(id), 1, damage);
    }

    @Override
    public Entity getThrower() {
        return this.owner;
    }

    @Override
    public void setThrower(Entity entity) {
        this.owner = entity;
    }

    /// 玩家是否可以捡起箭矢
    public boolean canPickUp(EntityPlayer player) {
        return this.inGround && this.arrowShake <= 0 && (this.canBePickedUp == 1 || this.canBePickedUp == 2 && player.capabilities.isCreativeMode);
    }

    /**
     * 箭矢命中方块或实体逻辑
     * @param objectPositionOnHit 击中时的信息参数
     */
    public void onHit(@Nonnull MovingObjectPosition objectPositionOnHit) {
        if (objectPositionOnHit.entityHit instanceof EntityPlayer) {//无敌实体
            EntityPlayer player = (EntityPlayer)objectPositionOnHit.entityHit;
            if (player.capabilities.disableDamage || this.owner instanceof EntityPlayer && !((EntityPlayer)this.owner).canAttackPlayer(player)) {
                return;
            }
        }
        //命中目标处理
        if (objectPositionOnHit.entityHit != null) {//命中实体
            Cauldronpp.LOGGER.debug("Arrow hit entity {}", objectPositionOnHit.entityHit);
            this.onHitEntity(objectPositionOnHit.entityHit);
        } else {//命中方块
            //获取命中的方块
            this.inBlockX = objectPositionOnHit.blockX;
            this.inBlockY = objectPositionOnHit.blockY;
            this.inBlockZ = objectPositionOnHit.blockZ;
            this.inBlock = this.worldObj.getBlock(this.inBlockX, this.inBlockY, this.inBlockZ);
            this.inBlockData = this.worldObj.getBlockMetadata(this.inBlockX, this.inBlockY, this.inBlockZ);

            Cauldronpp.LOGGER.debug("Arrow hit block {} at ({},{},{})", this.inBlock.getLocalizedName(), inBlockX, inBlockY, inBlockZ);
            //减弱动能
            this.motionX = (float)(objectPositionOnHit.hitVec.xCoord - this.posX);
            this.motionY = (float)(objectPositionOnHit.hitVec.yCoord - this.posY);
            this.motionZ = (float)(objectPositionOnHit.hitVec.zCoord - this.posZ);
            double d = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.posX -= this.motionX / d * 0.05000000074505806;
            this.posY -= this.motionY / d * 0.05000000074505806;
            this.posZ -= this.motionZ / d * 0.05000000074505806;
            //音效
            this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
            //属性设置
            this.inGround = true;
            this.arrowShake = 7;
            this.setIsCritical(false);
            //触发接触方块逻辑
            if (this.inBlock.getMaterial() != Material.air) {
                this.inBlock.onEntityCollidedWithBlock(this.worldObj, this.inBlockX, this.inBlockY, this.inBlockZ, this);
            }
        }
    }

    /**
     * 命中实体逻辑
     * @param entity 命中的实体
     */
    public void onHitEntity(Entity entity) {
        //伤害计算
        double d = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        int damage = MathHelper.ceiling_double_int(d * this.damage);
        if (this.isCritical()) {//暴击
            damage += this.rand.nextInt(damage / 2 + 2);
        }
        //伤害来源
        DamageSource source;
        if (this.owner == null) {
            source = DamageSource.causeArrowDamage(this, this);
        } else {
            source = DamageSource.causeArrowDamage(this, this.owner);
        }
        //点燃
        if (this.isBurning() && !(entity instanceof EntityEnderman)) {
            entity.setFire(5);
        }
        //造成伤害
        if (entity.attackEntityFrom(source, damage)) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase)entity;
                //实体附着箭矢渲染
                if (!this.worldObj.isRemote) {
                    target.setArrowCountInEntity(target.getArrowCountInEntity() + 1);
                }
                //击退
                if (this.knockbackStrength > 0) {
                    float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    if (f > 0.0f) {
                        entity.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579 / (double)f, 0.1, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579 / (double)f);
                    }
                }
                //施加药箭效果
                if (CauldronppItems.CPP_TIPPED_ARROW.equals(Utils.getItemFromStack(this.arrow))) {
                    for (PotionEffect effect : ((TippedArrowItem)CauldronppItems.CPP_TIPPED_ARROW).getEffects(Objects.requireNonNull(this.arrow))) {
                        target.addPotionEffect(effect);
                    }
                }
                //附魔效果计算
                if (this.owner != null && this.owner instanceof EntityLivingBase) {
                    EnchantmentHelper.func_151384_a(target, this.owner);
                    EnchantmentHelper.func_151385_b((EntityLivingBase)this.owner, target);
                }
                //玩家击中其他玩家计分（？
                if (this.owner != null && entity != this.owner &&
                    entity instanceof EntityPlayer && this.owner instanceof EntityPlayerMP) {
                    ((EntityPlayerMP)this.owner).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                }
            }
            //音效
            this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
            //成功命中后删除自身
            if (!(entity instanceof EntityEnderman)) {
                this.setDead();
            }
        } else {//命中失败，被弹开
            this.motionX *= -0.10000000149011612;
            this.motionY *= -0.10000000149011612;
            this.motionZ *= -0.10000000149011612;
            this.rotationYaw += 180.0f;
            this.prevRotationYaw += 180.0f;
            this.ticksInAir = 0;
        }
    }

    /// 粒子效果
    public void generateParticles() {
        if ((this.worldObj.getWorldTime() & 7) != 0) return;

        final float r = ((this.getColor() >> 16) & 0xFF) / 255.0f;
        final float g = ((this.getColor() >> 8) & 0xFF) / 255.0f;
        final float b = (this.getColor() & 0xFF) / 255.0f;
        this.worldObj.spawnParticle("mobSpell", this.posX + this.rand.nextDouble() - 0.5, this.posY,
            this.posZ + this.rand.nextDouble() - 0.5, r, g, b);
    }
}
