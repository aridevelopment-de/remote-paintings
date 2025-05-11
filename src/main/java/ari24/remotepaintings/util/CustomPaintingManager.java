package ari24.remotepaintings.util;

import ari24.remotepaintings.RemotePaintingsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomPaintingManager {
    public Map<Identifier, PaintingVariant> registry = new HashMap<>();

    public void initialize() {
        RemotePaintingsMod.LOGGER.info("Initializing Custom Painting Manager");

        Identifier assetId = Identifier.of("remotepaintings:test_painting");
        PaintingVariant variant = new PaintingVariant(1, 1, assetId,
                Optional.empty(), Optional.empty());

        registry.put(assetId, variant);

        NativeImage image;

        try {
            URI imageUri = URI.create("https://b.catgirlsare.sexy/QDrjK3Q7Rtrg.png");
            URL imageUrl = imageUri.toURL();
            InputStream imageStream = imageUrl.openStream();
            image = NativeImage.read(imageStream);
        } catch (Exception e) {
            RemotePaintingsMod.LOGGER.error("Failed to load image", e);
            return;
        }

        SpriteContents contents = new SpriteContents(assetId, new SpriteDimensions(image.getWidth(), image.getHeight()), image, ResourceMetadata.NONE);

        // Initialize dummy texture in ign texture registry
        // MinecraftClient.getInstance().getPaintingManager()

        // MinecraftClient.getInstance().getPaintingManager().getPaintingSprite();
        //MinecraftClient.getInstance().getTextureManager().registerTexture(assetId);
    }
}
