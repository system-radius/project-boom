package com.radius.system.screens.config_ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.utils.FontUtils;

public class GameConfigHeader extends GamePanel {

    private final BitmapFont headerTitleRenderer;

    private String headerTitle = "WADDUP!";

    public GameConfigHeader(float x, float y, float width, float height) {
        super(x, y, width, height);
        headerTitleRenderer = FontUtils.GetFont((int)(GlobalConstants.WORLD_SCALE * 0.75f), Color.WHITE, 3, Color.BLACK);
    }

    @Override
    public void Resize() {

    }

    public void SetHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    @Override
    public void Draw(Batch batch) {
        headerTitleRenderer.draw(batch, headerTitle, getX() + GlobalConstants.WORLD_SCALE / 2, getY() + getHeight() * 0.75f);
    }
}
