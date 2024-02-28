package com.radius.system.screens.config_ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.radius.system.assets.GlobalAssets;

public abstract class GamePanel extends Group {

    protected final Texture background;

    private Color bgColor = Color.CLEAR;

    private float alpha = 0.5f;

    public GamePanel(float x, float y, float width, float height) {
        setX(x); setY(y); setWidth(width); setHeight(height);
        background = GlobalAssets.LoadTexture(GlobalAssets.WHITE_SQUARE);
    }

    public void SetAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void SetBGColor(Color color) {
        this.bgColor = color;
    }

    public abstract void Resize();

    @Override
    public final void draw(Batch batch, float alpha) {
        batch.setColor(bgColor.r, bgColor.g, bgColor.b, this.alpha);
        batch.draw(background, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
        super.draw(batch, alpha);

        Draw(batch);
    }

    protected void Draw(Batch batch) {

    }

}
