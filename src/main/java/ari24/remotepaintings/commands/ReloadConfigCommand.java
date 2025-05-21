package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistryHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;

public class ReloadConfigCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("reloadConfig")
                .executes(ctx -> {
                    // Reload the config
                    String currentConfigUrl = RemotePaintingsMod.CONFIG.currentConfigUrl();

                    if (currentConfigUrl.isEmpty()) {
                        return 0;
                    }

                    try {
                        RemotePaintingRegistryHelper.registerFromConfigUrl(currentConfigUrl);
                    } catch (URISyntaxException | IOException e) {
                        RemotePaintingsMod.LOGGER.error("Failed to load image from config URL: " + currentConfigUrl, e);
                        return 0;
                    }

                    ctx.getSource().sendFeedback(Text.of("Reloaded config from URL: " + currentConfigUrl));
                    return 1;
                });
    }
}
