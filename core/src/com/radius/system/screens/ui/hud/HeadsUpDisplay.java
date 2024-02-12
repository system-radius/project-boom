package com.radius.system.screens.ui.hud;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;

public abstract class HeadsUpDisplay extends Group implements Disposable {

    public HeadsUpDisplay(float x, float y, float width, float height) {
        setX(x); setY(y); setWidth(width); setHeight(height);
    }

    public abstract void Resize(float x, float y);

}
