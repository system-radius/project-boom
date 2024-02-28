package com.radius.system.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.configs.GameConfig;
import com.radius.system.events.listeners.ExitGameListener;
import com.radius.system.events.listeners.StartGameListener;
import com.radius.system.screens.config_ui.ConfigStage;

public class ConfigScreen extends AbstractScreen implements ExitGameListener {

    private final ConfigStage configStage;

    private final OrthographicCamera uiCamera;

    private final Viewport uiViewport;

    private boolean hasStartGameListener;
    public ConfigScreen() {
        uiCamera = new OrthographicCamera();

        float viewportWidth = GlobalConstants.VIEWPORT_WIDTH, viewportHeight = GlobalConstants.VIEWPORT_HEIGHT, worldScale = GlobalConstants.WORLD_SCALE;
        uiViewport = new ExtendViewport(viewportWidth * worldScale, viewportHeight * worldScale, uiCamera);

        configStage = new ConfigStage(uiViewport, worldScale);
        Gdx.input.setInputProcessor(configStage);
    }

    public void AddStartGameListener(StartGameListener listener) {
        configStage.AddStartGameListener(listener);
        hasStartGameListener = true;
    }

    @Override
    public void resize(int width, int height) {
        configStage.Resize();
    }

    @Override
    public void Update(float delta) {
        configStage.act(delta);
    }

    @Override
    public void Draw(SpriteBatch spriteBatch) {
        if (!hasStartGameListener) return;
        configStage.draw();
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {

    }

    @Override
    public void OnExitGame() {
        Gdx.input.setInputProcessor(configStage);
        configStage.ResetState();
    }
}
