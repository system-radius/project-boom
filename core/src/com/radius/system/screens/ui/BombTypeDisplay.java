package com.radius.system.screens.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.BombType;
import com.radius.system.enums.BonusType;
import com.radius.system.objects.bombs.Bomb;

public class BombTypeDisplay extends Actor {

    protected final TextureRegion icon;
    protected boolean disabled;

    public BombTypeDisplay(BombType type, float x, float y, float width, float height) {
        icon = GlobalAssets.GetFrame(GlobalAssets.BOMB_TEXTURE_PATH, GlobalAssets.BOMB_TEXTURE_REGION_SIZE, GlobalAssets.BOMB_TEXTURE_REGION_SIZE, type.GetType(), 1);
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        Disable();
    }

    public void Disable() {
        disabled = true;
    }

    public void Enable() {
        disabled = false;
    }


    @Override
    public void draw(Batch batch, float alpha) {
        if (disabled) {
            return;
        }
        batch.draw(icon, getX(), getY(), getWidth(), getHeight());
    }
}
