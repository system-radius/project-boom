package com.radius.system.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class AbstractScreen extends ScreenAdapter {

    private SpriteBatch spriteBatch = new SpriteBatch();

    protected Color bgColor = Color.GRAY;

    protected int screenWidth = Gdx.graphics.getWidth();

    protected int screenHeight = Gdx.graphics.getHeight();

    public void ClearScreen() {
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public abstract void Update(float delta);

    public abstract void Draw(SpriteBatch spriteBatch);

    @Override
    public final void render(float delta) {
        Update(delta);
        ClearScreen();
        Draw(spriteBatch);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }

}
