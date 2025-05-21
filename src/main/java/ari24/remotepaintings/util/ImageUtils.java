package ari24.remotepaintings.util;

import ari24.remotepaintings.RemotePaintingsMod;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public static NativeImageBackedTexture retrieveGeneralImageFormat(InputStream stream) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(stream);

        if (bufferedImage == null) {
            throw new IOException("Failed to read image from input stream");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean success = ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

        if (!success) {
            throw new IOException("Failed to write image as PNG");
        }

        ByteArrayInputStream newStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return retrievePng(newStream);
    }
}
