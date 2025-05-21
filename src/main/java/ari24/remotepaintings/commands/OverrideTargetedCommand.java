package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistryHelper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.URISyntaxException;

public class OverrideTargetedCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("overrideTargeted")
                    .then(ClientCommandManager.argument("url", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                RemotePaintingsMod.LOGGER.info("Overriding painting...");
                                MinecraftClient client = MinecraftClient.getInstance();
                                Entity target = client.targetedEntity;

                                if (!(target instanceof PaintingEntity paintingEntity)) {
                                    ctx.getSource().sendFeedback(Text.of("No painting entity targeted.").getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                                    return 0;
                                }

                                PaintingVariant variant = paintingEntity.getVariant().value();
                                Identifier vanillaId = variant.assetId();
                                String url = StringArgumentType.getString(ctx, "url");

                                try {
                                    RemotePaintingRegistryHelper.registerFromUrl(vanillaId.getPath(), url);
                                } catch (IOException | URISyntaxException e) {
                                    ctx.getSource().sendError(Text.of("Failed to load image from URL")
                                            .getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                                    RemotePaintingsMod.LOGGER.error("Failed to load image from config URL: " + url, e);
                                    return 0;
                                }

                                ctx.getSource().sendFeedback(Text.of("Overriding painting with URL: " + url)
                                        .getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                                return 1;
                            }));
    }
}
