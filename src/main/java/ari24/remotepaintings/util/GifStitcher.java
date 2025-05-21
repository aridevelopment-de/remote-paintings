package ari24.remotepaintings.util;

import ari24.remotepaintings.RemotePaintingsMod;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class GifStitcher {
    public static GifAtlasResult generateGifAtlas(InputStream inputStream) {
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(inputStream);

        if (status != GifDecoder.STATUS_OK) {
            RemotePaintingsMod.LOGGER.error("Failed to read GIF: " + status);
            return null;
        }

        int frameCount = decoder.getFrameCount();
        int maxWidth = 0;
        int maxHeight = 0;
        final BufferedImage[] frames = new BufferedImage[frameCount];

        for (int i = 0; i < frameCount; i++) {
            BufferedImage frame = decoder.getFrame(i);
            frames[i] = frame;
            maxWidth = Math.max(maxWidth, frame.getWidth());
            maxHeight = Math.max(maxHeight, frame.getHeight());
        }

        try (NativeImage stitchedTexture = new NativeImage(
                NativeImage.Format.RGBA, maxWidth * frameCount, maxHeight, false)) {
            for (int i = 0; i < frameCount; ++i) {
                BufferedImage frame = frames[i];
                int width = frame.getWidth();
                int height = frame.getHeight();
                int xOffset = maxWidth * i + (maxWidth - width) / 2;
                int yOffset = (maxHeight - height) / 2;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgb = frame.getRGB(x, y);
                        stitchedTexture.setColorArgb(x + xOffset, y + yOffset, rgb);
                    }
                }
            }

            int delay = decoder.delay;

            if (delay == 0) {
                float aveDelay = 0;

                for (int i = 0; i < frameCount; i++) {
                    aveDelay += decoder.getDelay(i);
                }

                aveDelay /= (float) frameCount;
                delay = (int) Math.floor(aveDelay);
            }

            return new GifAtlasResult(
                    new NativeImageBackedTexture(stitchedTexture),
                    maxWidth * frameCount,
                    maxHeight,
                    frameCount,
                    delay
            );
        }
    }

    public record GifAtlasResult(
            NativeImageBackedTexture texture,
            int pixelWidth,
            int pixelHeight,
            int frameCount,
            int delayMs) {}
}
