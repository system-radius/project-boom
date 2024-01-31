package com.radius.system.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.radius.system.events.CoordinateEventListener;

public class GameCamera extends OrthographicCamera implements CoordinateEventListener {

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private final float worldWidth;

    private final float worldHeight;

    private final float scale;

    private float zoom = 0.35f;

    public GameCamera(float worldWidth, float worldHeight, float scale) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        this.scale = scale;
    }

    public void SetZoom(float zoom) {
        this.zoom = zoom;
    }

    @Override
    public void Trigger(float x, float y) {
        x *= scale;
        y *= scale;

        float effectiveViewportWidth = this.viewportWidth / EFFECTIVE_VIEWPORT_DIVIDER;
        float effectiveViewportHeight = this.viewportHeight / EFFECTIVE_VIEWPORT_DIVIDER;

        this.position.x = MathUtils.clamp(x, effectiveViewportWidth, (worldWidth * scale) - effectiveViewportWidth);
        this.position.y = MathUtils.clamp(y, effectiveViewportHeight, (worldHeight * scale) - effectiveViewportHeight);

        this.update();
        System.out.println("Triggered camera movement (" + position.x + ", " + position.y + ")");
    }
}
