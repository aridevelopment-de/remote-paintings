package ari24.remotepaintings.commands;

import ari24.remotepaintings.registry.RemotePaintingRegistry;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class ReloadImagesCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("reloadImages")
                .executes(ctx -> {
                    RemotePaintingRegistry.reloadAll();
                    ctx.getSource().sendFeedback(Text.of("Reloaded all images"));
                    return 1;
                });
    }
}
