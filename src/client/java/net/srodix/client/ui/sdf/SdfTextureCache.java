package net.srodix.client.ui.sdf;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

final class SdfTextureCache {
    private static final int MAX_TEXTURES = 128;

    private final Map<SdfTextureKey, CachedTexture> cache = new LinkedHashMap<>(32, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<SdfTextureKey, CachedTexture> eldest) {
            if (size() <= MAX_TEXTURES) {
                return false;
            }

            release(eldest.getValue());
            return true;
        }
    };

    CachedTexture getOrCreate(SdfTextureKey key) {
        CachedTexture cached = this.cache.get(key);
        if (cached != null) {
            return cached;
        }

        NativeImage image = key.kind().create(key);
        DynamicTexture texture = new DynamicTexture(() -> "sfu_sdf_" + Integer.toHexString(key.hashCode()), image);
        texture.upload();

        Identifier id = Identifier.parse("sfu:sdf/" + Integer.toHexString(key.hashCode()));
        Minecraft.getInstance().getTextureManager().register(id, texture);

        CachedTexture created = new CachedTexture(id, texture, key.width(), key.height());
        this.cache.put(key, created);
        return created;
    }

    void clear() {
        for (CachedTexture texture : this.cache.values()) {
            release(texture);
        }

        this.cache.clear();
    }

    private void release(CachedTexture texture) {
        Minecraft.getInstance().getTextureManager().release(texture.id());
        texture.texture().close();
    }

    record CachedTexture(Identifier id, DynamicTexture texture, int pixelWidth, int pixelHeight) {
    }
}
