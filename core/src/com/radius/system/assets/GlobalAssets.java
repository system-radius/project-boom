package com.radius.system.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class GlobalAssets {

    public static final String[] PLAYER_TEXTURE_PATHS = new String[]{
            "img/player_1.png", "img/player_2.png", "img/player_3.png", "img/player_4.png",
    };

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

    public static final String BUTTON_CANCEL_TEXTURE_PATH = "img/cancel.png";

    public static final String BACKGROUND_TEXTURE_PATH = "img/background.png";

    public static final String WHITE_SQUARE = "img/background_white.png";

    public static final String WARNING_SIGN_PATH = "img/warning_sign.png";

    public static final String BURN_TEST_PATH = "field/burn_test.png";

    /* * * * * * * * * SFX  PATHS * * * * * */
    public static final String EXPLOSION_SFX_PATH = "sfx/ex.wav";

    public static final String BOMB_SET_SFX_PATH = "sfx/set.wav";

    public static final String BONUS_GET_SFX_PATH = "sfx/get.wav";

    public static final String PLAYER_BURN_SFX_PATH = "sfx/dead.wav";
    /* * * * * * * * * * *  * * * * * * * * */

    /* * * * * * * CONFIG RELATED * * * * * */

    public static final String DEATH_MATCH_MODE_PATH = "mode_banners/death_match.png";

    public static final String CLASSIC_MODE_PATH = "mode_banners/classic.png";

    public static final String CFT_MODE_PATH = "mode_banners/cft.png";

    public static final String FORWARD_TEXTURE_PATH = "img/forward.png";

    public static final String BACKWARD_TEXTURE_PATH = "img/backward.png";

    /* * * * * * * * * * * * * * * * * * * */


    private static final Map<String, Texture> textureMap = new HashMap<>();

    private static final Map<String, Sound> soundMap = new HashMap<>();

    private static final Map<String, TextureRegion[][]> textureRegionMap = new HashMap<>();

    private static final Map<String, Long> soundTimerMap = new HashMap<>();

    private GlobalAssets() {

    }

    public static void PreLoad() {
        for (String path : PLAYER_TEXTURE_PATHS) {
            LoadTexture(path);
        }

        LoadTexture(BOMB_TEXTURE_PATH);
        LoadTexture(FIRE_TEXTURE_PATH);
        LoadTexture(BLOCKS_TEXTURE_PATH);
        LoadTexture(WARNING_SIGN_PATH);
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

    public static Sound LoadSound(String path) {
        if (soundMap.containsKey(path)) {
            return soundMap.get(path);
        }

        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        soundMap.put(path, sound);
        return sound;
    }

    public static void PlaySound(String path) {
        long millis = System.currentTimeMillis();
        long lastMillis = -1L;
        if (soundTimerMap.containsKey(path)) {
            lastMillis = soundTimerMap.get(path);
        }
        if (millis - lastMillis < 100) {
            return;
        }

        soundTimerMap.put(path, millis);
        LoadSound(path).play();
    }

    public static void Dispose() {
        for (String key : textureMap.keySet()) {
            textureMap.get(key).dispose();
        }

        for (String key : soundMap.keySet()) {
            soundMap.get(key).dispose();
        }
    }

}
