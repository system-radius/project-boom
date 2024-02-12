package com.radius.system.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.BonusType;
import com.radius.system.enums.ControlKeys;
import com.radius.system.enums.TimeState;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.RestartEventListener;
import com.radius.system.events.TimerEventListener;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.screens.ui.hud.BoomHUD;

import java.util.HashMap;
import java.util.Map;

public class BoomGameStage extends Stage {

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private boolean isTouching;

    private int id;

    private int joystickPointer = -1;

    private BoomHUD hud;

    private boolean paused = false;

    public BoomGameStage(int id, Viewport viewport, float scale) {
        super(viewport);
        this.id = id;

        this.hud = new BoomHUD(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight() / 9f);
        hud.AddItem(BonusType.BOMB_STOCK);
        hud.AddItem(BonusType.FIRE_POWER);
        hud.AddItem(BonusType.MOVEMENT_SPEED);
        hud.AddItem(BonusType.EMPTY);

        //joystick = new Joystick(camera.position.x - (Gdx.graphics.getWidth() / 2f), camera.position.y - (Gdx.graphics.getHeight() / 2f), scale);
        this.addActor(hud);
    }

    public void Resize() {

        Viewport viewport = getViewport();
        float width = viewport.getWorldWidth();

        hud.setPosition(hud.getX(), viewport.getWorldHeight() - hud.getHeight());
        hud.setWidth(width);
        //hud.Resize();
    }

    private boolean ProcessKeyInput(ControlKeys currentKey, float directionality, boolean isPressed) {
        if (currentKey.GetOppositeKey() != null && pressedKeys.get(currentKey.GetOppositeKey())) {
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
        //movementEvent.x = isPressed ? directionality : 0;
        FireMovementEvent();
        return true;
    }

    private boolean ProcessYKeyInput(float directionality, boolean isPressed) {
        //movementEvent.y = isPressed ? directionality : 0;
        FireMovementEvent();
        return true;
    }

    private void FireMovementEvent() {
        /*
        for (MovementEventListener listener : movementListeners) {
            listener.OnActivate(movementEvent);
        }
         */
    }

    public void AddMovementEventListener(MovementEventListener listener) {
        /*
        if (movementListeners.contains(listener)) return;
        movementListeners.add(listener);
         */
    }

    public void AddButtonAListener(ButtonEventListener listener) {
    }

    public void AddButtonBListener(ButtonEventListener listener) {
    }

    public void AddTimerEventListener(TimerEventListener listener) {
        /*
        if (timerEventListeners.contains(listener)) return;
        timerEventListeners.add(listener);

         */
    }

    public void AddRestartEventListener(RestartEventListener listener) {
    }

    public void FireTimeEvent(TimeState state, float time) {
        /*
        for (TimerEventListener listener : timerEventListeners) {
            switch (state) {
                case START:
                    listener.StartTimer(time);
                    break;
                case PAUSE:
                    listener.PauseTimer();
                    break;
                case RESUME:
                    listener.ResumeTimer();
                    break;
            }
        }

         */
    }

    public boolean IsPaused() {
        return paused;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!isTouching) {
            //joystick.SetPosition(camera.position.x - (viewport.getWorldWidth() / 2f) + 3f * scale, camera.position.y - (viewport.getWorldHeight() / 2f) + 3f * scale, true);
        }
    }

    @Override
    public void draw() {
        if (paused) {
            DrawPausedOverlay();
        }
        super.draw();
        if (!paused) {
            DrawJoystick();
        }
    }

    private void DrawJoystick() {
        Batch batch = getBatch();
        batch.setProjectionMatrix(getViewport().getCamera().combined);

        batch.begin();
        batch.setColor(1, 1, 1, 0.5f);
        //joystick.Draw(batch);
        batch.setColor(1, 1, 1, 1f);
        batch.end();
    }

    private void DrawPausedOverlay() {
        Batch batch = getBatch();
        batch.setProjectionMatrix(getViewport().getCamera().combined);

        batch.begin();
        batch.setColor(1, 1, 1, 0.5f);
        //batch.draw(pauseScreen, 0, 0);
        batch.setColor(1, 1, 1, 1f);
        batch.end();
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
            case Input.Keys.SHIFT_RIGHT:
                return ProcessKeyInput(ControlKeys.BOMB, 0, true);
            case Input.Keys.SLASH:
                return ProcessKeyInput(ControlKeys.DETONATE, 0, true);
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
            case Input.Keys.SHIFT_RIGHT:
                return ProcessKeyInput(ControlKeys.BOMB, 0, false);
            case Input.Keys.SLASH:
                return ProcessKeyInput(ControlKeys.DETONATE, 0, false);
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /*
        touchVector.x = screenX;
        touchVector.y = screenY;
        touchVector.set(camera.unproject(touchVector));

         */

        if (screenX < Gdx.graphics.getWidth() / 3f && joystickPointer < 0) {
            //joystick.SetPosition(touchVector.x, touchVector.y, false);
            isTouching = true;
            joystickPointer = pointer;
            return true;
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isTouching && pointer == joystickPointer) {
            joystickPointer = -1;
            //movementEvent.x = 0;
            //movementEvent.y = 0;
            FireMovementEvent();
            isTouching = false;
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isTouching || joystickPointer != pointer) {
            return super.touchDragged(screenX, screenY, pointer);
        }
        /*

        touchVector.x = screenX;
        touchVector.y = screenY;
        touchVector.set(camera.unproject(touchVector));

        // Get new vector coordinates for joystick drag.
        touchVector.set(joystick.SetDragPosition(touchVector.x, touchVector.y));

        float modifier = 0.5f;
        float sensitivity = 2f;

        float velX = Math.round(((touchVector.x - joystick.position.x) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * sensitivity)/sensitivity;
        float velY = Math.round(((touchVector.y - joystick.position.y) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * sensitivity)/sensitivity;
        movementEvent.x = velX;
        movementEvent.y = velY;
        FireMovementEvent();
         */
        return true;
    }

    @Override
    public void dispose() {
        //joystick.dispose();
        //stageRenderer.dispose();
        GlobalAssets.Dispose();
        super.dispose();
    }
}
