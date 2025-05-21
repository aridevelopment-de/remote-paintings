package ari24.remotepaintings.registry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RemotePaintingRegistry {
    private static final Map<Identifier, Entry> registeredPaintings = new HashMap<>();

    public static void registerRemotePainting(Entry entry, NativeImageBackedTexture image) {
        if (registeredPaintings.containsKey(entry.vanillaIdentifier())) {
            unregisterRemotePainting(entry.vanillaIdentifier());
        }

        registeredPaintings.put(entry.vanillaIdentifier(), entry);

        TextureManager manager = MinecraftClient.getInstance().getTextureManager();
        manager.registerTexture(entry.customIdentifier(), image);
    }

    public static void unregisterRemotePainting(Identifier vanillaIdentifier) {
        Entry entry = registeredPaintings.remove(vanillaIdentifier);

        TextureManager manager = MinecraftClient.getInstance().getTextureManager();
        manager.destroyTexture(entry.customIdentifier());
    }

    public static @Nullable Entry getRemotePainting(Identifier vanillaIdentifier) {
        return registeredPaintings.get(vanillaIdentifier);
    }

    public static List<Entry> getAllRemotePaintings() {
        return List.copyOf(registeredPaintings.values());
    }

    public static void reloadAll() throws IOException, URISyntaxException {
        Map<Identifier, Entry> copy = new HashMap<>(registeredPaintings);
        for (Map.Entry<Identifier, Entry> entry : copy.entrySet()) {
            Identifier vanillaIdentifier = entry.getKey();
            unregisterRemotePainting(vanillaIdentifier);
        }

        for (Map.Entry<Identifier, Entry> entry : copy.entrySet()) {
            Identifier vanillaIdentifier = entry.getKey();
            Entry paintingEntry = entry.getValue();
            RemotePaintingRegistryHelper.registerFromUrl(vanillaIdentifier.getPath(), paintingEntry.remoteUrl());
        }
    }

    public static final class Entry {
        private final Identifier vanillaIdentifier;
        private final Identifier customIdentifier;
        private final String remoteUrl;
        private int pixelWidth;
        private int pixelHeight;
        private RemotePaintingRegistryHelper.ContentType contentType;
        public @Nullable Integer delayMs;
        public @Nullable Integer frameCount;

        public Entry(Identifier vanillaIdentifier, Identifier customIdentifier, String remoteUrl) {
            this.vanillaIdentifier = vanillaIdentifier;
            this.customIdentifier = customIdentifier;
            this.remoteUrl = remoteUrl;
        }

        public Entry(Identifier vanillaIdentifier, Identifier customIdentifier,
                     String remoteUrl,
                     int pixelWidth, int pixelHeight,
                     RemotePaintingRegistryHelper.ContentType contentType) {
            this.vanillaIdentifier = vanillaIdentifier;
            this.customIdentifier = customIdentifier;
            this.remoteUrl = remoteUrl;
            this.pixelWidth = pixelWidth;
            this.pixelHeight = pixelHeight;
            this.contentType = contentType;
        }

        public void setDelayMs(@Nullable Integer delayMs) {
            this.delayMs = delayMs;
        }

        public void setFrameCount(@Nullable Integer frameCount) {
            this.frameCount = frameCount;
        }

        public void setPixelWidth(int pixelWidth) {
            this.pixelWidth = pixelWidth;
        }

        public void setPixelHeight(int pixelHeight) {
            this.pixelHeight = pixelHeight;
        }

        public void setContentType(RemotePaintingRegistryHelper.ContentType contentType) {
            this.contentType = contentType;
        }

        public Identifier vanillaIdentifier() {
            return vanillaIdentifier;
        }

        public Identifier customIdentifier() {
            return customIdentifier;
        }

        public int pixelWidth() {
            return pixelWidth;
        }

        public int pixelHeight() {
            return pixelHeight;
        }

        public String remoteUrl() {
            return remoteUrl;
        }

        public RemotePaintingRegistryHelper.ContentType contentType() {
            return contentType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Entry) obj;
            return Objects.equals(this.vanillaIdentifier, that.vanillaIdentifier) &&
                    Objects.equals(this.customIdentifier, that.customIdentifier) &&
                    this.pixelWidth == that.pixelWidth &&
                    this.pixelHeight == that.pixelHeight &&
                    Objects.equals(this.contentType, that.contentType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vanillaIdentifier, customIdentifier, pixelWidth, pixelHeight, contentType);
        }

        @Override
        public String toString() {
            return "Entry[" +
                    "vanillaIdentifier=" + vanillaIdentifier + ", " +
                    "customIdentifier=" + customIdentifier + ", " +
                    "pixelWidth=" + pixelWidth + ", " +
                    "pixelHeight=" + pixelHeight + ", " +
                    "contentType=" + contentType + ']';
        }
    }
}
