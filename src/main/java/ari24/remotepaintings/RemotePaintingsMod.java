package ari24.remotepaintings;

import ari24.remotepaintings.commands.Commands;
import ari24.remotepaintings.config.RemotePaintingsConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class RemotePaintingsMod implements ModInitializer {
	public static final String MOD_ID = "remote-paintings";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final Function<Identifier, RenderLayer> REMOTE_PAINTING_RENDER_LAYER;
	public static final RemotePaintingsConfig CONFIG = RemotePaintingsConfig.createAndLoad();


	static {
		REMOTE_PAINTING_RENDER_LAYER = Util.memoize((texture) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
					.program(RenderPhase.ENTITY_ALPHA_PROGRAM)
					.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
					.transparency(RenderPhase.NO_TRANSPARENCY)
					.lightmap(RenderPhase.ENABLE_LIGHTMAP)
					.cull(RenderPhase.DISABLE_CULLING)
					.overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
					.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING_FORWARD)
					.build(true);
			return RenderLayer.of("remote_painting_render_layer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, multiPhaseParameters);
		});
	}

	public static RenderLayer getRemotePaintingRenderLayer(Identifier texture) {
		return (RenderLayer) REMOTE_PAINTING_RENDER_LAYER.apply(texture);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		Commands.registerClient();
	}
}