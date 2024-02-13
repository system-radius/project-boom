package com.radius.system.screens.ui.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.BonusType;
import com.radius.system.events.parameters.StatChangeEvent;

public class BoomHUDValue extends HeadsUpValueDisplay {

    private static final TextureRegion[] ICONS = GlobalAssets.GetFrames(GlobalAssets.BLOCKS_TEXTURE_PATH, GlobalAssets.BLOCKS_TEXTURE_REGION_SIZE, GlobalAssets.BLOCKS_TEXTURE_REGION_SIZE, 7);

    private final BonusType bonusType;

    public BoomHUDValue(BonusType type, float x, float y, float width, float height) {
        super(ICONS[type.GetType()], x, y, width, height);
        this.bonusType = type;
    }

    @Override
    public void OnStatChange(StatChangeEvent event) {
        if (!event.bonusType.equals(bonusType)) {
            return;
        }

        DeriveValue(event.value);
    }
}
