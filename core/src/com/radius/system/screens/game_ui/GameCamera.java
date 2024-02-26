package com.radius.system.screens.game_ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.parameters.MovementEvent;

public class GameCamera extends OrthographicCamera implements MovementEventListener {

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private float worldWidth;

    private float worldHeight;

    private final float scale;

    private float zoom = 0.35f;

    private int watchId = 0;

    public GameCamera(float scale) {
        this.worldWidth = 0;
        this.worldHeight = 0;

        this.scale = scale;
    }

    public void SetWorldSize(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        Clamp(worldWidth / 2f, worldHeight / 2f);
    }

    public void SetZoom(float zoom) {
        this.zoom = zoom;
        this.update();
    }

    public void SetWatchId(int watchId) {
        this.watchId = watchId;
    }

    @Override
    public void OnMove(MovementEvent event) {

        if (watchId != event.playerId) {
            return;
        }

        if (zoom < 0.35f) {
            return;
        }

        float eventX = event.x, eventY = event.y;
        Clamp(eventX, eventY);
    }

    private void Clamp(float x, float y) {
        x *= scale;
        y *= scale;

        float effectiveViewportWidth = this.viewportWidth / EFFECTIVE_VIEWPORT_DIVIDER;
        float effectiveViewportHeight = this.viewportHeight / EFFECTIVE_VIEWPORT_DIVIDER;

        this.position.x = MathUtils.clamp(x, effectiveViewportWidth, (worldWidth * scale) - effectiveViewportWidth);
        this.position.y = MathUtils.clamp(y, effectiveViewportHeight, (worldHeight * scale) - effectiveViewportHeight);

        this.update();
    }
}
