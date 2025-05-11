package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.mixin.SpriteAtlasHolderAccessor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class TestCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("test")
                .executes(ctx -> {
                    ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
                    TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                    Identifier atlasId = Identifier.ofVanilla("textures/atlas/paintings.png");
                    Identifier sourcePath = Identifier.ofVanilla("paintings");

                    PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
                    SpriteAtlasHolderAccessor accessor = (SpriteAtlasHolderAccessor) paintingManager;
                    SpriteAtlasTexture atlas = accessor.getAtlas();
                    Set<ResourceMetadataSerializer<?>> metadataSerializers = accessor.getMetadataReaders();

                    // Get all normal sprites
                    SpriteOpener spriteOpener = SpriteOpener.create(metadataSerializers);

                    CompletableFuture.supplyAsync(() -> AtlasLoader.of(resourceManager, sourcePath).loadSources(resourceManager))
                            .thenCompose(sources -> SpriteLoader.loadAll(spriteOpener, sources, Runnable::run))
                            .thenApply(sprites -> {
                                for (SpriteContents contents : sprites) {
                                    RemotePaintingsMod.LOGGER.info("Loaded sprite: " + contents);
                                }

                                return sprites;
                            });

                    // This is a no-op, as we don't need to prepare the executor for this command.
                    Executor prepareExecutor = Runnable::run;

                    SpriteLoader.fromAtlas(atlas)
                            .load(resourceManager, sourcePath, 0, prepareExecutor, metadataSerializers)
                            .thenCompose(SpriteLoader.StitchResult::whenComplete)
                            .thenAcceptAsync((stitchResult -> {
                                RemotePaintingsMod.LOGGER.info("Reloaded paintings: " + stitchResult);
                                atlas.upload(stitchResult);
                            }));

                    return 1;
                });
    }
}
