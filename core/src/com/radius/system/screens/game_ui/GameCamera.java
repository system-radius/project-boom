package com.radius.system.screens.game_ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.parameters.MovementEvent;

public class GameCamera extends OrthographicCamera implements MovementEventListener {

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private final Viewport viewport;

    private float worldWidth;

    private float worldHeight;

    private final float scale;

    private float zoom = 0.35f, activeZoom;

    private int watchId = 0;

    public GameCamera(float scale) {
        this.worldWidth = 0;
        this.worldHeight = 0;

        activeZoom = GlobalConstants.MAX_ZOOM;
        float viewportWidth = (GlobalConstants.WORLD_SCALE * GlobalConstants.VIEWPORT_WIDTH) / activeZoom / EFFECTIVE_VIEWPORT_DIVIDER;
        float viewportHeight = (GlobalConstants.WORLD_SCALE * GlobalConstants.VIEWPORT_HEIGHT) / activeZoom / EFFECTIVE_VIEWPORT_DIVIDER;

        viewport = new FitViewport(viewportWidth, viewportHeight, this);
        viewport.apply();

        this.scale = scale;
    }

    public Viewport GetViewport() {
        return viewport;
    }

    public void SetWorldSize(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        AdjustViewport(activeZoom);

        position.x = worldWidth / 2f * scale;
        position.y = worldHeight / 2f * scale;
    }

    public void SetZoom(float zoom) {
        this.zoom = zoom;
        AdjustViewport(zoom);
        //Clamp(worldWidth / 2f, worldWidth / 2f);
        position.x = worldWidth / 2f * scale;
        position.y = worldHeight / 2f * scale;
    }

    private void AdjustViewport(float zoom) {
        this.activeZoom = zoom;
        float viewportWidth = (GlobalConstants.WORLD_SCALE * GlobalConstants.VIEWPORT_WIDTH) / activeZoom / EFFECTIVE_VIEWPORT_DIVIDER;
        float viewportHeight = (GlobalConstants.WORLD_SCALE * GlobalConstants.VIEWPORT_HEIGHT) / activeZoom / EFFECTIVE_VIEWPORT_DIVIDER;

        viewport.setWorldWidth(viewportWidth);
        viewport.setWorldHeight(viewportHeight);
        viewport.apply();
        this.update();
    }

    public void SetWatchId(int watchId) {

        if (watchId < 0) {
            //if (activeZoom == zoom) return;
            AdjustViewport(zoom);
            position.x = worldWidth / 2f * scale;
            position.y = worldHeight / 2f * scale;
            return;
        }

        AdjustViewport(GlobalConstants.MAX_ZOOM);
        this.watchId = watchId;
    }

    @Override
    public void OnMove(MovementEvent event) {

        if (watchId != event.playerId) {
            return;
        }

        if (activeZoom < 0.35f) {
            return;
        }

        float eventX = event.x, eventY = event.y;
        Clamp(eventX, eventY);
    }

    private void Clamp(float x, float y) {
        x *= scale;
        y *= scale;

        float effectiveViewportWidth = viewportWidth / EFFECTIVE_VIEWPORT_DIVIDER;
        float effectiveViewportHeight = viewportHeight / EFFECTIVE_VIEWPORT_DIVIDER;

        this.position.x = MathUtils.clamp(x, effectiveViewportWidth, (worldWidth * scale) - effectiveViewportWidth);
        this.position.y = MathUtils.clamp(y, effectiveViewportHeight, (worldHeight * scale) - effectiveViewportHeight);

        this.update();
    }
}
