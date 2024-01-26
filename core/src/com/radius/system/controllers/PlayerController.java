package com.radius.system.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.enums.ControlKeys;
import com.radius.system.objects.players.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor, Drawable, Disposable {

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private Player player;

    private Texture joystickOuter;

    private Texture joystickFill;

    public PlayerController(Player player) {
        this.player = player;
        joystickOuter = new Texture(Gdx.files.internal("img/JoystickOuter.png"));
        joystickFill = new Texture(Gdx.files.internal("img/JoystickFill.png"));

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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(joystickOuter, x, y, width, height);
        batch.draw(joystickFill, x + width / 4, y + height / 4, width / 2, height / 2);
        batch.setColor(1, 1, 1, 1f);
    }

    @Override
    public float getLeftWidth() {
        return 0;
    }

    @Override
    public void setLeftWidth(float leftWidth) {

    }

    @Override
    public float getRightWidth() {
        return 0;
    }

    @Override
    public void setRightWidth(float rightWidth) {

    }

    @Override
    public float getTopHeight() {
        return 0;
    }

    @Override
    public void setTopHeight(float topHeight) {

    }

    @Override
    public float getBottomHeight() {
        return 0;
    }

    @Override
    public void setBottomHeight(float bottomHeight) {

    }

    @Override
    public float getMinWidth() {
        return 0;
    }

    @Override
    public void setMinWidth(float minWidth) {

    }

    @Override
    public float getMinHeight() {
        return 0;
    }

    @Override
    public void setMinHeight(float minHeight) {

    }

    @Override
    public void dispose() {
        joystickOuter.dispose();
        joystickFill.dispose();
    }
}
