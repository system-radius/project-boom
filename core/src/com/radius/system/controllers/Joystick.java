package com.radius.system.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.objects.GameObject;

public class Joystick extends GameObject implements Disposable {

    private final Texture joystickOuter;

    private final Texture joystickFill;

    private final float scale;

    public Joystick(float x, float y, float scale) {
        super(x, y);
        this.scale = scale;
        joystickOuter = new Texture(Gdx.files.internal("img/JoystickOuter.png"));
        joystickFill = new Texture(Gdx.files.internal("img/JoystickFill.png"));
    }

    public void SetPosition(float x, float y) {
        this.x = x; this.y = y;

        //System.out.println(x + ", " + y);
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(SpriteBatch batch) {
        batch.draw(joystickOuter, x, y, 4 * scale, 4 * scale);
        batch.draw(joystickFill, x + scale, y + scale, 2 * scale, 2 * scale);
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
