package net.srodix.client.ui.sdf;

import com.mojang.blaze3d.platform.NativeImage;

enum SdfTextureKind {
    RECT {
        @Override
        NativeImage create(SdfTextureKey key) {
            NativeImage image = new NativeImage(key.width(), key.height(), true);
            float halfWidth = key.width() / 2.0F;
            float halfHeight = key.height() / 2.0F;
            float radius = Math.min(key.cornerRadius(), Math.min(halfWidth, halfHeight));

            for (int y = 0; y < key.height(); y++) {
                for (int x = 0; x < key.width(); x++) {
                    float signedDistance = roundedRectSdf(x + 0.5F, y + 0.5F, halfWidth, halfHeight, radius);
                    float outerAlpha = coverageForSignedDistance(signedDistance, key.softness());
                    float innerAlpha = key.borderWidth() > 0.0F
                        ? coverageForSignedDistance(signedDistance + key.borderWidth(), key.softness())
                        : outerAlpha;

                    int argb = blend(
                        scaleAlpha(key.borderColor(), outerAlpha),
                        scaleAlpha(key.fillColor(), innerAlpha)
                    );
                    image.setPixelABGR(x, y, argbToAbgr(argb));
                }
            }

            return image;
        }
    },
    SHADOW {
        @Override
        NativeImage create(SdfTextureKey key) {
            NativeImage image = new NativeImage(key.width(), key.height(), true);
            float centerX = (key.width() / 2.0F) - key.offsetX();
            float centerY = (key.height() / 2.0F) - key.offsetY();
            float halfWidth = (key.width() - key.softness() * 2.0F) / 2.0F;
            float halfHeight = (key.height() - key.softness() * 2.0F) / 2.0F;
            float radius = Math.min(key.cornerRadius(), Math.min(halfWidth, halfHeight));

            for (int y = 0; y < key.height(); y++) {
                for (int x = 0; x < key.width(); x++) {
                    float signedDistance = roundedRectSdf(x + 0.5F, y + 0.5F, centerX, centerY, halfWidth, halfHeight, radius);
                    float alpha = coverageForSignedDistance(signedDistance, key.softness());
                    int argb = scaleAlpha(key.fillColor(), alpha);
                    image.setPixelABGR(x, y, argbToAbgr(argb));
                }
            }

            return image;
        }
    };

    abstract NativeImage create(SdfTextureKey key);

    static float roundedRectSdf(float pixelX, float pixelY, float halfWidth, float halfHeight, float radius) {
        return roundedRectSdf(pixelX, pixelY, halfWidth, halfHeight, halfWidth, halfHeight, radius);
    }

    static float roundedRectSdf(float pixelX, float pixelY, float centerX, float centerY, float halfWidth, float halfHeight, float radius) {
        float localX = Math.abs(pixelX - centerX) - (halfWidth - radius);
        float localY = Math.abs(pixelY - centerY) - (halfHeight - radius);
        float outsideX = Math.max(localX, 0.0F);
        float outsideY = Math.max(localY, 0.0F);
        float outsideDistance = (float) Math.sqrt((outsideX * outsideX) + (outsideY * outsideY));
        float insideDistance = Math.min(Math.max(localX, localY), 0.0F);
        return outsideDistance + insideDistance - radius;
    }

    static float coverageForSignedDistance(float signedDistance, float softness) {
        float normalized = 0.5F - (signedDistance / softness);
        return clamp(normalized, 0.0F, 1.0F);
    }

    static int scaleAlpha(int color, float alphaFactor) {
        int alpha = Math.round(((color >>> 24) & 0xFF) * clamp(alphaFactor, 0.0F, 1.0F));
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    static int blend(int background, int foreground) {
        float fgAlpha = ((foreground >>> 24) & 0xFF) / 255.0F;
        float bgAlpha = ((background >>> 24) & 0xFF) / 255.0F;
        float outAlpha = fgAlpha + (bgAlpha * (1.0F - fgAlpha));
        if (outAlpha <= 0.0F) {
            return 0;
        }

        float fgRed = ((foreground >>> 16) & 0xFF) / 255.0F;
        float fgGreen = ((foreground >>> 8) & 0xFF) / 255.0F;
        float fgBlue = (foreground & 0xFF) / 255.0F;

        float bgRed = ((background >>> 16) & 0xFF) / 255.0F;
        float bgGreen = ((background >>> 8) & 0xFF) / 255.0F;
        float bgBlue = (background & 0xFF) / 255.0F;

        int alpha = Math.round(outAlpha * 255.0F);
        int red = Math.round((((fgRed * fgAlpha) + (bgRed * bgAlpha * (1.0F - fgAlpha))) / outAlpha) * 255.0F);
        int green = Math.round((((fgGreen * fgAlpha) + (bgGreen * bgAlpha * (1.0F - fgAlpha))) / outAlpha) * 255.0F);
        int blue = Math.round((((fgBlue * fgAlpha) + (bgBlue * bgAlpha * (1.0F - fgAlpha))) / outAlpha) * 255.0F);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    static int argbToAbgr(int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        int red = (argb >>> 16) & 0xFF;
        int green = (argb >>> 8) & 0xFF;
        int blue = argb & 0xFF;
        return (alpha << 24) | (blue << 16) | (green << 8) | red;
    }

    static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
