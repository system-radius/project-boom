package com.radius.system.screens.game_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.controllers.Joystick;
import com.radius.system.enums.BonusType;
import com.radius.system.enums.ButtonType;
import com.radius.system.enums.ControlKeys;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.EndGameEventListener;
import com.radius.system.events.listeners.LoadingEventListener;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.listeners.StatChangeListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.events.parameters.EndGameEvent;
import com.radius.system.events.parameters.MovementEvent;
import com.radius.system.screens.game_ui.buttons.GameButton;
import com.radius.system.screens.game_ui.hud.BoomHUD;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoomGameStage extends Stage implements ButtonPressListener, EndGameEventListener, LoadingEventListener {

    private final static float BUTTON_POSITION_Y_DIVIDER = 6f;

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private final List<ButtonPressListener> buttonPressListeners = new ArrayList<>();

    private final List<MovementEventListener> movementEventListeners = new ArrayList<>();

    private final Vector3 touchVector;

    private final BoomHUD hud;

    private final GameButton aButton, bButton, pauseButton, playButton, restartButton, cancelButton;

    private final TimerDisplay timer;

    private final Joystick joystick;

    private final Texture pauseScreen, warningSign;

    private final BitmapFont debugFont, winnerAnnouncement;

    private final MovementEvent movementEvent;

    private final ButtonPressEvent buttonPressEvent;

    private final float scale, buttonPositionMultiplier = 5f, gameButtonSize, pauseButtonSize;

    private float worldWidth, worldHeight;

    private boolean isTouching, paused, gameConcluded, loading;

    private String conclusionMessage;

    private int joystickPointer = -1;

    public BoomGameStage(Viewport viewport, float scale) {
        super(viewport);
        this.scale = scale;
        this.touchVector = new Vector3(0, 0, 0);
        this.debugFont = FontUtils.GetFont((int) scale / 2, Color.WHITE, 1, Color.BLACK);
        this.winnerAnnouncement = FontUtils.GetFont((int) scale, Color.WHITE, 4, Color.BLACK);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        this.hud = new BoomHUD(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight() / 9f);
        hud.AddItem(BonusType.LIFE);
        hud.AddItem(BonusType.BOMB_STOCK);
        hud.AddItem(BonusType.FIRE_POWER);
        hud.AddItem(BonusType.MOVEMENT_SPEED);
        hud.AddItem(BonusType.EMPTY);

        gameButtonSize = 2 * scale;
        pauseButtonSize = 4 * scale;

        joystick = new Joystick(0, 0, scale);
        pauseScreen = GlobalAssets.LoadTexture(GlobalAssets.BACKGROUND_TEXTURE_PATH);
        warningSign = GlobalAssets.LoadTexture(GlobalAssets.WARNING_SIGN_PATH);

        this.addActor(hud);
        this.addActor(timer = new TimerDisplay(0, 0, scale / 1.5f, scale / 1.5f));
        this.addActor(pauseButton = CreateGameButton(GlobalAssets.BUTTON_PAUSE_TEXTURE_PATH, ButtonType.PAUSE, 0, 0, scale / 1.5f, 1));
        this.addActor(aButton = CreateGameButton(GlobalAssets.BUTTON_B_TEXTURE_PATH, ButtonType.A, 0, viewport.getWorldHeight() / BUTTON_POSITION_Y_DIVIDER, gameButtonSize, 0.5f));
        this.addActor(bButton = CreateGameButton(GlobalAssets.BUTTON_B_TEXTURE_PATH, ButtonType.B, 0, viewport.getWorldHeight() / BUTTON_POSITION_Y_DIVIDER, gameButtonSize, 0.5f));
        this.addActor(playButton = CreateGameButton(GlobalAssets.BUTTON_PLAY_TEXTURE_PATH, ButtonType.PLAY, 0, 0, pauseButtonSize, 1));
        this.addActor(restartButton = CreateGameButton(GlobalAssets.BUTTON_RESTART_TEXTURE_PATH, ButtonType.RESTART, 0, 0, pauseButtonSize, 1));
        this.addActor(cancelButton = CreateGameButton(GlobalAssets.BUTTON_CANCEL_TEXTURE_PATH, ButtonType.CANCEL, 0, 0, pauseButtonSize, 1));

        for (ControlKeys key: ControlKeys.values()) {
            pressedKeys.put(key, false);
        }

        SetButtonStates();
        movementEvent = new MovementEvent(-1, 0, 0);
        buttonPressEvent = new ButtonPressEvent();

        this.AddButtonPressListener(hud);

        timer.StartTimer(600);
    }

    private GameButton CreateGameButton(String texturePath, ButtonType type, float x, float y, float size, float alpha) {
        TextureRegion texture = new TextureRegion(GlobalAssets.LoadTexture(texturePath));
        GameButton button = new GameButton(texture, type, x, y, size, size, alpha);
        button.AddListener(this);
        return button;
    }

    public void Restart() {
        timer.StartTimer(300);
        paused = false;
        gameConcluded = false;
        SetButtonStates();
    }

    public void Resize() {

        float pauseButtonsOffset = scale;
        worldWidth = getViewport().getWorldWidth();
        worldHeight = getViewport().getWorldHeight();

        hud.setPosition(hud.getX(), worldHeight - hud.getHeight());
        hud.setWidth(worldWidth);

        timer.setPosition(worldWidth - timer.getWidth() * 6, worldHeight - hud.getHeight() / 2 - timer.getHeight() / 2);

        pauseButton.setPosition(worldWidth - scale, worldHeight - hud.getHeight() / 2 - pauseButton.getHeight() / 2);

        playButton.setPosition(worldWidth / 2 - pauseButtonSize - pauseButtonsOffset, worldHeight / 2 - pauseButtonSize / 2);
        restartButton.setPosition(worldWidth / 2 + pauseButtonsOffset, worldHeight / 2 - pauseButtonSize / 2);
        if (gameConcluded) {
            restartButton.setPosition(playButton.getX(), playButton.getY());
        }
        cancelButton.setPosition(worldWidth / 2 + pauseButtonsOffset, worldHeight / 2 - pauseButtonSize / 2);

        aButton.setPosition(worldWidth - buttonPositionMultiplier / 2 * scale, aButton.getY());
        bButton.setPosition(worldWidth - buttonPositionMultiplier * scale, bButton.getY());

        joystick.SetPosition( 2.5f * scale, 2.5f * scale, true);
    }

    public TimerDisplay GetTimer() {
        return timer;
    }

    private void SetButtonStates() {
        aButton.setVisible(!paused && !gameConcluded);
        bButton.setVisible(!paused && !gameConcluded);
        pauseButton.setVisible(!paused && !gameConcluded);

        playButton.setVisible(paused);
        restartButton.setVisible(paused || gameConcluded);
        cancelButton.setVisible(gameConcluded);
        Resize();
    }

    private boolean ProcessKeyInput(ControlKeys currentKey, float directionality, boolean isPressed) {
        if (currentKey.GetOppositeKey() != null && pressedKeys.get(currentKey.GetOppositeKey())) {
            return false;
        }
        pressedKeys.put(currentKey, isPressed);
        boolean buttonActivate = false;

        switch (currentKey) {
            case NORTH:
            case SOUTH:
                return ProcessYKeyInput(directionality, isPressed);
            case EAST:
            case WEST:
                return ProcessXKeyInput(directionality, isPressed);
            case BOMB:
                buttonPressEvent.buttonType = ButtonType.A;
                buttonActivate = true;
                break;
            case DETONATE:
                buttonPressEvent.buttonType = ButtonType.B;
                buttonActivate = true;
                break;
            default:
                // Do nothing just yet.
        }

        if (isPressed && buttonActivate) {
            FireButtonEvent(buttonPressEvent);
        }

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

    public void AddMovementEventListener(MovementEventListener listener) {
        if (movementEventListeners.contains(listener)) return;
        movementEventListeners.add(listener);
    }

    public void AddButtonPressListener(ButtonPressListener listener) {
        if (buttonPressListeners.contains(listener)) return;
        buttonPressListeners.add(listener);
    }

    public List<StatChangeListener> GetStatChangeListeners() {
        return hud.GetStatChangeListeners();
    }

    public boolean IsPaused() {
        return paused || gameConcluded;
    }

    public boolean IsLoading() {
        return loading;
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
        Batch batch = getBatch();
        batch.setProjectionMatrix(getCamera().combined);
        batch.begin();

        if (paused || gameConcluded) {
            DrawPausedOverlay(batch);
            DrawConclusionMessage(batch);
        }

        batch.end();
        super.draw();

        batch.begin();
        if (!paused && !gameConcluded) {
            DrawJoystick(batch);
        }

        if (loading) {
            DrawLoadingOverlay(batch);
        }

        batch.end();
    }

    private void DrawLoadingOverlay(Batch batch) {
        batch.draw(pauseScreen, 0, 0, worldWidth, worldHeight);
        winnerAnnouncement.draw(batch, "Loading...", worldWidth / 2, worldHeight / 2, scale, Align.center, false);
    }

    private void DrawConclusionMessage(Batch batch) {
        if (gameConcluded) {
            winnerAnnouncement.draw(batch, conclusionMessage, worldWidth / 2, worldHeight - scale * 1.5f, scale, Align.center, false);
        }
    }

    private void DrawJoystick(Batch batch) {

        batch.setColor(1, 1, 1, 0.5f);
        joystick.Draw(batch);
        //debugFont.draw(getBatch(), "(" + touchVector.x + ", " + touchVector.y + ")", 0, scale);
        batch.setColor(1, 1, 1, 1f);
    }

    private void DrawPausedOverlay(Batch batch) {
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(pauseScreen, 0, 0, getViewport().getWorldWidth(), getViewport().getWorldHeight());
        batch.setColor(1, 1, 1, 1f);
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
        touchVector.set(getCamera().unproject(touchVector));

        // Get new vector coordinates for joystick drag.
        touchVector.set(joystick.SetDragPosition(touchVector.x, touchVector.y));

        float modifier = 0.5f;
        float sensitivity = 2f;

        float velX = Math.round(((touchVector.x - joystick.position.x) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * sensitivity)/sensitivity;
        float velY = Math.round(((touchVector.y - joystick.position.y) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * sensitivity)/sensitivity;
        movementEvent.x = velX;
        movementEvent.y = velY;
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
    public void OnButtonPress(ButtonPressEvent event) {
        switch (event.buttonType) {
            case PAUSE:
                paused = true;
                SetButtonStates();
                break;
            case PLAY:
                paused = false;
                SetButtonStates();
                break;
            case RESTART:
            case A:
            case B:
            default:
        }
        FireButtonEvent(event);
    }

    @Override
    public void OnEndGameTrigger(EndGameEvent event) {
        gameConcluded = true;
        if (event.playerName != null) {
            conclusionMessage = event.playerName + " is the last one standing!   ";
        } else {
            conclusionMessage = GlobalConstants.TIED_MESSAGE;
        }
        SetButtonStates();
    }

    private void FireButtonEvent(ButtonPressEvent event) {
        for (ButtonPressListener listener : buttonPressListeners) {
            listener.OnButtonPress(event);
        }
    }

    private void FireMovementEvent() {
        for (MovementEventListener listener : movementEventListeners) {
            listener.OnMove(movementEvent);
        }
    }

    @Override
    public void OnLoadStart() {
        loading = true;
    }

    @Override
    public void OnLoadFinish() {
        loading = false;
    }
}
