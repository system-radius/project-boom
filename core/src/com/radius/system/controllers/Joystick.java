package com.radius.system.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.objects.GameObject;

public class Joystick extends GameObject implements Disposable {

    private final Texture joystickOuter;

    private final Texture joystickFill;

    private final float scale;

    private final Vector2 innerJoystickPosition;

    public Joystick(float x, float y, float scale) {
        super(x, y);
        innerJoystickPosition = new Vector2();
        this.scale = scale;
        joystickOuter = new Texture(Gdx.files.internal("img/JoystickOuter.png"));
        joystickFill = new Texture(Gdx.files.internal("img/JoystickFill.png"));
    }

    public void SetPosition(float x, float y) {
        this.x = x - 2 * scale;
        this.innerJoystickPosition.x = this.x + scale;
        this.y = y - 2 * scale;
        this.innerJoystickPosition.y = this.y + scale;
    }

    public void SetDragPosition(float x, float y) {
        this.innerJoystickPosition.x = x - scale;
        this.innerJoystickPosition.y = y - scale;

        float tempX = (this.x - this.innerJoystickPosition.x);
        tempX *= tempX;

        float tempY = (this.y - this.innerJoystickPosition.y);
        tempY *= tempY;


        //mainCamera.position.x = MathUtils.clamp(mainCamera.position.x, effectiveViewportWidth, scaledWorldWidth - effectiveViewportWidth);
        //mainCamera.position.y = MathUtils.clamp(mainCamera.position.y, effectiveViewportHeight, scaledWorldHeight - effectiveViewportHeight);

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
