package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistryHelper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class OverrideCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("override")
                .then(ClientCommandManager.argument("id", StringArgumentType.string())
                        .then(ClientCommandManager.argument("url", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    RemotePaintingsMod.LOGGER.info("Overriding painting...");
                                    String vanillaId = StringArgumentType.getString(ctx, "id");
                                    String url = StringArgumentType.getString(ctx, "url");
                                    RemotePaintingRegistryHelper.registerFromUrl(vanillaId, url);
                                    return 1;
                                })));
    }
}
