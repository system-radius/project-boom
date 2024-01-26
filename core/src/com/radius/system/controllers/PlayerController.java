package com.radius.system;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.radius.system.enums.ControlKeys;
import com.radius.system.objects.players.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor {

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private Player player;

    public PlayerController(Player player) {
        this.player = player;

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
}
