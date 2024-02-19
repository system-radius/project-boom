package com.radius.system.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.controllers.HumanPlayerController;
import com.radius.system.enums.ButtonType;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.StatChangeListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;
import com.radius.system.screens.ui.BoomGameStage;
import com.radius.system.screens.ui.GameCamera;
import com.radius.system.states.GameState;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends AbstractScreen implements ButtonPressListener {

    private final float WORLD_WIDTH = GlobalConstants.WORLD_WIDTH;

    private final float WORLD_HEIGHT = GlobalConstants.WORLD_HEIGHT;

    private final float WORLD_SCALE = GlobalConstants.WORLD_SCALE;

    private final float VIEWPORT_WIDTH = 16f;

    private final float VIEWPORT_HEIGHT = 9f;

    //private final float ZOOM = 0.25785f;
    private final float ZOOM = 0.5f;

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private BoomGameStage stage;

    private GameCamera mainCamera;

    private GameState gameState;

    private OrthographicCamera uiCamera;

    private Viewport mainViewport;

    private Viewport uiViewport;

    private BitmapFont font;

    private boolean maxZoomOut = true;

    public GameScreen() {
        font = FontUtils.GetFont((int) WORLD_SCALE / 2, Color.WHITE, 1, Color.BLACK);

        InitializeView();
        InitializeStage();
        InitializeGameState();
        InitializeEvents();

        gameState.RestartField();
    }

    private void InitializeEvents() {
        stage.AddButtonPressListener(this);
        HumanPlayerController controller = gameState.GetMainController();
        if (controller == null) {
            float newZoom = ComputeZoomValue();
            mainCamera.SetZoom(newZoom);
            AdjustZoom(mainViewport, newZoom);
            return;
        }

        stage.AddMovementEventListener(controller);
        stage.AddButtonPressListener(controller);

        Player player = controller.GetPlayer();
        player.AddCoordinateEventListener(mainCamera);
        player.AddStatChangeListeners(stage.GetStatChangeListeners());

        mainCamera.SetWatchId(player.id);
    }

    private void InitializeStage() {
        //stage = new GameStage(0, uiViewport, WORLD_SCALE);;
        stage = new BoomGameStage(uiViewport, WORLD_SCALE);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void InitializeView() {
        mainCamera = new GameCamera(WORLD_WIDTH, WORLD_HEIGHT, WORLD_SCALE);

        uiCamera = new OrthographicCamera();

        float zoom = ZOOM;
        if (maxZoomOut) {
            zoom = ComputeZoomValue();
        }

        float viewportWidth = (WORLD_SCALE * VIEWPORT_WIDTH) / zoom / EFFECTIVE_VIEWPORT_DIVIDER;
        float viewportHeight = (WORLD_SCALE * VIEWPORT_HEIGHT) / zoom / EFFECTIVE_VIEWPORT_DIVIDER;
        mainCamera.SetZoom(zoom);

        mainViewport = new FitViewport(viewportWidth, viewportHeight, mainCamera);
        uiViewport = new ExtendViewport(VIEWPORT_WIDTH * WORLD_SCALE, VIEWPORT_HEIGHT * WORLD_SCALE, uiCamera);

        float scaledWorldWidth = WORLD_WIDTH * WORLD_SCALE, scaledWorldHeight = WORLD_HEIGHT * WORLD_SCALE;
        mainCamera.position.set(scaledWorldWidth / 2, scaledWorldHeight / 2, 0);

        uiCamera.position.x = mainCamera.position.x;
        uiCamera.position.y = mainCamera.position.y;

        uiCamera.update();
    }

    private void AdjustZoom(Viewport viewport, float zoom) {
        viewport.setWorldWidth(WORLD_SCALE * VIEWPORT_WIDTH / zoom / EFFECTIVE_VIEWPORT_DIVIDER);
        viewport.setWorldHeight(WORLD_SCALE * VIEWPORT_HEIGHT / zoom / EFFECTIVE_VIEWPORT_DIVIDER);
    }

    private float ComputeZoomValue() {

        float viewportWidth = 0, viewportHeight = 0, zoom = ZOOM;

        do {
            zoom -= 0.01f;

            viewportWidth = (WORLD_SCALE * VIEWPORT_WIDTH) / zoom / EFFECTIVE_VIEWPORT_DIVIDER;
            viewportHeight = (WORLD_SCALE * VIEWPORT_HEIGHT) / zoom / EFFECTIVE_VIEWPORT_DIVIDER;

        } while (viewportWidth / WORLD_WIDTH < WORLD_SCALE || viewportHeight / WORLD_HEIGHT < WORLD_SCALE);

        return zoom;
    }

    private void InitializeGameState() {

        List<PlayerConfig> configs = new ArrayList<>();
        configs.add(CreatePlayerConfig(true, true));
        configs.add(CreatePlayerConfig(false, false));
        configs.add(CreatePlayerConfig(false, true));
        configs.add(CreatePlayerConfig(false, true));

        gameState = new GameState();
        gameState.AddPlayers(configs);
    }

    private PlayerConfig CreatePlayerConfig(boolean human, boolean randomizeSprite) {
        PlayerConfig config = new PlayerConfig();
        config.isHuman = human;
        if (randomizeSprite) {
            config.RandomizePlayerSprite();
        }

        return config;
    }

    @Override
    public void show() {
        stage.Resize();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        mainViewport.update(width, height);
        mainCamera.update();
        stage.Resize();
    }

    @Override
    public void Update(float delta) {
        if (stage.IsPaused()) {
            return;
        }
        gameState.Update(delta);
        stage.act(delta);
    }

    @Override
    public void Draw(SpriteBatch spriteBatch) {
        DrawObjects(spriteBatch);
        DrawUI(spriteBatch);
        stage.draw();
    }

    private void DrawObjects(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(mainCamera.projection);
        spriteBatch.setTransformMatrix(mainCamera.view);
        mainViewport.apply();
        gameState.Draw(spriteBatch);

        spriteBatch.end();
    }

    private void DrawUI(SpriteBatch spriteBatch) {
        spriteBatch.begin();

        uiViewport.apply();
        spriteBatch.setProjectionMatrix(uiCamera.projection);
        spriteBatch.setTransformMatrix(uiCamera.view);

        float x = (uiCamera.position.x - uiViewport.getWorldWidth() / 2f);
        float y = (uiCamera.position.y - uiViewport.getWorldHeight() / 2f) + WORLD_SCALE;

        //font.draw(spriteBatch, "(" + mainViewport.getWorldWidth() + ", " + mainViewport.getWorldHeight() + ")" , x, y);
        //font.draw(spriteBatch, "(" + uiViewport.getWorldWidth() / 4 + ", " + uiViewport.getWorldHeight() + ")" , x, y + WORLD_SCALE);
        //font.draw(spriteBatch, "(" + mainCamera.position.x + ", " + mainCamera.position.y + ")" , x, y + WORLD_SCALE * 2);

        spriteBatch.end();
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        renderer.setProjectionMatrix(mainCamera.projection);
        renderer.setTransformMatrix(mainCamera.view);
        mainViewport.apply();

        renderer.begin(ShapeRenderer.ShapeType.Line);

        gameState.DrawDebug(renderer);

        renderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        stage.dispose();
        gameState.dispose();
        FontUtils.Dispose();
    }

    @Override
    public void OnButtonPress(ButtonPressEvent event) {
        if (!ButtonType.RESTART.equals(event.buttonType)) {
            return;
        }
        gameState.ActivateGodMode();
        gameState.RestartField();
        stage.Restart();
    }
}
