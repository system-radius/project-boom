package com.radius.system.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.enums.ControlKeys;
import com.radius.system.objects.players.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor, Disposable {

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private Player player;

    private Viewport viewport;

    private Camera camera;

    private Joystick joystick;

    private final Vector3 vector;

    private final float scale;

    private boolean isTouching;

    public PlayerController(Player player, Viewport viewport, float scale) {
        this.player = player;
        this.viewport = viewport;
        this.vector = new Vector3();

        this.scale = scale;

        camera = viewport.getCamera();
        joystick = new Joystick(camera.position.x - (Gdx.graphics.getWidth() / 2f), camera.position.y - (Gdx.graphics.getHeight() / 2f), scale);

        for (ControlKeys key: ControlKeys.values()) {
            pressedKeys.put(key, false);
        }
    }

    private boolean ProcessKeyInput(ControlKeys currentKey, float directionality, boolean isPressed) {
        if (pressedKeys.get(currentKey.GetOppositeKey())) {
            return false;
        }
        pressedKeys.put(currentKey, isPressed);

        switch (currentKey) {
            case NORTH:
            case SOUTH:
                return ProcessYKeyInput(directionality, isPressed);
            case EAST:
            case WEST:
                return ProcessXKeyInput(directionality, isPressed);
            default:
                // Do nothing just yet.
        }

        return true;
    }

    private boolean ProcessXKeyInput(float directionality, boolean isPressed) {
        player.SetVelX(isPressed ? directionality : 0);
        return true;
    }

    private boolean ProcessYKeyInput(float directionality, boolean isPressed) {
        player.SetVelY(isPressed ? directionality : 0);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.A:
                return ProcessKeyInput(ControlKeys.WEST, -1, true);
            case Input.Keys.D:
                return ProcessKeyInput(ControlKeys.EAST, 1, true);
            case Input.Keys.W:
                return ProcessKeyInput(ControlKeys.NORTH, 1, true);
            case Input.Keys.S:
                return ProcessKeyInput(ControlKeys.SOUTH, -1, true);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode) {
            case Input.Keys.A:
                return ProcessKeyInput(ControlKeys.WEST, 0, false);
            case Input.Keys.D:
                return ProcessKeyInput(ControlKeys.EAST, 0, false);
            case Input.Keys.W:
                return ProcessKeyInput(ControlKeys.NORTH, 0, false);
            case Input.Keys.S:
                return ProcessKeyInput(ControlKeys.SOUTH, 0, false);
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        vector.x = screenX;
        vector.y = screenY;
        vector.set(camera.unproject(vector));

        if (screenX < Gdx.graphics.getWidth() / 3f) {
            joystick.SetPosition(vector.x, vector.y);
            isTouching = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (isTouching) {
            player.SetVelX(0);
            player.SetVelY(0);
        }

        isTouching = false;
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        vector.x = screenX;
        vector.y = screenY;
        vector.set(camera.unproject(vector));

        // Get new vector coordinates for joystick drag.
        vector.set(joystick.SetDragPosition(vector.x, vector.y));

        float velX = Math.round(((vector.x - joystick.GetX()) / (2 * scale) - 0.5f) * 10)/10f;
        float velY = Math.round(((vector.y - joystick.GetY()) / (2 * scale) - 0.5f) * 10)/10f;
        player.SetVelX(velX);
        player.SetVelY(velY);

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void Update(float delta) {
        joystick.Update(delta);
        if (!isTouching) {
            joystick.SetPosition(camera.position.x - (viewport.getWorldWidth() / 2f) + 2.5f * scale, camera.position.y - (viewport.getWorldHeight() / 2f) + 2.5f * scale);
        }
    }

    public void Draw(SpriteBatch batch) {
        batch.setColor(1, 1, 1, 0.5f);
        joystick.Draw(batch);
        batch.setColor(1, 1, 1, 1f);
    }

    @Override
    public void dispose() {
        joystick.dispose();
    }
}
