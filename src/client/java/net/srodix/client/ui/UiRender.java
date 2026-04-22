package net.srodix.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class UiRender {
    private static final int TEXT_OPTICAL_CENTER_OFFSET = 2;

    private UiRender() {
    }

    public static void fill(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }

        guiGraphics.fill(x, y, x + width, y + height, color);
    }

    public static void text(GuiGraphics guiGraphics, String text, int x, int y, int color, boolean shadow) {
        guiGraphics.drawString(font(), text, x, y, color, shadow);
    }

    public static void text(GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean shadow) {
        guiGraphics.drawString(font(), text, x, y, color, shadow);
    }

    public static void centeredText(GuiGraphics guiGraphics, String text, int centerX, int y, int color) {
        guiGraphics.drawCenteredString(font(), text, centerX, y, color);
    }

    public static int centeredTextY(int top, int height) {
        return top + Math.round((height - fontHeight()) / 2.0F);
    }

    public static int centeredTextY(int top, int height, float scale) {
        return top + Math.round((height - (fontHeight() * scale)) / 2.0F);
    }

    public static int textTopForCenter(float centerY) {
        return Math.round(centerY - (fontHeight() / 2.0F));
    }

    public static int textTopForCenter(float centerY, float scale) {
        return Math.round(centerY - ((fontHeight() * scale) / 2.0F));
    }

    public static int opticalTextTopForCenter(float centerY) {
        return textTopForCenter(centerY) + TEXT_OPTICAL_CENTER_OFFSET;
    }

    public static int opticalTextTopForCenter(float centerY, float scale) {
        return textTopForCenter(centerY, scale) + TEXT_OPTICAL_CENTER_OFFSET;
    }

    public static void wrappedText(GuiGraphics guiGraphics, Component text, int x, int y, int width, int color) {
        guiGraphics.drawWordWrap(font(), text, x, y, width, color);
    }

    public static int wrappedTextHeight(Component text, int width) {
        return font().wordWrapHeight(text, width);
    }

    public static int textWidth(String text) {
        return font().width(text);
    }

    public static int fontHeight() {
        return font().lineHeight;
    }

    public static boolean contains(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static int withAlpha(int color, int alpha) {
        return (clamp(alpha, 0, 255) << 24) | (color & 0x00FFFFFF);
    }

    public static int multiplyColor(int color, float factor) {
        int alpha = (color >>> 24) & 0xFF;
        int red = clamp(Math.round(((color >>> 16) & 0xFF) * factor), 0, 255);
        int green = clamp(Math.round(((color >>> 8) & 0xFF) * factor), 0, 255);
        int blue = clamp(Math.round((color & 0xFF) * factor), 0, 255);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static float animate(float current, float target, float smoothing) {
        return current + ((target - current) * clamp(smoothing, 0.0F, 1.0F));
    }

    public static int lerpColor(int from, int to, float delta) {
        float clamped = clamp(delta, 0.0F, 1.0F);
        int fromA = (from >>> 24) & 0xFF;
        int fromR = (from >>> 16) & 0xFF;
        int fromG = (from >>> 8) & 0xFF;
        int fromB = from & 0xFF;
        int toA = (to >>> 24) & 0xFF;
        int toR = (to >>> 16) & 0xFF;
        int toG = (to >>> 8) & 0xFF;
        int toB = to & 0xFF;
        int alpha = clamp(Math.round(fromA + ((toA - fromA) * clamped)), 0, 255);
        int red = clamp(Math.round(fromR + ((toR - fromR) * clamped)), 0, 255);
        int green = clamp(Math.round(fromG + ((toG - fromG) * clamped)), 0, 255);
        int blue = clamp(Math.round(fromB + ((toB - fromB) * clamped)), 0, 255);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static void scaledText(GuiGraphics guiGraphics, String text, float x, float y, float scale, int color, boolean shadow) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.drawString(font(), text, 0, 0, color, shadow);
        guiGraphics.pose().popMatrix();
    }

    public static void scaledCenteredText(GuiGraphics guiGraphics, String text, float centerX, float centerY, float scale, int color, boolean shadow) {
        float width = textWidth(text) * scale;
        float height = fontHeight() * scale;
        scaledText(guiGraphics, text, centerX - (width / 2.0F), centerY - (height / 2.0F), scale, color, shadow);
    }

    private static Font font() {
        return Minecraft.getInstance().font;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
