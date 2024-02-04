package com.radius.system.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.objects.GameObject;

public class Joystick extends GameObject implements Disposable {

    public boolean dynamicPosition = false;

    private final Texture joystickOuter;

    private final Texture joystickFill;

    private final float scale;

    private final float outerSizeMultiplier = 3f;

    private final float innerSizeMultiplier = outerSizeMultiplier / 2;

    private final float fourthSizeMultiplier = innerSizeMultiplier / 2;

    private final Vector3 innerJoystickPosition;

    public Joystick(float x, float y, float scale) {
        super(x, y);
        innerJoystickPosition = new Vector3();
        this.scale = scale;
        joystickOuter = new Texture(Gdx.files.internal("img/JoystickOuter.png"));
        joystickFill = new Texture(Gdx.files.internal("img/JoystickFill.png"));
    }

    public void SetPosition(float x, float y, boolean force) {

        if (dynamicPosition || force) {
            this.x = x - innerSizeMultiplier * scale;
            this.y = y - innerSizeMultiplier * scale;
        }

        this.innerJoystickPosition.x = this.x + fourthSizeMultiplier * scale;
        this.innerJoystickPosition.y = this.y + fourthSizeMultiplier * scale;

    }

    public Vector3 SetDragPosition(float x, float y) {
        x -= fourthSizeMultiplier * scale;
        y -= fourthSizeMultiplier * scale;

        float dx = (x - this.x) - fourthSizeMultiplier * scale;
        float dy = (y - this.y) - fourthSizeMultiplier * scale;


        float dx2 = dx * dx;
        float dy2 = dy * dy;
        float c = (float)Math.sqrt(dx2 + dy2);

        if (c >= innerSizeMultiplier * scale) {
            float tan = MathUtils.atan2(dy, dx);

            this.innerJoystickPosition.x = this.x + (innerSizeMultiplier * scale * MathUtils.cos(tan)) + fourthSizeMultiplier * scale;
            this.innerJoystickPosition.y = this.y + (innerSizeMultiplier * scale * MathUtils.sin(tan)) + fourthSizeMultiplier * scale;
        } else {
            this.innerJoystickPosition.x = x;
            this.innerJoystickPosition.y = y;
        }

        return this.innerJoystickPosition;
    }

    public float GetInnerSizeMultiplier() {
        return innerSizeMultiplier;
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(Batch batch) {
        batch.draw(joystickOuter, x, y, outerSizeMultiplier * scale, outerSizeMultiplier * scale);
        batch.draw(joystickFill, innerJoystickPosition.x, innerJoystickPosition.y, innerSizeMultiplier * scale, innerSizeMultiplier * scale);
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {

    }

    @Override
    public void dispose() {
        joystickOuter.dispose();
        joystickFill.dispose();
    }
}
