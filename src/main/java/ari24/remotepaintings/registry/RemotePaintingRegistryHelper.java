package ari24.remotepaintings.registry;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.util.GifStitcher;
import ari24.remotepaintings.util.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;
import java.util.List;

public class RemotePaintingRegistryHelper {
    public static void registerFromConfigUrl(String urlString) throws URISyntaxException, IOException {
        URL url = new URI(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            Gson gson = new Gson();
            JsonArray array = gson.fromJson(response.toString(), JsonArray.class);

            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.get("name").getAsString();
                String overrideUrl = obj.get("url").getAsString();
                registerFromUrl(name, overrideUrl);
            }
        }
    }

    public static void registerFromUrl(String vanillaIdentifier, String url) {
        Pair<InputStream, ContentType> inputStreamPair;

        try {
            inputStreamPair = getInputStream(url);
        } catch (IOException | URISyntaxException e) {
            RemotePaintingsMod.LOGGER.error("Failed to load image from URL: " + url, e);
            return;
        }

        InputStream inputStream = inputStreamPair.getLeft();
        @Nullable ContentType contentType = inputStreamPair.getRight();

        if (contentType == null) {
            RemotePaintingsMod.LOGGER.error("Failed to determine content type for URL: " + url);
            return;
        }

        RemotePaintingRegistry.Entry entry = new RemotePaintingRegistry.Entry(
                Identifier.ofVanilla(vanillaIdentifier),
                Identifier.of(RemotePaintingsMod.MOD_ID, "remote/" + vanillaIdentifier),
                url);
        entry.setContentType(contentType);

        // TODO: Remove jpeg and add default fallback for all other image types
        NativeImageBackedTexture image;
        switch (contentType) {
            case GIF:
                GifStitcher.GifAtlasResult result = GifStitcher.generateGifAtlas(inputStream);

                if (result == null) {
                    RemotePaintingsMod.LOGGER.error("Failed to load GIF from InputStream");
                    return;
                }

                image = result.texture();
                entry.setDelayMs(result.delayMs());
                entry.setFrameCount(result.frameCount());
                break;
            case PNG:
                image = ImageUtils.retrievePng(inputStream);
                break;
            case JPEG:
                image = ImageUtils.retrieveJpeg(inputStream);
                break;
            default:
                RemotePaintingsMod.LOGGER.error("Unsupported content type: " + contentType);
                return;
        }

        if (image == null || image.getImage() == null) {
            RemotePaintingsMod.LOGGER.error("Failed to load image from InputStream");
            return;
        }

        entry.setPixelWidth(image.getImage().getWidth());
        entry.setPixelHeight(image.getImage().getHeight());
        RemotePaintingRegistry.registerRemotePainting(entry, image);
    }

    private static Pair<InputStream, @Nullable ContentType> getInputStream(String urlString) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        URLConnection connection = url.openConnection();
        connection.connect();

        String contentType = connection.getContentType();
        InputStream inputStream = connection.getInputStream();

        if (contentType == null) {
            throw new IOException("Unable to determine content type from URL: " + urlString);
        }

        ContentType type = ContentType.fromString(contentType);
        return new Pair<>(inputStream, type);
    }

    public enum ContentType {
        PNG("png"),
        JPEG("jpeg", "jpg"),
        GIF("gif");

        private final List<String> extensions;

        ContentType(String ...extensions) {
            this.extensions = Arrays.asList(extensions);
        }

        public static ContentType fromString(String contentType) {
            for (ContentType type : values()) {
                if (type.extensions.stream().anyMatch(contentType::contains)) {
                    return type;
                }
            }
            return null;
        }
    }
}
