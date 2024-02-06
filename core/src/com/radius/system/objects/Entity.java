package com.radius.system.objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.enums.BoardRep;

public abstract class Entity extends AnimatedGameObject {

    public final Vector2 velocity = new Vector2(0, 0);

    public final Vector2 past;

    protected float movementSpeed = 0.75f;

    public Entity(BoardRep rep, float x, float y, float width, float height) {
        super(rep, x, y, width, height);
        past = new Vector2(x, y);
    }

    protected Rectangle RefreshRectangle (Rectangle rectangle, float x, float y, float width, float height) {
        if (rectangle == null) {
            return new Rectangle(x, y, width, height);
        }

        return rectangle.set(x, y, width, height);
    }

    public void Move(float x, float y) {
        velocity.x = x;
        velocity.y = y;
    }

    public void MoveAlongX(float multiplier) {
        velocity.x = (size.x * movementSpeed) * multiplier;
    }

    public void MoveAlongY(float multiplier) {
        velocity.y = (size.y * movementSpeed) * multiplier;
    }

    public void RefreshScaledPosition() {
        scaledPosition.x = position.x * size.x;
        scaledPosition.y = position.y * size.y;
    }

    public boolean UpdatePosition(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        RefreshScaledPosition();
        return HasMoved();
    }

    private boolean HasMoved() {

        int x = GetWorldX();
        int y = GetWorldY();
        boolean hasMoved = past.x != x || past.y != y;

        if (hasMoved) {
            past.x = x;
            past.y = y;
        }

        return hasMoved;
    }
}
