package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistry;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class UnloadPaintingCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("unloadPainting")
                .then(ClientCommandManager.argument("id", StringArgumentType.string())
                            .executes(ctx -> {
                                RemotePaintingsMod.LOGGER.info("Unloading painting...");
                                String vanillaId = StringArgumentType.getString(ctx, "id");
                                RemotePaintingRegistry.unregisterRemotePainting(Identifier.ofVanilla(vanillaId));
                                ctx.getSource().sendFeedback(Text.of("Unloaded painting with ID: " + vanillaId).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)).getFirst());
                                return 1;
                            }));
    }
}
