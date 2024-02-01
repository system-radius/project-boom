package com.radius.system.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.radius.system.events.MovementEventListener;

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
    }

    public void SetWatchId(int watchId) {
        this.watchId = watchId;
    }

    @Override
    public void OnMove(int id, float x, float y) {

        if (watchId != id) {
            return;
        }

        if (zoom < 0.35f) {
            return;
        }

        x *= scale;
        y *= scale;

        float effectiveViewportWidth = this.viewportWidth / EFFECTIVE_VIEWPORT_DIVIDER;
        float effectiveViewportHeight = this.viewportHeight / EFFECTIVE_VIEWPORT_DIVIDER;

        this.position.x = MathUtils.clamp(x, effectiveViewportWidth, (worldWidth * scale) - effectiveViewportWidth);
        this.position.y = MathUtils.clamp(y, effectiveViewportHeight, (worldHeight * scale) - effectiveViewportHeight);

        this.update();
    }
}
