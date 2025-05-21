package ari24.remotepaintings.util;

import ari24.remotepaintings.RemotePaintingsMod;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    public static NativeImageBackedTexture retrievePng(InputStream stream) {
        NativeImage image = null;

        try {
            image = NativeImage.read(stream);
        } catch (IOException e) {
            RemotePaintingsMod.LOGGER.error("Failed to load image from InputStream", e);
        }

        return new NativeImageBackedTexture(image);
    }

    public static NativeImageBackedTexture retrieveJpeg(InputStream stream) {
        throw new UnsupportedOperationException("JPEG format is not supported yet");
    }
}
