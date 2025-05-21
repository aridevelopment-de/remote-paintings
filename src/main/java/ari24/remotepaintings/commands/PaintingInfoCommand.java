package ari24.remotepaintings.commands;

import ari24.remotepaintings.registry.RemotePaintingRegistry;
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

public class PaintingInfoCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("paintingInfo")
                    .executes(ctx -> {
                        MinecraftClient client = MinecraftClient.getInstance();
                        Entity target = client.targetedEntity;

                        if (!(target instanceof PaintingEntity paintingEntity)) {
                            ctx.getSource().sendFeedback(Text.of("No painting entity targeted.").getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                            return 0;
                        }

                        PaintingVariant variant = paintingEntity.getVariant().value();
                        Identifier assetId = variant.assetId();
                        RemotePaintingRegistry.Entry entry = RemotePaintingRegistry.getRemotePainting(assetId);

                        ctx.getSource().sendFeedback(Text.of("Painting Info:").getWithStyle(Style.EMPTY.withColor(Formatting.YELLOW).withBold(true)).getFirst());
                        ctx.getSource().sendFeedback(Text.of("V-ID: " + assetId).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                        ctx.getSource().sendFeedback(Text.of("V-Width: " + variant.width()).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                        ctx.getSource().sendFeedback(Text.of("V-Height: " + variant.height()).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                        ctx.getSource().sendFeedback(variant.author().orElse(Text.of("No author given")).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                        ctx.getSource().sendFeedback(variant.title().orElse(Text.of("No title given")).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());

                        if (entry == null) {
                            ctx.getSource().sendFeedback(Text.of("No remote painting found for ID: " + assetId).getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                            return 0;
                        }

                        ctx.getSource().sendFeedback(Text.of("Remote Painting Info:").getWithStyle(Style.EMPTY.withColor(Formatting.YELLOW).withBold(true)).getFirst());
                        ctx.getSource().sendFeedback(Text.of("R-ID: " + entry.customIdentifier()).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                        ctx.getSource().sendFeedback(Text.of("Content-Type: " + entry.contentType()).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                        return 1;
                    });
    }
}
