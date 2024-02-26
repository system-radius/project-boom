package com.radius.system.screens.game_ui.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.radius.system.events.listeners.StatChangeListener;

public abstract class HeadsUpDisplayIcon extends Actor implements StatChangeListener {

    protected TextureRegion icon;

    protected boolean enabled;

    public HeadsUpDisplayIcon(TextureRegion texture, float x, float y, float width, float height) {
        this.icon = texture;
        setX(x); setY(y); setWidth(width); setHeight(height);
        enabled = true;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (!enabled) {
            return;
        }
        batch.draw(icon, getX(), getY(), getWidth(), getHeight());
    }

}
