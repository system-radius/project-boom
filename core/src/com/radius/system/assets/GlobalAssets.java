package com.radius.system.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class GlobalAssets {

    public static final String BOMB_TEXTURE_PATH = "img/bomb.png";

    public static final int BOMB_TEXTURE_REGION_SIZE = 32;

    public static final String FIRE_TEXTURE_PATH = "img/spiralingFire.png";

    public static final int FIRE_TEXTURE_REGION_SIZE = 64;

    public static final String BLOCKS_TEXTURE_PATH = "img/blocks.png";

    public static final int BLOCKS_TEXTURE_REGION_SIZE = 32;

    public static final String SYMBOLS_TEXTURE_PATH = "img/Lettering.png";

    public static final int SYMBOLS_TEXTURE_REGION_SIZE = 20;

    public static final String BUTTON_A_TEXTURE_PATH = "img/A.png";

    public static final String BUTTON_B_TEXTURE_PATH = "img/B.png";

    public static final String BUTTON_PAUSE_TEXTURE_PATH = "img/pause.png";

    public static final String BUTTON_PLAY_TEXTURE_PATH = "img/play.png";

    public static final String BUTTON_RESTART_TEXTURE_PATH = "img/restart.png";

    private static final Map<String, Texture> textureMap = new HashMap<>();

    private static final Map<String, TextureRegion[][]> textureRegionMap = new HashMap<>();

    private GlobalAssets() {

    }

    public static Texture LoadTexture(String path) {
        if (textureMap.containsKey(path)) {
            return textureMap.get(path);
        }

        Texture texture = new Texture(Gdx.files.internal(path));
        textureMap.put(path, texture);
        return texture;
    }

    public static TextureRegion[][] LoadTextureRegion(String path, int tileWidth, int tileHeight) {
        if (textureRegionMap.containsKey(path)) {
            return textureRegionMap.get(path);
        }

        Texture texture = LoadTexture(path);
        TextureRegion[][] textureRegions = TextureRegion.split(texture, tileWidth, tileHeight);
        textureRegionMap.put(path, textureRegions);
        return textureRegions;
    }

    public static TextureRegion[] GetFrames(String path, int tileWidth, int tileHeight, int row) {
        TextureRegion[][] textureRegion = LoadTextureRegion(path, tileWidth, tileHeight);
        return textureRegion[row];
    }

    public static TextureRegion GetFrame(String path, int tileWidth, int tileHeight, int row, int col) {
        return GetFrames(path, tileWidth, tileHeight, row)[col];
    }

    public static TextureRegion[] GetFrames(String path, int row) {
        if (!textureRegionMap.containsKey(path)) {
            return null;
        }

        TextureRegion[][] regions = textureRegionMap.get(path);
        return regions[row];
    }

    public static TextureRegion GetFrame(String path, int row, int col) {
        TextureRegion[] frames = GetFrames(path, row);

        if (frames == null) {
            return null;
        }

        return frames[col];
    }

    public static void Dispose() {
        for (String key : textureMap.keySet()) {
            textureMap.get(key).dispose();
        }
    }

}
