package rc55.mc.cauldronpp.api;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.*;

//特别鸣谢 wwwweeeeee团队及retromcp项目 解析b1.9-pre2酿造逻辑
//special thanks to wwwweeeeee team and the retromcp project
@SuppressWarnings("unused")
public final class CppPotionHelper {

    private CppPotionHelper() {}

    //酿造材料（地狱疣单独计算）
    public final static Map<Item, String> brewingMaterial = new HashMap<>();
    // 出现特定效果需要满足的条件
    private final static Map<Potion, String> potionRequirements = new HashMap<>();
    // 提升效果等级的条件
    private final static Map<Potion, String> potionAmplifiers = new HashMap<>();

    public static Map<Item, String> getBrewingMaterial() {
        return brewingMaterial;
    }

    public static String getMaterialProperty(Item item) {
        return brewingMaterial.get(item);
    }

    //加水后改变
    public static final String WATER_MATERIAL = "-1-3-5-7-9-11-13";
    //药水类型
    //0为普通，1为喷溅，2为滞留，3为药箭
    public static final byte DEFAULT_TYPE = 0;
    public static final byte SPLASH_TYPE = 1;
    public static final byte LINGERING_TYPE = 2;
    public static final byte ARROW_TYPE = 3;
    //类型（普通，喷溅，滞留）转化
    public static final Map<Item, Byte> brewingMaterialType = Utils.makeMap(map -> {
        map.put(Items.speckled_melon, DEFAULT_TYPE);
        map.put(Items.gunpowder, SPLASH_TYPE);
    });



    public static Map<Potion, String> getPotionRequirements() {
        return potionRequirements;
    }

    public static Map<Potion, String> getPotionAmplifiers() {
        return potionAmplifiers;
    }

    public static void registerPotionRequirement(Potion potion, String requirement) {
        if (potionRequirements.containsKey(potion))
            throw new IllegalArgumentException("Duplicate registration for potion effect " + potion.getId());
        potionRequirements.put(potion, requirement);
    }

    public static void registerPotionAmplifiers(Potion potion, String requirement) {
        if (potionAmplifiers.containsKey(potion))
            throw new IllegalArgumentException("Duplicate registration for potion effect " + potion.getId());
        potionAmplifiers.put(potion, requirement);
    }

    //二进制数相关计算
    // 返回二进制数index的i1~i5位组成的二进制数
    private static int getNumberInBinary(int index, int i1, int i2, int i3, int i4, int i5) {
        return (checkFlag(index, i1) ? 16 : 0) | (checkFlag(index, i2) ? 8 : 0)
            | (checkFlag(index, i3) ? 4 : 0)
            | (checkFlag(index, i4) ? 2 : 0)
            | (checkFlag(index, i5) ? 1 : 0);
    }

    private static boolean checkFlag(int i0, int i1) {// 二进制数i0的i1位是否为1
        return (i0 & 1 << i1) != 0;
    }

    private static int countFlags(int i0) {//二进制数i0中有多少位为1
        int i1;
        for (i1 = 0; i0 > 0; ++i1) {
            i0 &= i0 - 1;
        }
        return i1;
    }

    private static int isFlagSet(int i0, int i1) {
        return checkFlag(i0, i1) ? 1 : 0;
    }

    private static int isFlagNotSet(int i0, int i1) {
        return checkFlag(i0, i1) ? 0 : 1;
    }

    private static boolean checkBoolean(int i0, int i1) {
        return (i0 & 1 << i1 % 15) != 0;
    }

    //药水颜色
    public static int getPotionColor(int potionData) {
        int r = (getNumberInBinary(potionData, 2, 14, 11, 8, 5) ^ 3) << 3;
        int g = (getNumberInBinary(potionData, 0, 12, 9, 6, 3) ^ 6) << 3;
        int b = (getNumberInBinary(potionData, 13, 10, 4, 1, 7) ^ 8) << 3;
        return r << 16 | g << 8 | b;
    }

    //药水名字
    public static String getPotionPrefixTranslationKey(int potionData) {
        int i = getNumberInBinary(potionData, 14, 9, 7, 3, 2);
        return potionPrefixTranslationKeys[i];
    }

    //地狱疣计算
    public static int applyMaterialNetherWart(int potionData) {
        if ((potionData & 1) != 0) {
            potionData = applyNetherWart1(potionData);
        }

        return applyNetherWart(potionData);
    }

    private static int applyNetherWart1(int i0) {
        if ((i0 & 1) == 0) {
            return i0;
        }

        int i1;
        //i0有多少位
        for (i1 = 14; (i0 & 1 << i1) == 0 && i1 >= 0; --i1);

        if (i1 < 2 || (i0 & 1 << i1 - 1) != 0) {
            return i0;
        }

        i0 &= ~(1 << i1);

        i0 <<= 1;
        i0 |= 1 << i1;
        i0 |= 1 << i1 - 1;

        return i0 & 32767;
    }

    private static int applyNetherWart(int i0) {
        int i1;
        for (i1 = 14; (i0 & 1 << i1) == 0 && i1 >= 0; --i1);

        if (i1 >= 0) {
            i0 &= ~(1 << i1);
        }

        int i2 = 0;

        for (int i3 = i0; i3 != i2; i0 = i2) {
            i3 = i0;
            i2 = 0;

            for (int i4 = 0; i4 < 15; ++i4) {
                boolean z5 = checkBoolean(i0, i4);
                if (z5) {
                    if (!checkBoolean(i0, i4 + 1) && checkBoolean(i0, i4 + 2)) {
                        z5 = false;
                    } else if (!checkBoolean(i0, i4 - 1) && checkBoolean(i0, i4 - 2)) {
                        z5 = false;
                    }
                } else {
                    z5 = checkBoolean(i0, i4 - 1) && checkBoolean(i0, i4 + 1);
                }

                if (z5) {
                    i2 |= 1 << i4;
                }
            }
        }

        if (i1 >= 0) {
            i2 |= 1 << i1;
        }

        return i2 & 32767;
    }

    //效果等级&持续时间 不符合要求返回0
    private static int getEffectMultiplier(String requirement, int startPos, int endPos, int potionData) {
        if (startPos >= requirement.length() || endPos < 0 || startPos >= endPos) {
            //没有效果或者不提升等级
            return 0;
        }

        // | ascii 124 符号|所在位置
        int i4 = requirement.indexOf(124, startPos);
        int i5;
        //具有 “|” (或) 运算符
        if (i4 >= 0 && i4 < endPos) {
            // 前半
            int front = getEffectMultiplier(requirement, startPos, i4 - 1, potionData);
            if (front > 0) {
                return front;
            }

            // 后半
            int back = getEffectMultiplier(requirement, i4 + 1, endPos, potionData);
            return Math.max(back, 0);
        }

        // & ascii 38 符号&所在位置
        i5 = requirement.indexOf(38, startPos);

        //具有 “&” (与) 运算符
        if (i5 >= 0 && i5 < endPos) {
            int front = getEffectMultiplier(requirement, startPos, i5 - 1, potionData);
            if (front <= 0) {
                return 0;
            }

            int back = getEffectMultiplier(requirement, i5 + 1, endPos, potionData);
            return back <= 0 ? 0 : (Math.max(front, back));
        }

        boolean z6 = false;
        boolean z7 = false;
        boolean needsValueUpdate = false;
        boolean z9 = false;
        boolean z10 = false;

        //模式 未指定为-1 =为0 >为1 <为2
        byte operation = -1;
        int flag = 0;
        int i13 = 0;
        int i14 = 0;

        //遍历要求
        for (int i15 = startPos; i15 < endPos; ++i15) {
            //当前位置
            char thisChar = requirement.charAt(i15);

            //数字0~9
            if (thisChar >= 48 && thisChar <= 57) {
                //乘数
                if (z6) {
                    i13 = thisChar - 48;
                    z7 = true;
                    continue;
                }

                //位数
                flag *= 10;
                flag += thisChar - 48;
                //计算后更新计算用属性
                //以符号分隔为一组（e.g. 在+1-15<13中，有+1 -15 <13 3组）
                //检测到第二组才更新第一组
                //遍历完成后更新最后一组
                needsValueUpdate = true;
                continue;
            }

            if (thisChar == 42) {// * ascii 42
                z6 = true;
                continue;
            }

            switch ((byte) thisChar) {
                case 33:
                    z9 = true;
                    break;
                case 45:
                    z10 = true;
                    break;
                case 61:
                    operation = 0;
                    break;
                case 62:
                    operation = 1;
                    break;
                case 63:
                    operation = 2;
                    break;
            }

            if (!needsValueUpdate) {
                break;
            }
            i14 += updateEffectWeigh(z9, z7, z10, operation, flag, i13, potionData);
            z9 = false;
            z10 = false;
            z6 = false;
            z7 = false;
            needsValueUpdate = false;
            i13 = 0;
            flag = 0;
        }

        if (needsValueUpdate) {//更新最后一组属性
            i14 += updateEffectWeigh(z9, z7, z10, operation, flag, i13, potionData);
        }

        return i14;
    }

    private static int updateEffectWeigh(boolean flagNon, boolean flagMul, boolean flagI, int operation, int flagCount, int i5,
        int potionData) {
        // Fixme - 为什么这里的 i7 赋值后下方（324）行重新赋值？
        int i7 = 0;
        if (flagNon) {
            i7 = isFlagNotSet(potionData, flagCount);
        } else if (operation != -1) {           //判断模式 未指定为-1 =为0 >为1 <为2
            if (operation == 0 && countFlags(potionData) == flagCount) {
                i7 = 1;
            } else if (operation == 1 && countFlags(potionData) > flagCount) {
                i7 = 1;
            } else if (operation == 2 && countFlags(potionData) < flagCount) {
                i7 = 1;
            }
        }

        i7 = isFlagSet(potionData, flagCount);

        if (flagMul) {
            i7 *= i5;
        }

        if (flagI) {
            i7 *= -1;
        }

        return i7;
    }

    //放入材料后转换对应药水数据
    public static int applyMaterial(int potionData, String materialProperty) {
        byte b2 = 0;
        int i3 = materialProperty.length();
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        int i7 = 0;

        //遍历
        for (int i8 = b2; i8 < i3; ++i8) {
            char thisChar = materialProperty.charAt(i8);
            if (thisChar >= 48 && thisChar <= 57) {//数字 0~9
                i7 *= 10;
                i7 += thisChar - 48;
                z4 = true;
                continue;
            }

            if (!z4) {
                continue;
            }


            // Fixme - 冗余写法
            switch ((byte) thisChar) {
                case 33:
                case 45:
                case 43:
                    potionData = updatePotionData(potionData, i7, z6, z5);
                    z5 = false;
                    z6 = false;
                    z4 = false;
                    i7 = 0;
            }
        }

        if (z4) {
            potionData = updatePotionData(potionData, i7, z6, z5);
        }

        return potionData & 32767;
    }

    private static int updatePotionData(int potionData, int flag, boolean z2, boolean z3) {
        //设置为0
        if (z2) {
            return potionData & ~(1 << flag);
        }

        //反转
        if (!z3 && (potionData & (1 << flag)) == 0) {
            return potionData | (1 << flag);
        }
        //原为1,设为0
        return potionData & ~(1 << flag);
    }

    //获取对应药水的所有效果
    public static List<PotionEffect> getEffects(int potionData) {
        ArrayList<PotionEffect> effects = new ArrayList<>();
        for (Potion effect : Potion.potionTypes) {
            if (Objects.isNull(effect)) {
                continue;
            }

            String effectRequirement = potionRequirements.get(effect);
            if (Objects.isNull(effectRequirement)) {
                continue;
            }

            int duration = getEffectMultiplier(effectRequirement, 0, effectRequirement.length(), potionData);
            if (duration <= 0) {
                continue;
            }

            int amplifier = 0;
            String amplifierRequirement = potionAmplifiers.get(effect);
            if (Objects.nonNull(amplifierRequirement)) {
                amplifier = getEffectMultiplier(amplifierRequirement, 0,
                    amplifierRequirement.length(), potionData);

                amplifier = Math.max(0, amplifier);
            }

            if (effect.isInstant()) {
                duration = 1;
                effects.add(new PotionEffect(effect.getId(), duration, amplifier));
                continue;
            }

            duration = 1200 * (duration * 3 + (duration - 1) * 2);
            //减少有害效果时常
            if (effect.isBadEffect()) {
                duration >>= 1;
            }

            effects.add(new PotionEffect(effect.getId(), duration, amplifier));
        }

        return effects;
    }

    // 药水名字
    private static final String[] potionPrefixTranslationKeys = new String[] { "potion.prefix.mundane",
        "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky",
        "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward",
        "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered",
        "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick",
        "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing",
        "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent",
        "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh",
        "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky" };

    static {
        //原版
        //不同效果的需求 感谢wwwweeeeee团队 解读此部分逻辑
        //数字为位数 +为某位是否为1 -为某位是否为0 !为为1的位数不为多少 >=<为为1的位数大于/等于/小于多少 |为或者(满足一个条件即可) &为并且(所有条件均需要满足) *暂时不明
        //逻辑优先计算或（|） 其次为与（&）
        potionRequirements.put(Potion.moveSpeed, "!10 & !4 & 5*2+0 & >1 | !7 & !4 & 5*2+0 & >1");
        potionRequirements.put(Potion.moveSlowdown, "10 & 7 & !4 & 7+5+1-0");
        potionRequirements.put(Potion.digSpeed, "2 & 12+2+6-1-7 & <8");
        potionRequirements.put(Potion.digSlowdown, "!2 & !1*2-9 & 14-5");
        potionRequirements.put(Potion.damageBoost, "9 & 3 & 9+4+5 & <11");
        potionRequirements.put(Potion.heal, "11 & <6");
        potionRequirements.put(Potion.harm, "!11 & 1 & 10 & !7");
        potionRequirements.put(Potion.jump, "8 & 2+0 & <5");
        potionRequirements.put(Potion.confusion, "8*2-!7+4-11 & !2 | 13 & 11 & 2*3-1-5");
        potionRequirements.put(Potion.regeneration, "!14 & 13*3-!0-!5-8");
        potionRequirements.put(Potion.resistance, "10 & 4 & 10+5+6 & <9");
        potionRequirements.put(Potion.fireResistance, "14 & !5 & 6-!1 & 14+13+12");
        potionRequirements.put(Potion.waterBreathing, "0+1+12 & !6 & 10 & !11 & !13");
        potionRequirements.put(Potion.invisibility, "2+5+13-0-4 & !7 & !1 & >5");
        potionRequirements.put(Potion.blindness, "9 & !1 & !5 & !3 & =3");
        potionRequirements.put(Potion.nightVision, "8*2-!7 & 5 & !0 & >3");
        potionRequirements.put(Potion.hunger, ">4>6>8-3-8+2");
        potionRequirements.put(Potion.weakness, "=1>5>7>9+3-7-2-11 & !10 & !0");
        potionRequirements.put(Potion.poison, "12+9 & !13 & !0");
        //提升效果等级的需求
        potionAmplifiers.put(Potion.moveSpeed, "7+!3-!1");
        potionAmplifiers.put(Potion.digSpeed, "1+0-!11");
        potionAmplifiers.put(Potion.damageBoost, "2+7-!12");
        potionAmplifiers.put(Potion.heal, "11+!0-!1-!14");
        potionAmplifiers.put(Potion.harm, "!11-!14+!0-!1");
        potionAmplifiers.put(Potion.resistance, "12-!2");
        potionAmplifiers.put(Potion.poison, "14>5");
        //酿造材料
        //数字为位数 +为将某位设置为1 -为将某位设置为0 !为反转某位(若它为1,将其设置为0;若它为0,将其设置为1)
        brewingMaterial.put(Items.sugar, "+0");
        brewingMaterial.put(Items.ghast_tear, "+11");
        brewingMaterial.put(Items.spider_eye, "+10+7+5");
        brewingMaterial.put(Items.fermented_spider_eye, "+14+9");
        brewingMaterial.put(Items.blaze_powder, "+14");
        brewingMaterial.put(Items.magma_cream, "+14+6+1");
        //新增
        //效果
        potionRequirements.put(Potion.wither, "=6>7 & +14+2");
        potionRequirements.put(Potion.field_76434_w, "6+8 & !3-9 & =10>14");// 生命提升
        potionRequirements.put(Potion.field_76444_x, "=1>4 & !5!3 | 2*4+5<13");// 伤害吸收
        potionRequirements.put(Potion.field_76443_y, "=0=5=7=8");// 饱和
        //提升效果等级的需求
        potionAmplifiers.put(Potion.wither, "6-5 & !1 & =8");
        potionAmplifiers.put(Potion.field_76434_w, "!5 & !3 & =11");
        potionAmplifiers.put(Potion.field_76444_x, "=11=4 & -5+14");
        potionAmplifiers.put(Potion.digSlowdown, "!6 & 2 & -1-9");
        potionAmplifiers.put(Potion.field_76443_y, "=1=9+8-10");
        //酿造材料
        brewingMaterial.put(Items.glowstone_dust, "+8+3");
        brewingMaterial.put(Items.redstone, "-6-1+0");
        brewingMaterial.put(Items.golden_carrot, "-5+12!4");
        brewingMaterial.put(Items.slime_ball, "+11-13");
    }

    //返回对应药水的物品堆
    public static ItemStack getPotionItem(byte potionType, int potionData, int amount) {
        if (potionData == 0 && potionType == 0) return new ItemStack(CauldronppItems.WATER_BOTTLE);
        ItemStack stack;
        if (potionType == SPLASH_TYPE) {
            stack = new ItemStack(CauldronppItems.CPP_SPLASH_POTION, amount, potionData);
        } else {
            stack = new ItemStack(CauldronppItems.CPP_POTION, amount, potionData);
        }
        return stack;
    }

    public static ItemStack getPotionItem(byte potionType, int potionData) {
        return getPotionItem(potionType, potionData, 1);
    }

    //是否为水瓶
    public static boolean isWaterBottle(ItemStack stack) {
        return stack != null && ((stack.getItem() == CauldronppItems.WATER_BOTTLE)
            || ((stack.getItem() == Items.potionitem) && stack.getItemDamage() == 0));
    }
}
