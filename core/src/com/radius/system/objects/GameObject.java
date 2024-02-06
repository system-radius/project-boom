package com.radius.system.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * A representation of objects in the game. Has position and velocity.
 * Required to be updated and drawn.
 */
public abstract class GameObject implements Disposable {

    public final Vector2 position;

    public GameObject(float x, float y) {
        position = new Vector2(x,  y);
    }

    public static int GetWorldPosition(float c, float scale) {
        float excess = (c * scale) % scale >= scale / 2 ? 1 : 0;
        return (int)(c + excess);
    }

}
