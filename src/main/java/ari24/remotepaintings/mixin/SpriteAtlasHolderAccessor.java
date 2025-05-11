package ari24.remotepaintings.mixin;

import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;


@Mixin(SpriteAtlasHolder.class)
public interface SpriteAtlasHolderAccessor {
    @Accessor
    SpriteAtlasTexture getAtlas();

    @Accessor
    Set<ResourceMetadataSerializer<?>> getMetadataReaders();
}
