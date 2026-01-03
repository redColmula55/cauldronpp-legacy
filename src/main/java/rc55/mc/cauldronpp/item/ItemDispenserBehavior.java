package rc55.mc.cauldronpp.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.api.Utils;
import rc55.mc.cauldronpp.entity.CppPotionEntity;
import rc55.mc.cauldronpp.entity.TippedArrowEntity;

import java.util.HashSet;
import java.util.Set;

public abstract class ItemDispenserBehavior implements IBehaviorDispenseItem {

    public static final IBehaviorDispenseItem FALLBACK_BEHAVIOR = new BehaviorDefaultDispenseItem();

    public static final ItemDispenserBehavior POTION_BEHAVIOR = new PlaceProjectileEntity(Utils.make(new HashSet<>(), set -> {
        set.add(CauldronppItems.CPP_SPLASH_POTION);
        set.add(CauldronppItems.CPP_LINGERING_POTION);
    }), CppPotionEntity::new, 1.375F, 3.0F);
    public static final ItemDispenserBehavior TIPPED_ARROW_BEHAVIOR = new PlaceProjectileEntity(CauldronppItems.CPP_TIPPED_ARROW, TippedArrowEntity::new);

    public static void registerItemDispenserBehavior() {
        POTION_BEHAVIOR.register();
        TIPPED_ARROW_BEHAVIOR.register();
    }

    public abstract void register();

    public static class PlaceProjectileEntity extends ItemDispenserBehavior {

        private final Set<Item> acceptableItems;

        private final ProjectileProvider projectileProvider;

        private final float charge;
        private final float f;

        public PlaceProjectileEntity(final Item item, ProjectileProvider projectileProvider) {
            this(Utils.make(new HashSet<>(), set -> set.add(item)), projectileProvider, 1.1f, 6.0f);
        }

        public PlaceProjectileEntity(Set<Item> items, ProjectileProvider projectileProvider, float charge, float f) {
            this.acceptableItems = items;
            this.projectileProvider = projectileProvider;
            this.charge = charge;
            this.f = f;
        }

        @Override
        public void register() {
            this.acceptableItems.forEach(item -> BlockDispenser.dispenseBehaviorRegistry.putObject(item, this));
        }

        @Override
        public ItemStack dispense(IBlockSource source, ItemStack stack) {
            for (Item item : this.acceptableItems) {
                if (item.equals(Utils.getItemFromStack(stack))) {
                    World world = source.getWorld();
                    IPosition pos = BlockDispenser.func_149939_a(source);
                    EnumFacing facing = BlockDispenser.func_149937_b(source.getBlockMetadata());
                    int blockX = MathHelper.floor_double(pos.getX() + facing.getFrontOffsetX());
                    int blockY = MathHelper.floor_double(pos.getY() + facing.getFrontOffsetY());
                    int blockZ = MathHelper.floor_double(pos.getZ() + facing.getFrontOffsetZ());
                    if (world.getBlock(blockX, blockY, blockZ).getMaterial() != Material.air) {
                        return FALLBACK_BEHAVIOR.dispense(source, stack);
                    }
                    IProjectile projectile = this.getProjectileEntity(world, pos, stack);
                    projectile.setThrowableHeading(facing.getFrontOffsetX(), (float)facing.getFrontOffsetY() + 0.1f, facing.getFrontOffsetZ(), this.charge, this.f);
                    world.spawnEntityInWorld((Entity) projectile);
                    stack.splitStack(1);
                    return stack;
                }
            }
            return FALLBACK_BEHAVIOR.dispense(source, stack);
        }

        protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
            return this.projectileProvider.get(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }

    @FunctionalInterface
    public interface ProjectileProvider {
        IProjectile get(World world, double x, double y, double z, ItemStack stack);
    }
}
