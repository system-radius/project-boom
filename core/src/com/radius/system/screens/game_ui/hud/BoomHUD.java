package com.radius.system.screens.game_ui.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BonusType;
import com.radius.system.enums.ButtonType;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.StatChangeListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;

public class BoomHUD extends HeadsUpDisplay implements ButtonPressListener {

    private final String gamePaused = "GAME PAUSED";

    private final BitmapFont pausedTextRenderer;

    private final Texture background;

    private final List<StatChangeListener> statChangeListeners = new ArrayList<>();

    private boolean isPaused;

    public BoomHUD(float x, float y, float width, float height) {
        super(x, y, width, height);
        background = GlobalAssets.LoadTexture(GlobalAssets.BACKGROUND_TEXTURE_PATH);
        pausedTextRenderer = FontUtils.GetFont((int)(GlobalConstants.WORLD_SCALE * 0.75f), Color.WHITE, 3, Color.BLACK);
    }

    public void AddItem(BonusType bonusType) {
        float size = getHeight() / 1.5f;
        int distanceMultiplier = 4;
        float positionX = size * ((getChildren().size * distanceMultiplier) + 1);
        float positionY = getHeight() / 2 - size / 2;

        HeadsUpDisplayIcon item;

        switch (bonusType) {
            case LIFE:
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
        if (!isPaused) {
            super.draw(batch, alpha);
        } else {
            batch.setColor(1, 1, 1, 1);
            pausedTextRenderer.draw(batch, gamePaused, getX() + GlobalConstants.WORLD_SCALE / 2, getY() + getHeight() * 0.75f);
        }
    }

    @Override
    public void dispose() {
        background.dispose();
    }

    @Override
    public void OnButtonPress(ButtonPressEvent event) {
        switch (event.buttonType) {
            case PAUSE:
                isPaused = true;
                break;
            case PLAY:
            case RESTART:
                isPaused = false;
                break;
        }
    }
}
