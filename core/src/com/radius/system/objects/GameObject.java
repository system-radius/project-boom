package com.radius.system.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A representation of objects in the game. Has position and velocity.
 * Required to be updated and drawn.
 */
public abstract class GameObject {

    protected float x;

    protected float y;

    protected float velX = 0f;

    protected float velY = 0f;

    public GameObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public abstract void Update(float delta);

    public abstract void Draw(Batch batch);

    public abstract void DrawDebug(ShapeRenderer renderer);

    public float GetX() {
        return x;
    }

    public float GetY() {
        return y;
    }

}
