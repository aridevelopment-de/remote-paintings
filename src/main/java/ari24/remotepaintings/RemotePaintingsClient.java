package ari24.remotepaintings;

import ari24.remotepaintings.registry.RemotePaintingRegistryHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.io.IOException;
import java.net.URISyntaxException;

public class RemotePaintingsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RemotePaintingsMod.LOGGER.info("Initialized Remote Paintings Client");

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            registerDefaultPaintings();
        });
    }

    private void registerDefaultPaintings() {
        try {
            String currentConfigUrl = RemotePaintingsMod.CONFIG.currentConfigUrl();

            if (currentConfigUrl.isEmpty()) {
                return;
            }

            RemotePaintingRegistryHelper.registerFromConfigUrl(currentConfigUrl);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
