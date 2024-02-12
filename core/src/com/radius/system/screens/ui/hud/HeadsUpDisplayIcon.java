package com.radius.system.screens.ui.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HeadsUpDisplayIcon extends Actor {

    protected Texture icon;

    public HeadsUpDisplayIcon(Texture texture, float x, float y, float width, float height) {
        this.icon = texture;
        setX(x); setY(y); setWidth(width); setHeight(height);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(icon, getX(), getY(), getWidth(), getHeight());
    }

}
