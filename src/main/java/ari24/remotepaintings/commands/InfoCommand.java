package ari24.remotepaintings.commands;

import ari24.remotepaintings.registry.RemotePaintingRegistry;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class InfoCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("paintingInfo")
                .executes(ctx -> {
                    for (RemotePaintingRegistry.Entry entry : RemotePaintingRegistry.getAllRemotePaintings()) {
                        // [x] 1. AZTEC => (PNG) https://gif.gif.com
                        // [x] 2. AZTEC2 => (GIF) https://gif.gif.com

                        Text closeText = Text.of("[x]").getWithStyle(Style.EMPTY.withColor(Formatting.RED)
                                .withBold(true)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Remove override")))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/remotepainting unloadPainting " + entry.vanillaIdentifier().getPath()))).getFirst();

                        Text urlText = Text.of(entry.remoteUrl()).getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, entry.remoteUrl()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Open URL")))).getFirst();

                        MutableText finalText = MutableText.of(PlainTextContent.of(entry.vanillaIdentifier().getPath() + " => "))
                                .append(Text.of("(" + entry.contentType() + ") ").getWithStyle(Style.EMPTY.withColor(Formatting.YELLOW)).getFirst())
                                .append(closeText)
                                .append(Text.of(" "))
                                .append(urlText);

                        ctx.getSource().sendFeedback(finalText);
                    }
                    return 1;
                });
    }
}
