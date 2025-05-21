package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistryHelper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.net.URISyntaxException;

public class LoadCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("load")
                .then(ClientCommandManager.argument("url", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        // https://haste.pinofett.de/raw/cvx8tmaubs
                        String url = StringArgumentType.getString(ctx, "url");
                        try {
                            RemotePaintingRegistryHelper.registerFromConfigUrl(url);
                        } catch (URISyntaxException | IOException e) {
                            ctx.getSource().sendError(Text.of("Failed to load image from URL: " + url)
                                    .getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                            RemotePaintingsMod.LOGGER.error("Failed to load image from config URL: " + url, e);
                            return 0;
                        }

                        return 1;
                    }));
    }
}
