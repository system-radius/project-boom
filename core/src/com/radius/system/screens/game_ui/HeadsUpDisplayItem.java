package com.radius.system.screens.game_ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.BonusType;

public class HeadsUpDisplayItem extends Actor {

    public static final TextureRegion[][] SYMBOLS = GlobalAssets.LoadTextureRegion(GlobalAssets.SYMBOLS_TEXTURE_PATH, GlobalAssets.SYMBOLS_TEXTURE_REGION_SIZE, GlobalAssets.SYMBOLS_TEXTURE_REGION_SIZE);

    protected static final TextureRegion[] ICONS = GlobalAssets.GetFrames(GlobalAssets.BLOCKS_TEXTURE_PATH, GlobalAssets.BLOCKS_TEXTURE_REGION_SIZE, GlobalAssets.BLOCKS_TEXTURE_REGION_SIZE, 7);

    protected final TextureRegion icon;

    private final TextureRegion colon;

    protected boolean disabled, disableCount = false;

    private TextureRegion tens, ones;

    public HeadsUpDisplayItem(BonusType type, float x, float y, float width, float height) {
        this.icon = ICONS[type.GetType()];
        this.colon = SYMBOLS[5][0];
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        SetValue(0);

    }

    private void DeriveDisplay(int value) {
        int onesValue = value % 10;
        int tensValue = value / 10;

        ones = SYMBOLS[0][onesValue];
        tens = SYMBOLS[0][tensValue];
    }

    public HeadsUpDisplayItem EnableCount() {
        disableCount = false;
        return this;
    }

    public HeadsUpDisplayItem DisableCount() {
        disableCount = true;
        return this;
    }

    public void SetValue(int value) {
        if (!disableCount) {
            DeriveDisplay(value);
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (disabled) {
            return;
        }
        batch.draw(icon, getX(), getY(), getWidth(), getHeight());

        if (!disableCount) {
            DrawItem(batch, colon, 1);
            DrawItem(batch, tens, 2);
            DrawItem(batch, ones, 3);
        }
    }

    private void DrawItem(Batch batch, TextureRegion texture, int index) {
        batch.draw(texture, getX() + (getWidth() * index) / 1.5f, getY(), getWidth(), getHeight());
    }
}
