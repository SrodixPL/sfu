package net.srodix.client.ui.sdf;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

public final class SdfRenderer {
    private static final int SHAPE_SAMPLE_SCALE = 4;
    private static final int SHADOW_SAMPLE_SCALE = 3;
    private static final SdfTextureCache CACHE = new SdfTextureCache();

    private SdfRenderer() {
    }

    public static void drawRoundedRect(GuiGraphics guiGraphics, int x, int y, int width, int height, SdfRectStyle style) {
        if (width <= 0 || height <= 0) {
            return;
        }

        SdfTextureKey key = new SdfTextureKey(
            width * SHAPE_SAMPLE_SCALE,
            height * SHAPE_SAMPLE_SCALE,
            SHAPE_SAMPLE_SCALE,
            SdfTextureKind.RECT,
            style.fillColor(),
            style.borderColor(),
            style.cornerRadius() * SHAPE_SAMPLE_SCALE,
            style.borderWidth() * SHAPE_SAMPLE_SCALE,
            style.edgeSoftness() * SHAPE_SAMPLE_SCALE,
            0,
            0
        );

        blit(guiGraphics, x, y, width, height, CACHE.getOrCreate(key));
    }

    public static void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int fillColor, int borderColor, float borderWidth) {
        int diameter = radius * 2;
        drawRoundedRect(
            guiGraphics,
            centerX - radius,
            centerY - radius,
            diameter,
            diameter,
            new SdfRectStyle(fillColor, borderColor, radius, borderWidth, 1.25F)
        );
    }

    public static void drawShadow(GuiGraphics guiGraphics, int x, int y, int width, int height, SdfShadowStyle style) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int spread = Math.max(1, Math.round(style.blurRadius() * 2.0F));
        int textureWidth = width + (spread * 2);
        int textureHeight = height + (spread * 2);
        SdfTextureKey key = new SdfTextureKey(
            textureWidth * SHADOW_SAMPLE_SCALE,
            textureHeight * SHADOW_SAMPLE_SCALE,
            SHADOW_SAMPLE_SCALE,
            SdfTextureKind.SHADOW,
            style.color(),
            0x00000000,
            style.cornerRadius() * SHADOW_SAMPLE_SCALE,
            0.0F,
            style.blurRadius() * SHADOW_SAMPLE_SCALE,
            style.offsetX() * SHADOW_SAMPLE_SCALE,
            style.offsetY() * SHADOW_SAMPLE_SCALE
        );

        blit(guiGraphics, x - spread, y - spread, textureWidth, textureHeight, CACHE.getOrCreate(key));
    }

    public static void clearCache() {
        CACHE.clear();
    }

    public static void warmRoundedRect(int width, int height, SdfRectStyle style) {
        if (width <= 0 || height <= 0) {
            return;
        }

        SdfTextureKey key = new SdfTextureKey(
            width * SHAPE_SAMPLE_SCALE,
            height * SHAPE_SAMPLE_SCALE,
            SHAPE_SAMPLE_SCALE,
            SdfTextureKind.RECT,
            style.fillColor(),
            style.borderColor(),
            style.cornerRadius() * SHAPE_SAMPLE_SCALE,
            style.borderWidth() * SHAPE_SAMPLE_SCALE,
            style.edgeSoftness() * SHAPE_SAMPLE_SCALE,
            0,
            0
        );
        CACHE.getOrCreate(key);
    }

    public static void warmCircle(int radius, int fillColor, int borderColor, float borderWidth) {
        int diameter = radius * 2;
        warmRoundedRect(diameter, diameter, new SdfRectStyle(fillColor, borderColor, radius, borderWidth, 1.25F));
    }

    public static void warmShadow(int width, int height, SdfShadowStyle style) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int spread = Math.max(1, Math.round(style.blurRadius() * 2.0F));
        int textureWidth = width + (spread * 2);
        int textureHeight = height + (spread * 2);
        SdfTextureKey key = new SdfTextureKey(
            textureWidth * SHADOW_SAMPLE_SCALE,
            textureHeight * SHADOW_SAMPLE_SCALE,
            SHADOW_SAMPLE_SCALE,
            SdfTextureKind.SHADOW,
            style.color(),
            0x00000000,
            style.cornerRadius() * SHADOW_SAMPLE_SCALE,
            0.0F,
            style.blurRadius() * SHADOW_SAMPLE_SCALE,
            style.offsetX() * SHADOW_SAMPLE_SCALE,
            style.offsetY() * SHADOW_SAMPLE_SCALE
        );
        CACHE.getOrCreate(key);
    }

    private static void blit(GuiGraphics guiGraphics, int x, int y, int width, int height, SdfTextureCache.CachedTexture texture) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture.id(), x, y, 0.0F, 0.0F, width, height, texture.pixelWidth(), texture.pixelHeight(), texture.pixelWidth(), texture.pixelHeight());
    }
}
