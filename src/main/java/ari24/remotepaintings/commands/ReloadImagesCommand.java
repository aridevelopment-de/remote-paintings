package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistry;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.net.URISyntaxException;

public class ReloadImagesCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("reloadImages")
                .executes(ctx -> {
                    try {
                        RemotePaintingRegistry.reloadAll();
                    } catch (IOException | URISyntaxException e) {
                        ctx.getSource().sendError(Text.of("Failed to reload all images")
                                .getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                        RemotePaintingsMod.LOGGER.error("Failed to reload all images", e);
                        return 0;
                    }

                    ctx.getSource().sendFeedback(Text.of("Reloaded all images").getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                    return 1;
                });
    }
}
