package rc55.mc.cauldronpp.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import rc55.mc.cauldronpp.client.renderer.CppCauldronTileEntityRenderer;
import rc55.mc.cauldronpp.tileEntity.CppCauldronTileEntity;

public class ClientProxy implements IModProxy {
    @Override
    public void clientRenderTile() {
        ClientRegistry.bindTileEntitySpecialRenderer(
            CppCauldronTileEntity.class,
            new CppCauldronTileEntityRenderer()
        );
    }
}
