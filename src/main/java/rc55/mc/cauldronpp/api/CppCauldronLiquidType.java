package rc55.mc.cauldronpp.api;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import rc55.mc.cauldronpp.Cauldronpp;

public enum CppCauldronLiquidType {

    NONE(),
    WATER(Items.water_bucket, new ResourceLocation(Cauldronpp.MODID, "textures/blocks/cauldron_inner_water.png"), true),
    POTION(null, new ResourceLocation(Cauldronpp.MODID, "textures/blocks/cauldron_inner_potion.png"), true),
    LAVA(Items.lava_bucket, new ResourceLocation(Cauldronpp.MODID, "textures/blocks/cauldron_inner_lava.png"), false),
    COLORED_WATER(null, new ResourceLocation(Cauldronpp.MODID, "textures/blocks/cauldron_inner_water.png"), true);

    private final Item bucketItem;
    private final ResourceLocation texture;
    private final boolean shouldRenderColor;

    CppCauldronLiquidType(Item bucketItem, ResourceLocation texture, boolean color) {
        this.bucketItem = bucketItem;
        this.texture = texture;
        this.shouldRenderColor = color;
    }

    CppCauldronLiquidType() {
        this(null, null, false);
    }

    public static CppCauldronLiquidType byId(byte id) {
        return id < 0 || id > values().length ? NONE : values()[id];
    }

    public byte getId() {
        return (byte) this.ordinal();
    }

    @Nullable
    public Item getBucketItem() {
        return this.bucketItem;
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    public ResourceLocation getTexture() {
        return this.texture;
    }

    public boolean shouldRenderColor() {
        return this.shouldRenderColor;
    }
}
