package com.radius.system.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.controllers.Joystick;
import com.radius.system.enums.ControlKeys;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.MovementEventListener;
import com.radius.system.objects.bombs.Bomb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStage extends Stage {

    private final Texture aTexture = new Texture(Gdx.files.internal("img/A.png"));

    private final Texture bTexture = new Texture(Gdx.files.internal("img/B.png"));

    private final Map<ControlKeys, Boolean> pressedKeys = new HashMap<>();

    private final List<MovementEventListener> movementListeners = new ArrayList<>();

    private final List<ButtonEventListener> buttonAListeners = new ArrayList<>();

    private final List<ButtonEventListener> buttonBListeners = new ArrayList<>();

    private final Vector3 vector;

    private final float scale;

    private final Image aButton;

    private final Image bButton;

    private Viewport viewport;

    private Camera camera;

    private Joystick joystick;

    private boolean isTouching;

    private float movementX;

    private float movementY;

    private int id;

    private int joystickPointer = -1;

    public GameStage(int id, Viewport viewport, float scale) {
        super(viewport);
        this.id = id;
        float buttonSize = 2f * scale;
        this.scale = scale;
        this.vector = new Vector3();

        this.viewport = viewport;
        this.camera = viewport.getCamera();

        joystick = new Joystick(camera.position.x - (Gdx.graphics.getWidth() / 2f), camera.position.y - (Gdx.graphics.getHeight() / 2f), scale);

        aButton = CreateButton(aTexture,
                camera.position.x + viewport.getWorldWidth(),
                camera.position.y - viewport.getWorldHeight() / 3.5f,
                buttonSize, buttonSize);

        aButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FireButtonEvent(buttonAListeners);
            }
        });

        bButton = CreateButton(bTexture,
                camera.position.x + viewport.getWorldWidth() - 15.5f * scale,
                camera.position.y - viewport.getWorldHeight() / 3f,
                buttonSize, buttonSize);

        bButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FireButtonEvent(buttonBListeners);
                // Remove this from here.
                joystick.dynamicPosition = !joystick.dynamicPosition;;
            }
        });

        this.addActor(aButton);
        this.addActor(bButton);

        for (ControlKeys key: ControlKeys.values()) {
            pressedKeys.put(key, false);
        }
    }

    private Image CreateButton(Texture texture, float x, float y, float width, float height) {
        Image image = new Image(texture);
        image.setSize(width, height);
        image.setPosition(x, y);
        image.getColor().a = 0.5f;

        return image;
    }

    private void FireButtonEvent(List<ButtonEventListener> listeners) {
        for (ButtonEventListener listener : listeners) {
            listener.OnButtonPress(id);
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
        movementX = isPressed ? directionality : 0;
        FireMovementEvent();
        return true;
    }

    private boolean ProcessYKeyInput(float directionality, boolean isPressed) {
        movementY = isPressed ? directionality : 0;
        FireMovementEvent();
        return true;
    }

    private void FireMovementEvent() {
        for (MovementEventListener listener : movementListeners) {
            listener.OnMove(id, movementX, movementY);
        }
    }

    public void RepositionButtons() {
        aButton.setPosition(getViewport().getWorldWidth() - 2.5f * scale, aButton.getY());
        bButton.setPosition(getViewport().getWorldWidth() - 5f * scale, bButton.getY());
    }

    public void AddMovementEventListener(MovementEventListener listener) {
        movementListeners.add(listener);
    }

    public void AddButtonAListener(ButtonEventListener listener) {
        buttonAListeners.add(listener);
    }

    public void AddButtonBListener(ButtonEventListener listener) {
        buttonBListeners.add(listener);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        joystick.Update(delta);
        if (!isTouching) {
            joystick.SetPosition(camera.position.x - (viewport.getWorldWidth() / 2f) + 3f * scale, camera.position.y - (viewport.getWorldHeight() / 2f) + 3f * scale, true);
        }
    }

    @Override
    public void draw() {
        super.draw();

        Batch batch = getBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.setColor(1, 1, 1, 0.5f);
        joystick.Draw(batch);
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        vector.x = screenX;
        vector.y = screenY;
        vector.set(camera.unproject(vector));

        if (screenX < Gdx.graphics.getWidth() / 3f && joystickPointer < 0) {
            joystick.SetPosition(vector.x, vector.y, false);
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
            movementX = 0;
            movementY = 0;
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

        vector.x = screenX;
        vector.y = screenY;
        vector.set(camera.unproject(vector));

        // Get new vector coordinates for joystick drag.
        vector.set(joystick.SetDragPosition(vector.x, vector.y));

        float modifier = 0.5f;
        float divider = 10f;

        float velX = Math.round(((vector.x - joystick.GetX()) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * divider)/divider;
        float velY = Math.round(((vector.y - joystick.GetY()) / (joystick.GetInnerSizeMultiplier() * scale) - modifier) * divider)/divider;
        movementX = velX;
        movementY = velY;
        FireMovementEvent();

        return true;
    }

    @Override
    public void dispose() {
        aTexture.dispose();
        bTexture.dispose();
        joystick.dispose();
        Bomb.BOMB_TEXTURE.dispose();
        Bomb.FIRE_TEXTURE.dispose();
        super.dispose();
    }

}
