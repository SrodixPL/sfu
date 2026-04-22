package net.srodix.client.ui.sdf;

record SdfTextureKey(
    int width,
    int height,
    int sampleScale,
    SdfTextureKind kind,
    int fillColor,
    int borderColor,
    float cornerRadius,
    float borderWidth,
    float softness,
    int offsetX,
    int offsetY
) {
}
