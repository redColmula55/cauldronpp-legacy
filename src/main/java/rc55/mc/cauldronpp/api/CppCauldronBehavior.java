package rc55.mc.cauldronpp.api;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rc55.mc.cauldronpp.item.CauldronppItems;
import rc55.mc.cauldronpp.tileEntity.CppCauldronTileEntity;

import java.util.Map;

@FunctionalInterface
public interface CppCauldronBehavior {
    //交互
    boolean interact(World world, int x, int y, int z, int meta, CppCauldronTileEntity cauldron, EntityPlayer player, ItemStack stack);
    //空交互
    CppCauldronBehavior EMPTY = (world, x, y, z, meta, cauldron, player, stack) -> false;
    //常量
    int BOTTLE_AMOUNT = 1;//瓶子
    int BUCKET_AMOUNT = 3;//桶
    int MAX_AMOUNT = 3;//最大容量

    CppCauldronBehavior brewMaterialBehavior = (world, x, y, z, meta,
                                                cauldron,
                                                player,
                                                stack) -> {
        if (!cauldron.canBrew()) {
            return false;
        }

        int newData;

        // 地狱疣 -> 药水类型材料 -> 酿造材料
        if (stack.getItem() == Items.nether_wart) {
            newData = CppPotionHelper.applyMaterialNetherWart(cauldron.getLiquidData());
            if (newData  == cauldron.getLiquidData()) {
                return false;
            }

            cauldron.setLiquidData(newData);
        } else if (CppPotionHelper.brewingMaterialType.containsKey(stack.getItem())) {
            newData = CppPotionHelper.brewingMaterialType.get(stack.getItem());
            if (newData == cauldron.getPotionType()) {
                return false;
            }

            cauldron.setPotionType((byte) newData);
        } else if (CppPotionHelper.getBrewingMaterial().containsKey(stack.getItem())) {
            if (!cauldron.applyMaterial(CppPotionHelper.getMaterialProperty(stack.getItem()))) {
                return false;
            }
        } else {
            return false;
        }

        if (cauldron.isWater()) {
            cauldron.setLiquidType(CppCauldronLiquidType.POTION);
        }

        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }

        update(world, x, y, z, meta, cauldron);
        return true;
    };

    CppCauldronBehavior bucketBehavior = (world, x, y, z, meta, cauldron, player, stack) -> {
        ItemStack itemStack;
        if (stack.getItem() == Items.bucket) {
            if (cauldron.getLiquidType().getBucketItem() != null) {
                itemStack = new ItemStack(cauldron.getLiquidType().getBucketItem());
                if (cauldron.canDecrease(BUCKET_AMOUNT)) {
                    cauldron.decrease(BUCKET_AMOUNT);
                    meta = 0;
                    if (!player.capabilities.isCreativeMode) stack.stackSize--;
                    Utils.addStackToPlayerInv(world, player, itemStack);
                }
            } else return false;
        } else {
            itemStack = new ItemStack(Items.bucket);
            if (stack.getItem() == Items.lava_bucket) {
                if (cauldron.canIncrease(BUCKET_AMOUNT)) {
                    cauldron.setLiquidType(CppCauldronLiquidType.LAVA);
                    cauldron.increase(BUCKET_AMOUNT);
                    meta = 1;
                } else return false;
            } else if (stack.getItem() == Items.water_bucket) {
                if (cauldron.isEmpty() || cauldron.canBrew()) {
                    if (cauldron.isEmpty()) {
                        cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                    }
                    cauldron.setLiquidLevel(BUCKET_AMOUNT);
                    cauldron.applyMaterial(CppPotionHelper.WATER_MATERIAL);
                } else return false;
            } else return false;
            if (!player.capabilities.isCreativeMode) stack.stackSize--;
            Utils.addStackToPlayerInv(world, player, itemStack);
        }
        update(world, x, y, z, meta, cauldron);
        return true;
    };

    CppCauldronBehavior bottleBehavior = (world, x, y, z, meta, cauldron, player, stack) -> {
        if (!cauldron.isEmpty() && !cauldron.canBrew()) return false;
        if (stack.getItem() == Items.glass_bottle) {
            if (!cauldron.canDecrease(BOTTLE_AMOUNT)) return false;
            ItemStack itemStack;
            if (cauldron.isWater()) {
                itemStack = new ItemStack(CauldronppItems.WATER_BOTTLE);
            } else {
                itemStack = CppPotionHelper.getPotionItem(cauldron.getPotionType(), cauldron.getLiquidData());
            }
            Utils.addStackToPlayerInv(world, player, itemStack);
            cauldron.decrease(BOTTLE_AMOUNT);
        } else if (CppPotionHelper.isWaterBottle(stack) && cauldron.canIncrease(BOTTLE_AMOUNT)) {
            if (cauldron.isEmpty()) cauldron.setLiquidType(CppCauldronLiquidType.WATER);
            cauldron.increase(BOTTLE_AMOUNT);
            cauldron.applyMaterial(CppPotionHelper.WATER_MATERIAL);
            Utils.addStackToPlayerInv(world, player, new ItemStack(Items.glass_bottle));
        } else return false;
        if (!player.capabilities.isCreativeMode) stack.stackSize--;
        update(world, x, y, z, meta, cauldron);
        return true;
    };

    CppCauldronBehavior dyeableItemBehavior = (world, x, y, z, meta, cauldron, player, stack) -> {
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.CLOTH) {
                if (cauldron.isWater()) {
                    if (!armor.hasColor(stack)) return false;
                    armor.removeColor(stack);
                } else if (cauldron.getLiquidType() == CppCauldronLiquidType.COLORED_WATER) {
                    if (armor.hasColor(stack)) {
                        armor.func_82813_b(stack, getDyedColor(armor.getColor(stack), cauldron.getLiquidData()));
                    } else {
                        armor.func_82813_b(stack, cauldron.getLiquidData());
                    }
                } else return false;
                cauldron.decrease(1);
            }
        } else return false;
        update(world, x, y, z, meta, cauldron);
        return true;
    };

    Map<Item, CppCauldronBehavior> behaviorMap = Utils.makeMap(map -> {
        //桶
        map.put(Items.lava_bucket, bucketBehavior);
        map.put(Items.bucket, bucketBehavior);
        map.put(Items.water_bucket, bucketBehavior);

        //酿造材料
        for (Item item : CppPotionHelper.getBrewingMaterial().keySet()) {
            map.put(item, brewMaterialBehavior);
        }
        for (Item item : CppPotionHelper.brewingMaterialType.keySet()) {
            map.put(item, brewMaterialBehavior);
        }
        map.put(Items.nether_wart, brewMaterialBehavior);

        //瓶子
        map.put(Items.glass_bottle, bottleBehavior);
        map.put(Items.potionitem, bottleBehavior);
        map.put(CauldronppItems.WATER_BOTTLE, bottleBehavior);

        //皮革盔甲清洗，染色
        map.put(Items.leather_helmet, dyeableItemBehavior);
        map.put(Items.leather_chestplate, dyeableItemBehavior);
        map.put(Items.leather_leggings, dyeableItemBehavior);
        map.put(Items.leather_boots, dyeableItemBehavior);

        //染色水
        map.put(Items.dye, (world, x, y, z, meta, cauldron, player, stack) -> {
            if (stack.getItem() == Items.dye && stack.getItemDamage() < 16) {
                if (cauldron.isEmpty()) return false;
                int dyeColor = ItemDye.field_150922_c[stack.getItemDamage()];
                if (cauldron.isWater()) {
                    cauldron.setLiquidType(CppCauldronLiquidType.COLORED_WATER);
                    cauldron.setLiquidData(dyeColor);
                } else if (cauldron.getLiquidType() == CppCauldronLiquidType.COLORED_WATER) {
                    cauldron.setLiquidData(getDyedColor(cauldron.getLiquidData(), dyeColor));
                } else return false;
                if (!player.capabilities.isCreativeMode) stack.stackSize--;
            } else return false;
            update(world, x, y, z, meta, cauldron);
            return true;
        });
    });

    static void update(World world, int x, int y, int z, int meta, CppCauldronTileEntity cauldron) {
        Block block = cauldron.getBlockType();
        cauldron.markDirty();
        cauldron.updateEntity();
        world.scheduleBlockUpdate(x, y, z, block, meta);
        world.markAndNotifyBlock(x, y, z, world.getChunkFromBlockCoords(x, z), block, block, 2);
        if (!world.setBlockMetadataWithNotify(x, y, z, meta, 2)) {
            world.markBlockForUpdate(x, y, z);
        }
        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
    }

    static int getDyedColor(int oriColor, int dyeColor) {
        int max = 0;
        int[] results = new int[3];
        oriColor = oriColor & 0xffffff;
        float oriR = (oriColor >> 16 & 0xff) / 255.0f;
        float oriG = (oriColor >> 8 & 0xff) / 255.0f;
        float oriB = (oriColor & 0xff) / 255.0f;
        results[0] += (int) (oriR * 255.0f);
        results[1] += (int) (oriG * 255.0f);
        results[2] += (int) (oriB * 255.0f);
        max += (int) (Math.max(oriR, Math.max(oriG, oriB)) * 255.0f);

        dyeColor = dyeColor & 0xffffff;
        float[] dyeColors = new float[3];
        dyeColors[0] = (dyeColor >> 16 & 0xff) / 255.0f;
        dyeColors[1] = (dyeColor >> 8 & 0xff) / 255.0f;
        dyeColors[2] = (dyeColor & 0xff) / 255.0f;
        int dyeR = (int) (dyeColors[0] * 255.0f);
        int dyeG = (int) (dyeColors[1] * 255.0f);
        int dyeB = (int) (dyeColors[2] * 255.0f);
        max += Math.max(dyeR, Math.max(dyeG, dyeB));
        results[0] += dyeR;
        results[1] += dyeG;
        results[2] += dyeB;

        int resultR = results[0] / 2;
        int resultG = results[1] / 2;
        int resultB = results[2] / 2;

        float avg = (float) max / 2;
        float max2 = Math.max(resultR, Math.max(resultG, resultB));

        resultR = (int) (resultR * avg / max2);
        resultG = (int) (resultG * avg / max2);
        resultB = (int) (resultB * avg / max2);

        return (resultR << 16) | (resultG << 8) | resultB;
    }
}
