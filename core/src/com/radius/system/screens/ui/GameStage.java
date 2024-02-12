package com.radius.system.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.controllers.Joystick;
import com.radius.system.enums.BombType;
import com.radius.system.enums.ControlKeys;
import com.radius.system.enums.TimeState;
import com.radius.system.events.BombTypeChangeListener;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.OverTimeListener;
import com.radius.system.events.RestartEventListener;
import com.radius.system.events.listeners.StatChangeListener;
import com.radius.system.events.TimerEventListener;
import com.radius.system.events.parameters.MovementEvent;
import com.radius.system.events.parameters.StatChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStage extends Stage implements StatChangeListener, BombTypeChangeListener, OverTimeListener {

    private final Image playButton;

    private final Image restartButton;

    private final Texture pauseScreen;

    private final ShapeRenderer stageRenderer = new ShapeRenderer();

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private final List<MovementEventListener> movementListeners = new ArrayList<>();

    private MovementEvent movementEvent;

    private final List<ButtonEventListener> buttonAListeners = new ArrayList<>();

    private final List<ButtonEventListener> buttonBListeners = new ArrayList<>();

    private final List<TimerEventListener> timerEventListeners = new ArrayList<>();

    private final List<RestartEventListener> restartEventListeners = new ArrayList<>();

    private final Vector3 touchVector;

    private final float scale;

    private final Image aButton;

    private final Image bButton;

    private Viewport viewport;

    private Camera camera;

    private Joystick joystick;

    private boolean isTouching;

    private int id;

    private int joystickPointer = -1;

    private HeadsUpDisplay hud;

    private boolean paused;

    public GameStage(int id, Viewport viewport, float scale) {
        super(viewport);
        this.id = id;
        float buttonSize = 2f * scale;
        this.scale = scale;
        this.touchVector = new Vector3();

        this.viewport = viewport;
        this.camera = viewport.getCamera();

        this.hud = new HeadsUpDisplay(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight() / 9f, scale);
        AddPauseButtonEvent(hud.GetPauseButton());
        AddTimerEventListener(hud.GetTimer());
        hud.GetTimer().AddOverTimeListener(this);

        joystick = new Joystick(camera.position.x - (Gdx.graphics.getWidth() / 2f), camera.position.y - (Gdx.graphics.getHeight() / 2f), scale);

        float pauseButtonSize = scale * 4;

        playButton = CreateButton(GlobalAssets.LoadTexture(GlobalAssets.BUTTON_PLAY_TEXTURE_PATH), viewport.getWorldWidth() / 2 - (pauseButtonSize + scale), viewport.getWorldHeight() / 2 - pauseButtonSize / 2, pauseButtonSize, pauseButtonSize, 1);
        AddPauseButtonEvent(playButton);
        playButton.setVisible(false);

        restartButton = CreateButton(GlobalAssets.LoadTexture(GlobalAssets.BUTTON_RESTART_TEXTURE_PATH), viewport.getWorldWidth() / 2 + scale, viewport.getWorldHeight() / 2 - pauseButtonSize / 2, pauseButtonSize, pauseButtonSize, 1);
        restartButton.setVisible(false);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FireRestartEvent();
            }
        });

        aButton = CreateButton(GlobalAssets.LoadTexture(GlobalAssets.BUTTON_A_TEXTURE_PATH), camera.position.x + viewport.getWorldWidth(), camera.position.y - viewport.getWorldHeight() / 3.5f, buttonSize, buttonSize, 0.5f);
        AddGameButtonEvent(aButton, buttonAListeners);

        bButton = CreateButton(GlobalAssets.LoadTexture(GlobalAssets.BUTTON_B_TEXTURE_PATH), camera.position.x + viewport.getWorldWidth() - 15.5f * scale, camera.position.y - viewport.getWorldHeight() / 3f, buttonSize, buttonSize, 0.5f);
        AddGameButtonEvent(bButton, buttonBListeners);

        this.addActor(playButton);
        this.addActor(restartButton);
        this.addActor(hud);
        this.addActor(aButton);
        this.addActor(bButton);

        for (ControlKeys key: ControlKeys.values()) {
            pressedKeys.put(key, false);
        }

        pauseScreen = CreateTexture(viewport.getScreenWidth(), viewport.getScreenHeight());
        InitializeEventParameters(id);
    }

    private Texture CreateTexture(int width, int height) {
        Pixmap map = new Pixmap(width, height, Pixmap.Format.RGB888);
        map.setColor(Color.BLACK);
        map.fillRectangle(0, 0, width, height);

        Texture texture = new Texture(map);
        map.dispose();

        return texture;
    }

    private Image CreateButton(Texture texture, float x, float y, float width, float height, float alpha) {
        Image image = new Image(texture);
        image.setSize(width, height);
        image.setPosition(x, y);
        image.getColor().a = alpha;

        return image;
    }

    private void InitializeEventParameters(int playerId) {
        movementEvent = new MovementEvent(playerId, 0, 0);
    }

    private void AddGameButtonEvent(Image button, List<ButtonEventListener> listeners) {
        button.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               FireButtonEvent(listeners);
           }
        });
    }

    private void AddPauseButtonEvent(Image button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!paused) {
                    FireTimeEvent(TimeState.PAUSE, 0);
                } else {
                    FireTimeEvent(TimeState.RESUME, 0);
                }
                paused = !paused;
                SetButtonState();
            }
        });
    }

    private void SetButtonState() {
        playButton.setVisible(paused);
        restartButton.setVisible(paused);
        aButton.setVisible(!paused);
        bButton.setVisible(!paused);
        hud.GetPauseButton().setVisible(!paused);
    }

    private void FireButtonEvent(List<ButtonEventListener> listeners) {
        for (ButtonEventListener listener : listeners) {
            listener.OnButtonPress(id);
        }
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
            case BOMB:
                return ProcessButtonInput(isPressed, buttonAListeners);
            case DETONATE:
                return ProcessButtonInput(isPressed, buttonBListeners);
            default:
                // Do nothing just yet.
        }

        return true;
    }

    private boolean ProcessButtonInput(boolean isPressed, List<ButtonEventListener> listeners) {
        if (!isPressed) {
            return false;
        }

        FireButtonEvent(listeners);
        return true;
    }

    private boolean ProcessXKeyInput(float directionality, boolean isPressed) {
        movementEvent.x = isPressed ? directionality : 0;
        FireMovementEvent();
        return true;
    }

    private boolean ProcessYKeyInput(float directionality, boolean isPressed) {
        movementEvent.y = isPressed ? directionality : 0;
        FireMovementEvent();
        return true;
    }

    private void FireMovementEvent() {
        for (MovementEventListener listener : movementListeners) {
            listener.OnActivate(movementEvent);
        }
    }

    public void RepositionUI() {

        float width = viewport.getWorldWidth();

        aButton.setPosition(width - 2.5f * scale, aButton.getY());
        bButton.setPosition(width - 5f * scale, bButton.getY());
        hud.setPosition(hud.getX(), viewport.getWorldHeight() - hud.getHeight());
        hud.setWidth(width);
        hud.RepositionUI();
    }

    public void AddMovementEventListener(MovementEventListener listener) {
        if (movementListeners.contains(listener)) return;
        movementListeners.add(listener);
    }

    public void AddButtonAListener(ButtonEventListener listener) {
        if (buttonAListeners.contains(listener)) return;
        buttonAListeners.add(listener);
    }

    public void AddButtonBListener(ButtonEventListener listener) {
        if (buttonBListeners.contains(listener)) return;
        buttonBListeners.add(listener);
    }

    public void AddTimerEventListener(TimerEventListener listener) {
        if (timerEventListeners.contains(listener)) return;
        timerEventListeners.add(listener);
    }

    public void AddRestartEventListener(RestartEventListener listener) {
        if (restartEventListeners.contains(listener)) return;
        restartEventListeners.add(listener);
        FireRestartEvent();
    }

    public void FireTimeEvent(TimeState state, float time) {
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
    }

    public void FireRestartEvent() {
        for (RestartEventListener listener : restartEventListeners) {
            listener.OnRestart();
        }
        FireTimeEvent(TimeState.START, 300);
        paused = false;
        SetButtonState();
    }

    public boolean IsPaused() {
        return paused;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!isTouching) {
            joystick.SetPosition(camera.position.x - (viewport.getWorldWidth() / 2f) + 3f * scale, camera.position.y - (viewport.getWorldHeight() / 2f) + 3f * scale, true);
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
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.setColor(1, 1, 1, 0.5f);
        joystick.Draw(batch);
        batch.setColor(1, 1, 1, 1f);
        batch.end();
    }

    private void DrawPausedOverlay() {
        Batch batch = getBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(pauseScreen, 0, 0);
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
        touchVector.x = screenX;
        touchVector.y = screenY;
        touchVector.set(camera.unproject(touchVector));

        if (screenX < Gdx.graphics.getWidth() / 3f && joystickPointer < 0) {
            joystick.SetPosition(touchVector.x, touchVector.y, false);
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
            movementEvent.x = 0;
            movementEvent.y = 0;
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

        return true;
    }

    @Override
    public void dispose() {
        joystick.dispose();
        stageRenderer.dispose();
        GlobalAssets.Dispose();
        super.dispose();
    }

    @Override
    public void OnBombTypeChange(BombType newBombType) {
        hud.SetBombType(newBombType);
    }

    @Override
    public void OverTime() {
        System.out.println("Game over!");
    }

    @Override
    public void OnActivate(StatChangeEvent event) {
        if (event.playerId != id) {
            return;
        }

        hud.SetValue(event.bonusType, event.value);
    }
}
