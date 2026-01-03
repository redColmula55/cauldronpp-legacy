package rc55.mc.cauldronpp.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.renderer.entity.RenderArrow;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.CauldronppCommonProxy;
import rc55.mc.cauldronpp.client.renderer.block.CppCauldronRenderHandler;
import rc55.mc.cauldronpp.client.renderer.tileEntity.CppCauldronTileEntityRenderer;
import rc55.mc.cauldronpp.client.renderer.entity.CppPotionEntityRenderer;
import rc55.mc.cauldronpp.client.renderer.entity.LingeringEffectCloudEntityRenderer;
import rc55.mc.cauldronpp.entity.CppPotionEntity;
import rc55.mc.cauldronpp.entity.LingeringEffectCloudEntity;
import rc55.mc.cauldronpp.entity.TippedArrowEntity;
import rc55.mc.cauldronpp.tileEntity.CppCauldronTileEntity;

/**
 * 客户端侧
 * 用于注册渲染器等
 */
@SuppressWarnings("unused")
public class CauldronppClientProxy extends CauldronppCommonProxy {

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        //方块
        RenderingRegistry.registerBlockHandler(new CppCauldronRenderHandler());
        //方块实体
        ClientRegistry.bindTileEntitySpecialRenderer(CppCauldronTileEntity.class, new CppCauldronTileEntityRenderer());
        //实体
        RenderingRegistry.registerEntityRenderingHandler(TippedArrowEntity.class, new RenderArrow());
        RenderingRegistry.registerEntityRenderingHandler(CppPotionEntity.class, new CppPotionEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(LingeringEffectCloudEntity.class, new LingeringEffectCloudEntityRenderer());
        Cauldronpp.LOGGER.info("Cauldron++ client init complete.");
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
    }
}
