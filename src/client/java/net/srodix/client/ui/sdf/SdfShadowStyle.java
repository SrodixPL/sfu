package net.srodix.client.ui.sdf;

public record SdfShadowStyle(
    int color,
    float cornerRadius,
    float blurRadius,
    int offsetX,
    int offsetY
) {
    public SdfShadowStyle {
        cornerRadius = Math.max(0.0F, cornerRadius);
        blurRadius = Math.max(1.0F, blurRadius);
    }
}
