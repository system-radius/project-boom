package com.radius.system.objects.players;

import com.badlogic.gdx.math.Vector2;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BotLevel;

public class PlayerConfig {

    private static final String[] PLAYER_SPRITE_PATHS = new String[]{
        "img/player_1.png", "img/player_2.png", "img/player_3.png", "img/player_4.png",
    };

    private static final Vector2[] PLAYER_SPAWN_POINTS = new Vector2[] {
        new Vector2(1, 1), new Vector2(GlobalConstants.WORLD_WIDTH - 2, GlobalConstants.WORLD_HEIGHT - 2),
            new Vector2(1, GlobalConstants.WORLD_HEIGHT - 2), new Vector2(GlobalConstants.WORLD_WIDTH - 2, 1)
    };

    public BotLevel botLevel = BotLevel.D_CLASS;

    public boolean isHuman = false;

    public int playerSpriteIndex = -1;

    public void RandomizePlayerSprite() {
        playerSpriteIndex = (int)(Math.random() * PLAYER_SPRITE_PATHS.length);
    }

    public String GetSpritePath() {
        return PLAYER_SPRITE_PATHS[playerSpriteIndex];
    }

    public Vector2 GetPlayerSpawnPoint(int index) {
        if (playerSpriteIndex == -1) {
            playerSpriteIndex = index;
        }
        return PLAYER_SPAWN_POINTS[index];
    }

}
