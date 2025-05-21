package ari24.remotepaintings.mixin;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.state.PaintingEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PaintingEntityRenderer.class)
public class PaintingEntityRendererMixin {
    @Redirect(method="render(Lnet/minecraft/client/render/entity/state/PaintingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;[IIILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V"))
    private void remotePainting$injectRemovePaintingRenderer(PaintingEntityRenderer instance, MatrixStack matrices, VertexConsumer vertexConsumer, int[] lightmapCoordinates, int width, int height, Sprite paintingSprite, Sprite backSprite, @Local(argsOnly = true) PaintingEntityRenderState paintingEntityRenderState) {
        if (paintingEntityRenderState.variant == null) {
            this.renderPainting(matrices, vertexConsumer, lightmapCoordinates, width, height, paintingSprite, backSprite, true);
            return;
        }

        @Nullable RemotePaintingRegistry.Entry remotePaintingEntry = RemotePaintingRegistry.getRemotePainting(paintingEntityRenderState.variant.assetId());
        boolean renderOriginal = remotePaintingEntry == null;
        this.renderPainting(matrices, vertexConsumer, lightmapCoordinates, width, height, paintingSprite, backSprite, renderOriginal);
    }

    @Inject(method="render(Lnet/minecraft/client/render/entity/state/PaintingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at=@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void remotePainting$injectCustomPaintingRenderer(PaintingEntityRenderState paintingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (paintingEntityRenderState.variant == null) return;

        @Nullable RemotePaintingRegistry.Entry remotePaintingEntry = RemotePaintingRegistry.getRemotePainting(paintingEntityRenderState.variant.assetId());
        if (remotePaintingEntry == null) {
            return;
        }

        matrixStack.push();
        RenderSystem.setShaderTexture(0, remotePaintingEntry.customIdentifier());
        RenderLayer layer = RemotePaintingsMod.getRemotePaintingRenderLayer(remotePaintingEntry.customIdentifier());
        VertexConsumer customConsumer = vertexConsumerProvider.getBuffer(layer);
        MatrixStack.Entry entry = matrixStack.peek();
        int width = paintingEntityRenderState.variant.width();
        int height = paintingEntityRenderState.variant.height();

        //noinspection SwitchStatementWithTooFewBranches
        switch (remotePaintingEntry.contentType()) {
            case GIF -> renderGif(remotePaintingEntry, entry, customConsumer, width, height);
            default -> renderImage(remotePaintingEntry, entry, customConsumer, width, height);
        }

        matrixStack.pop();
    }

    @Unique
    private void renderGif(RemotePaintingRegistry.Entry remotePaintingEntry, MatrixStack.Entry entry, VertexConsumer vertexConsumer, int width, int height) {
        MinecraftClientAccessor accessor = (MinecraftClientAccessor) MinecraftClient.getInstance();
        float halfWidthOffset = -width / 2.0F;
        float halfHeightOffset = -height / 2.0F;

        // this stretches the whole image across the painting
        float x0 = halfWidthOffset;
        float x1 = halfWidthOffset + width;
        float y0 = halfHeightOffset;
        float y1 = halfHeightOffset + height;
        long gameTick = accessor.getUptimeInTicks();

        float delayTicks = Objects.requireNonNull(remotePaintingEntry.delayMs) / 50f;
        float uStepSize = 1f / Objects.requireNonNull(remotePaintingEntry.frameCount);
        int currentFrame = (int) ((gameTick / delayTicks) % Objects.requireNonNull(remotePaintingEntry.frameCount));
        float u0 = uStepSize * currentFrame;
        float u1 = u0 + uStepSize;

        // lower left
        this.vertex(entry, vertexConsumer, x1, y0, u0, 1, -0.03125F, 0, 1, 0, 15728880);
        // top left
        this.vertex(entry, vertexConsumer, x0, y0, u1, 1, -0.03125F, 0, 1, 0, 15728880);
        // top right
        this.vertex(entry, vertexConsumer, x0, y1, u1, 0, -0.03125F, 0, 1, 0, 15728880);
        // bottom right
        this.vertex(entry, vertexConsumer, x1, y1, u0, 0, -0.03125F, 0, 1, 0, 15728880);
    }

    @Unique
    private void renderImage(RemotePaintingRegistry.Entry remotePaintingEntry, MatrixStack.Entry entry, VertexConsumer vertexConsumer, int width, int height) {
        float halfWidthOffset = -width / 2.0F;
        float halfHeightOffset = -height / 2.0F;

        // this stretches the whole image across the painting
        float x0 = halfWidthOffset;
        float x1 = halfWidthOffset + width;
        float y0 = halfHeightOffset;
        float y1 = halfHeightOffset + height;

        // lower left
        this.vertex(entry, vertexConsumer, x1, y0, 0, 1, -0.03125F, 0, 1, 0, 15728880);
        // top left
        this.vertex(entry, vertexConsumer, x0, y0, 1, 1, -0.03125F, 0, 1, 0, 15728880);
        // top right
        this.vertex(entry, vertexConsumer, x0, y1, 1, 0, -0.03125F, 0, 1, 0, 15728880);
        // bottom right
        this.vertex(entry, vertexConsumer, x1, y1, 0, 0, -0.03125F, 0, 1, 0, 15728880);
    }

    @Unique
    private void renderPainting(
            MatrixStack matrices, VertexConsumer vertexConsumer, int[] lightmapCoordinates, int width, int height, Sprite paintingSprite, Sprite backSprite,
            boolean renderOriginal
    ) {
        MatrixStack.Entry entry = matrices.peek();
        float halfWidthOffset = -width / 2.0F;
        float halfHeightOffset = -height / 2.0F;
        float relLayerWidth = 0.03125F;
        float minU = backSprite.getMinU();
        float maxU = backSprite.getMaxU();
        float minV = backSprite.getMinV();
        float maxV = backSprite.getMaxV();

        float innerV = backSprite.getFrameV(0.0625F);
        float innerU = backSprite.getFrameU(0.0625F);
        double widthStep = 1.0 / width;
        double heightStep = 1.0 / height;

        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {
                float x1 = halfWidthOffset + (u + 1);
                float x0 = halfWidthOffset + u;
                float y1 = halfHeightOffset + (v + 1);
                float y0 = halfHeightOffset + v;

                int lightMap = lightmapCoordinates[u + v * width];
                float textureU0 = paintingSprite.getFrameU((float)(widthStep * (width - u)));
                float textureU1 = paintingSprite.getFrameU((float)(widthStep * (width - (u + 1))));
                float textureV0 = paintingSprite.getFrameV((float)(heightStep * (height - v)));
                float textureV1 = paintingSprite.getFrameV((float)(heightStep * (height - (v + 1))));

                // Drawing image content itself
                if (renderOriginal) {
                    this.vertex(entry, vertexConsumer, x1, y0, textureU1, textureV0, -relLayerWidth, 0, 0, -1, lightMap);
                    this.vertex(entry, vertexConsumer, x0, y0, textureU0, textureV0, -relLayerWidth, 0, 0, -1, lightMap);
                    this.vertex(entry, vertexConsumer, x0, y1, textureU0, textureV1, -relLayerWidth, 0, 0, -1, lightMap);
                    this.vertex(entry, vertexConsumer, x1, y1, textureU1, textureV1, -relLayerWidth, 0, 0, -1, lightMap);
                }

                // Drawing borders and backside
                this.vertex(entry, vertexConsumer, x1, y1, maxU, minV, relLayerWidth, 0, 0, 1, lightMap);
                this.vertex(entry, vertexConsumer, x0, y1, minU, minV, relLayerWidth, 0, 0, 1, lightMap);
                this.vertex(entry, vertexConsumer, x0, y0, minU, maxV, relLayerWidth, 0, 0, 1, lightMap);
                this.vertex(entry, vertexConsumer, x1, y0, maxU, maxV, relLayerWidth, 0, 0, 1, lightMap);
                this.vertex(entry, vertexConsumer, x1, y1, minU, minV, -relLayerWidth, 0, 1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y1, maxU, minV, -relLayerWidth, 0, 1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y1, maxU, innerV, relLayerWidth, 0, 1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x1, y1, minU, innerV, relLayerWidth, 0, 1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x1, y0, minU, minV, relLayerWidth, 0, -1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y0, maxU, minV, relLayerWidth, 0, -1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y0, maxU, innerV, -relLayerWidth, 0, -1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x1, y0, minU, innerV, -relLayerWidth, 0, -1, 0, lightMap);
                this.vertex(entry, vertexConsumer, x1, y1, innerU, minV, relLayerWidth, -1, 0, 0, lightMap);
                this.vertex(entry, vertexConsumer, x1, y0, innerU, maxV, relLayerWidth, -1, 0, 0, lightMap);
                this.vertex(entry, vertexConsumer, x1, y0, minU, maxV, -relLayerWidth, -1, 0, 0, lightMap);
                this.vertex(entry, vertexConsumer, x1, y1, minU, minV, -relLayerWidth, -1, 0, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y1, innerU, minV, -relLayerWidth, 1, 0, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y0, innerU, maxV, -relLayerWidth, 1, 0, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y0, minU, maxV, relLayerWidth, 1, 0, 0, lightMap);
                this.vertex(entry, vertexConsumer, x0, y1, minU, minV, relLayerWidth, 1, 0, 0, lightMap);
            }
        }
    }

    @Unique
    private void vertex(
            MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light
    ) {
        vertexConsumer.vertex(matrix, x, y, z)
                .color(Colors.WHITE)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, normalX, normalY, normalZ);
    }
}
