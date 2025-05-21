package ari24.remotepaintings.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class Commands {
    public static void registerClient() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("remotepaintings")
                    .then(OverrideCommand.register())
                    .then(LoadCommand.register())
                    .then(ReloadConfigCommand.register())
                    .then(ReloadImagesCommand.register())
                    .then(PaintingInfoCommand.register())
                    .then(UnloadPaintingCommand.register())
                    .then(OverrideTargetedCommand.register())
                    .then(SaveCommand.register()));
        }));
    }
}
