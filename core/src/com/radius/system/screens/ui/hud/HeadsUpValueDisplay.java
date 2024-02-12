package com.radius.system.screens.ui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.assets.GlobalAssets;

public abstract class HeadsUpValueDisplay extends HeadsUpDisplayIcon {

    protected static final TextureRegion[][] SYMBOLS = GlobalAssets.LoadTextureRegion(GlobalAssets.SYMBOLS_TEXTURE_PATH, GlobalAssets.SYMBOLS_TEXTURE_REGION_SIZE, GlobalAssets.SYMBOLS_TEXTURE_REGION_SIZE);

    protected TextureRegion tens, ones, colon;

    public HeadsUpValueDisplay(TextureRegion region, float x, float y, float width, float height) {
        super(region, x, y, width, height);
        colon = SYMBOLS[5][0];
        DeriveValue(0);
    }

    protected void DeriveValue(int value) {
        int onesValue = value % 10;
        int tensValue = value / 10;

        ones = SYMBOLS[0][onesValue];
        tens = SYMBOLS[0][tensValue];
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (!enabled) {
            return;
        }

        DrawItem(batch, colon, 1);
        DrawItem(batch, tens, 2);
        DrawItem(batch, ones, 3);
        super.draw(batch, alpha);
    }

    private void DrawItem(Batch batch, TextureRegion texture, int index) {
        batch.draw(texture, getX() + (getWidth() * index) / 1.5f, getY(), getWidth(), getHeight());
    }
}
