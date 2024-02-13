package com.radius.system.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.controllers.Joystick;
import com.radius.system.enums.BonusType;
import com.radius.system.enums.ButtonType;
import com.radius.system.enums.ControlKeys;
import com.radius.system.enums.TimeState;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.RestartEventListener;
import com.radius.system.events.TimerEventListener;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.screens.ui.buttons.GameButton;
import com.radius.system.screens.ui.hud.BoomHUD;
import com.radius.system.utils.FontUtils;

import java.util.HashMap;
import java.util.Map;

public class BoomGameStage extends Stage implements ButtonPressListener {

    private final static float BUTTON_POSITION_Y_DIVIDER = 6f;

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private final Vector3 touchVector;

    private boolean isTouching, paused;

    private int joystickPointer = -1;

    private final BoomHUD hud;

    private final GameButton aButton, bButton, pauseButton, playButton, restartButton;

    private final TimerDisplay timer;

    private final Joystick joystick;

    private final float scale, buttonPositionMultiplier = 5f, gameButtonSize, pauseButtonSize;

    private final Texture pauseScreen;

    private final BitmapFont debugFont;

    public BoomGameStage(int id, Viewport viewport, float scale) {
        super(viewport);
        this.scale = scale;
        this.touchVector = new Vector3(0, 0, 0);
        this.debugFont = FontUtils.GetFont((int) scale / 2, Color.WHITE, 1, Color.BLACK);

        this.hud = new BoomHUD(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight() / 9f);
        hud.AddItem(BonusType.BOMB_STOCK);
        hud.AddItem(BonusType.FIRE_POWER);
        hud.AddItem(BonusType.MOVEMENT_SPEED);
        hud.AddItem(BonusType.EMPTY);

        gameButtonSize = 2 * scale;
        pauseButtonSize = 4 * scale;

        joystick = new Joystick(0, 0, scale);
        pauseScreen = GlobalAssets.LoadTexture(GlobalAssets.BACKGROUND_TEXTURE_PATH);

        this.addActor(hud);
        this.addActor(timer = new TimerDisplay(0, 0, scale / 1.5f, scale / 1.5f));
        this.addActor(pauseButton = CreateGameButton(GlobalAssets.BUTTON_PAUSE_TEXTURE_PATH, ButtonType.PAUSE, 0, 0, scale / 1.5f, 1));
        this.addActor(aButton = CreateGameButton(GlobalAssets.BUTTON_B_TEXTURE_PATH, ButtonType.A, 0, viewport.getWorldHeight() / BUTTON_POSITION_Y_DIVIDER, gameButtonSize, 0.5f));
        this.addActor(bButton = CreateGameButton(GlobalAssets.BUTTON_B_TEXTURE_PATH, ButtonType.B, 0, viewport.getWorldHeight() / BUTTON_POSITION_Y_DIVIDER, gameButtonSize, 0.5f));
        this.addActor(playButton = CreateGameButton(GlobalAssets.BUTTON_PLAY_TEXTURE_PATH, ButtonType.PLAY, 0, 0, pauseButtonSize, 1));
        this.addActor(restartButton = CreateGameButton(GlobalAssets.BUTTON_RESTART_TEXTURE_PATH, ButtonType.RESTART, 0, 0, pauseButtonSize, 1));

        SetButtonStates();
        timer.StartTimer(600);
    }

    private GameButton CreateGameButton(String texturePath, ButtonType type, float x, float y, float size, float alpha) {
        TextureRegion texture = new TextureRegion(GlobalAssets.LoadTexture(texturePath));
        GameButton button = new GameButton(texture, type, x, y, size, size, alpha);
        button.AddListener(this);
        return button;
    }

    public void Resize() {

        Viewport viewport = getViewport();
        float width = viewport.getWorldWidth(), height = viewport.getWorldHeight();
        float pauseButtonsOffset = scale;

        hud.setPosition(hud.getX(), viewport.getWorldHeight() - hud.getHeight());
        hud.setWidth(width);

        timer.setPosition(width - timer.getWidth() * 6, height - hud.getHeight() / 2 - timer.getHeight() / 2);

        pauseButton.setPosition(width - scale, height - hud.getHeight() / 2 - pauseButton.getHeight() / 2);
        playButton.setPosition(width / 2 - pauseButtonSize - pauseButtonsOffset, height / 2 - pauseButtonSize / 2);
        restartButton.setPosition(width / 2 + pauseButtonsOffset, height / 2 - pauseButtonSize / 2);

        aButton.setPosition(width - buttonPositionMultiplier / 2 * scale, aButton.getY());
        bButton.setPosition(width - buttonPositionMultiplier * scale, bButton.getY());
        //hud.Resize();

        joystick.SetPosition( 2.5f * scale, 2.5f * scale, true);
    }

    private void SetButtonStates() {
        aButton.setVisible(!paused);
        bButton.setVisible(!paused);
        pauseButton.setVisible(!paused);

        playButton.setVisible(paused);
        restartButton.setVisible(paused);
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
            joystick.SetPosition(2.5f * scale, 2.5f * scale, true);
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
        batch.setProjectionMatrix(getCamera().combined);

        batch.begin();
        batch.setColor(1, 1, 1, 0.5f);
        joystick.Draw(batch);
        debugFont.draw(getBatch(), "(" + touchVector.x + ", " + touchVector.y + ")", 0, scale);
        batch.setColor(1, 1, 1, 1f);
        batch.end();
    }

    private void DrawPausedOverlay() {
        Batch batch = getBatch();
        batch.setProjectionMatrix(getCamera().combined);

        batch.begin();
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(pauseScreen, 0, 0, getViewport().getWorldWidth(), getViewport().getWorldHeight());
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
        touchVector.set(getCamera().unproject(touchVector));

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

        touchVector.x = screenX;
        touchVector.y = screenY;
        touchVector.set(getCamera().unproject(touchVector));

        // Get new vector coordinates for joystick drag.
        touchVector.set(joystick.SetDragPosition(touchVector.x, touchVector.y));

        float modifier = 0.5f;
        float sensitivity = 2f;

        float velX = Math.round(((touchVector.x - joystick.position.x) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * sensitivity)/sensitivity;
        float velY = Math.round(((touchVector.y - joystick.position.y) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * sensitivity)/sensitivity;
        //movementEvent.x = velX;
        //movementEvent.y = velY;
        touchVector.x = velX;
        touchVector.y = velY;

        FireMovementEvent();
        return true;
    }

    @Override
    public void dispose() {
        joystick.dispose();
        //stageRenderer.dispose();
        GlobalAssets.Dispose();
        super.dispose();
    }

    @Override
    public void OnActivate(ButtonPressEvent event) {
        switch (event.buttonType) {
            case A:
                System.out.println("Button A pressed!");
                break;
            case B:
                System.out.println("Button B pressed!");
                break;
            case PAUSE:
                paused = true;
                System.out.println("Pause button pressed!");
                SetButtonStates();
                break;
            case PLAY:
                paused = false;
                System.out.println("Play button pressed!");
                SetButtonStates();
                break;
            case RESTART:
                System.out.println("Restart button pressed!");
                break;
            default:
        }
    }
}
