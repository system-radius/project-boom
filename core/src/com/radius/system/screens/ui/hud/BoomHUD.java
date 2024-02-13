package com.radius.system.screens.ui.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.BonusType;
import com.radius.system.events.listeners.StatChangeListener;

import java.util.ArrayList;
import java.util.List;

public class BoomHUD extends HeadsUpDisplay {

    private final Texture background;

    private final List<StatChangeListener> statChangeListeners = new ArrayList<>();

    public BoomHUD(float x, float y, float width, float height) {
        super(x, y, width, height);
        background = GlobalAssets.LoadTexture(GlobalAssets.BACKGROUND_TEXTURE_PATH);
    }

    public void AddItem(BonusType bonusType) {
        float size = getHeight() / 1.5f;
        int distanceMultiplier = 4;
        float positionX = size * ((getChildren().size * distanceMultiplier) + 1);
        float positionY = getHeight() / 2 - size / 2;

        HeadsUpDisplayIcon item;

        switch (bonusType) {
            case BOMB_STOCK:
            case FIRE_POWER:
            case FLASH_FIRE:
            case MOVEMENT_SPEED:
                item = new BoomHUDValue(bonusType, positionX, positionY, size, size);
                break;
            default:
                item = new BombDisplayIcon(bonusType, positionX, positionY, size, size);
        }

        statChangeListeners.add(item);
        addActor(item);
    }

    public List<StatChangeListener> GetStatChangeListeners() {
        return statChangeListeners;
    }

    @Override
    public void Resize(float x, float y) {

    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(background, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, alpha);
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
