package ari24.remotepaintings;

import ari24.remotepaintings.mixin.SpriteAtlasHolderAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class RemotePaintingsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RemotePaintingsMod.customPaintingManager.initialize();
        RemotePaintingsMod.LOGGER.info("Initialized Remote Paintings Client");
    }
}
