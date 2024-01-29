package com.radius.system.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.objects.GameObject;

public class Joystick extends GameObject implements Disposable {

    private final Texture joystickOuter;

    private final Texture joystickFill;

    private final float scale;

    private final Vector3 innerJoystickPosition;

    public Joystick(float x, float y, float scale) {
        super(x, y);
        innerJoystickPosition = new Vector3();
        this.scale = scale;
        joystickOuter = new Texture(Gdx.files.internal("img/JoystickOuter.png"));
        joystickFill = new Texture(Gdx.files.internal("img/JoystickFill.png"));
    }

    public void SetPosition(float x, float y) {
        this.x = x - 2 * scale;
        this.y = y - 2 * scale;
        this.innerJoystickPosition.x = this.x + scale;
        this.innerJoystickPosition.y = this.y + scale;
    }

    public Vector3 SetDragPosition(float x, float y) {
        x -= scale;
        y -= scale;

        float dx = (x - this.x) - scale;
        float dy = (y - this.y) - scale;


        float dx2 = dx * dx;
        float dy2 = dy * dy;
        float c = (float)Math.sqrt(dx2 + dy2);

        if (c >= 2 * scale) {
            float tan = MathUtils.atan2(dy, dx);

            this.innerJoystickPosition.x = this.x + (2 * scale * MathUtils.cos(tan)) + scale;
            this.innerJoystickPosition.y = this.y + (2 * scale * MathUtils.sin(tan)) + scale;
        } else {
            this.innerJoystickPosition.x = x;
            this.innerJoystickPosition.y = y;
        }

        return this.innerJoystickPosition;
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(SpriteBatch batch) {
        batch.draw(joystickOuter, x, y, 4 * scale, 4 * scale);
        batch.draw(joystickFill, innerJoystickPosition.x, innerJoystickPosition.y, 2 * scale, 2 * scale);
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
