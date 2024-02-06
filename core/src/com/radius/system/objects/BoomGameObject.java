package com.radius.system.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.enums.BoardRep;

public abstract class BoomGameObject extends GameObject implements BoomUpdatable, BoomDrawable, Burnable {

    public final Vector2 size;

    public final Vector2 scaledPosition;

    protected BoardRep rep;

    public BoomGameObject(float x, float y, float width, float height) {
        super(x, y);
        this.size = new Vector2(width, height);
        this.scaledPosition = new Vector2(x * width, y * width);
    }

    public int GetWorldX() {
        return GetWorldPosition(position.x, size.x);
    }

    public int GetWorldY() {
        return GetWorldPosition(position.y, size.y);
    }
}
