package com.radius.system.screens.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.parameters.MovementEvent;

public class GameCamera extends OrthographicCamera implements MovementEventListener {

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private final float worldWidth;

    private final float worldHeight;

    private final float scale;

    private float zoom = 0.35f;

    private int watchId = 0;

    public GameCamera(float worldWidth, float worldHeight, float scale) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        this.scale = scale;
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

        eventX *= scale;
        eventY *= scale;

        float effectiveViewportWidth = this.viewportWidth / EFFECTIVE_VIEWPORT_DIVIDER;
        float effectiveViewportHeight = this.viewportHeight / EFFECTIVE_VIEWPORT_DIVIDER;

        this.position.x = MathUtils.clamp(eventX, effectiveViewportWidth, (worldWidth * scale) - effectiveViewportWidth);
        this.position.y = MathUtils.clamp(eventY, effectiveViewportHeight, (worldHeight * scale) - effectiveViewportHeight);

        this.update();
    }
}
