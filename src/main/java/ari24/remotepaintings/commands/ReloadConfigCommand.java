package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistryHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
                        ctx.getSource().sendError(Text.of("Failed to reload config from URL")
                                .getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                        RemotePaintingsMod.LOGGER.error("Failed to register config from URL: " + currentConfigUrl, e);
                        return 0;
                    }

                    ctx.getSource().sendFeedback(Text.of("Reloaded config from URL: " + currentConfigUrl).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                    return 1;
                });
    }
}
