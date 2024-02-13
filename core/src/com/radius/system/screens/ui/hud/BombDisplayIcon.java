package com.radius.system.screens.ui.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.BonusType;
import com.radius.system.events.parameters.StatChangeEvent;

public class BombDisplayIcon extends HeadsUpDisplayIcon {

    private static final TextureRegion[] ICONS = GlobalAssets.GetFrames(GlobalAssets.BLOCKS_TEXTURE_PATH, GlobalAssets.BLOCKS_TEXTURE_REGION_SIZE, GlobalAssets.BLOCKS_TEXTURE_REGION_SIZE, 7);

    private static final TextureRegion[][] BOMBS = GlobalAssets.LoadTextureRegion(GlobalAssets.BOMB_TEXTURE_PATH, GlobalAssets.BOMB_TEXTURE_REGION_SIZE, GlobalAssets.BOMB_TEXTURE_REGION_SIZE);

    private final int displayIndex = 1;

    public BombDisplayIcon(BonusType bonusType, float x, float y, float width, float height) {
        super(ICONS[bonusType.GetType()], x, y, width, height);
        icon = BOMBS[0][displayIndex];
    }

    @Override
    public void OnStatChange(StatChangeEvent event) {
        if (event.bonusType.GetType() <= BonusType.MOVEMENT_SPEED.GetType()) {
            return;
        }

        icon = BOMBS[event.value][displayIndex];
    }
}
