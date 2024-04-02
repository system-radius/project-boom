package com.radius.system.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.radius.system.configs.GameConfig;
import com.radius.system.events.listeners.ExitGameListener;
import com.radius.system.events.listeners.StartGameListener;

public class GameScreen extends AbstractScreen implements StartGameListener {

    public void AddExitGameListener(ExitGameListener listener) {

    }

    @Override
    public void OnGameStart(GameConfig gameConfig) {
        // Initialization function when starting the game.
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(SpriteBatch spriteBatch) {

    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {

    }
}
