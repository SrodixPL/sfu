package net.srodix.client.ui.sdf;

public record SdfRectStyle(
    int fillColor,
    int borderColor,
    float cornerRadius,
    float borderWidth,
    float edgeSoftness
) {
    public SdfRectStyle {
        cornerRadius = Math.max(0.0F, cornerRadius);
        borderWidth = Math.max(0.0F, borderWidth);
        edgeSoftness = Math.max(0.5F, edgeSoftness);
    }

    public static SdfRectStyle filled(int fillColor, float cornerRadius) {
        return new SdfRectStyle(fillColor, 0x00000000, cornerRadius, 0.0F, 1.25F);
    }

    public static SdfRectStyle bordered(int fillColor, int borderColor, float cornerRadius, float borderWidth) {
        return new SdfRectStyle(fillColor, borderColor, cornerRadius, borderWidth, 1.25F);
    }
}
