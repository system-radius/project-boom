package com.radius.system.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.controllers.ArtificialIntelligenceController;
import com.radius.system.controllers.BoomPlayerController;
import com.radius.system.controllers.HumanPlayerController;
import com.radius.system.enums.BotLevel;
import com.radius.system.enums.ButtonType;
import com.radius.system.enums.GameState;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.EndGameEventListener;
import com.radius.system.events.listeners.LoadingEventListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.events.parameters.EndGameEvent;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;
import com.radius.system.screens.ui.BoomGameStage;
import com.radius.system.screens.ui.GameCamera;
import com.radius.system.modes.GameMode;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends AbstractScreen implements ButtonPressListener, EndGameEventListener {

    private final float WORLD_WIDTH = GlobalConstants.WORLD_WIDTH;

    private final float WORLD_HEIGHT = GlobalConstants.WORLD_HEIGHT;

    private final float WORLD_SCALE = GlobalConstants.WORLD_SCALE;

    private final float VIEWPORT_WIDTH = 16f;

    private final float VIEWPORT_HEIGHT = 9f;

    //private final float ZOOM = 0.25785f;
    private final float ZOOM = 0.5f, preloadLimit = 1f, gameEndLimit = 3f;

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private final List<LoadingEventListener> loadingEventListeners = new ArrayList<>();

    private GameState gameState;

    private BoomGameStage stage;

    private GameCamera mainCamera;

    private GameMode gameMode;

    private OrthographicCamera uiCamera;

    private Viewport mainViewport;

    private Viewport uiViewport;

    private BitmapFont font;

    private boolean maxZoomOut = true;

    private float preloadBuffer = 0f;

    private int[] wins;

    private int matches;

    public GameScreen() {
        font = FontUtils.GetFont((int) WORLD_SCALE / 4, Color.WHITE, 1, Color.BLACK);

        InitializeView();
        InitializeStage();
        InitializeGameState();
        InitializeEvents();

        gameState = GameState.START;
        matches = 0;
    }

    private void InitializeEvents() {
        stage.AddButtonPressListener(this);
        stage.GetTimer().AddOverTimeListener(gameMode);
        gameMode.AddEndGameEventListener(stage);
        gameMode.AddEndGameEventListener(this);
        this.AddLoadingEventListener(stage);
        HumanPlayerController controller = gameMode.GetMainController();
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
        controller.AddFirePathEventListener(player);

        mainCamera.SetWatchId(player.id);
    }

    private void InitializeStage() {
        //stage = new GameStage(0, uiViewport, WORLD_SCALE);;
        stage = new BoomGameStage(uiViewport, WORLD_SCALE);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        stage.OnLoadStart();
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

        configs.add(CreatePlayerConfig(false, true, BotLevel.S_CLASS));
        configs.add(CreatePlayerConfig(false, false, BotLevel.D_CLASS));
        configs.add(CreatePlayerConfig(false, true, BotLevel.A_CLASS));
        configs.add(CreatePlayerConfig(false, false, BotLevel.B_CLASS));
        /**/

        wins = new int[configs.size()];

        gameMode = new GameMode();
        gameMode.AddPlayers(configs);
    }

    private PlayerConfig CreatePlayerConfig(boolean human, boolean randomizeSprite, BotLevel botLevel) {
        PlayerConfig config = new PlayerConfig();
        config.isHuman = human;
        if (!human) {
            config.botLevel = botLevel;
        }

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

        switch (gameState) {
            case START:
                // Do things that need to be done only once, then set state to restart.
                if (preloadBuffer < preloadLimit) {
                    preloadBuffer += delta;
                    return;
                }
                gameState = GameState.RESTART;
                preloadBuffer = 0;
                break;
            case RESTART:
                gameState = GameState.LOADING;
                FireOnLoadStartEvent();
                gameMode.Restart(delta);
                stage.Restart();
                break;
            case LOADING:
                // Wait for loading to complete.
                if (gameMode.IsDoneLoading()) {
                    gameState = GameState.LOAD_FINISH;
                    FireOnLoadFinishEvent();
                }
                break;
            case LOAD_FINISH:
                // Basically a marker to start playing.
                gameState = GameState.PLAYING;
                break;
            case PLAYING:
                gameMode.Update(delta * 1.5f);
                stage.act(delta * 1.5f);
                break;
            case PAUSED:
            case COMPLETE:
                // Do nothing when paused;
                break;
            case CONCLUDED:
                if (preloadBuffer < gameEndLimit) {
                    preloadBuffer+= delta;
                    return;
                }
                gameState = GameState.RESTART;
                preloadBuffer = 0;
                break;
        }
    }

    @Override
    public void Draw(SpriteBatch spriteBatch) {
        if (GameState.PLAYING.equals(gameState) || GameState.PAUSED.equals(gameState)) {
            DrawObjects(spriteBatch);
        }
        DrawUI(spriteBatch);
        stage.draw();
    }

    private void DrawObjects(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(mainCamera.projection);
        spriteBatch.setTransformMatrix(mainCamera.view);
        mainViewport.apply();
        gameMode.Draw(spriteBatch);

        spriteBatch.end();
    }

    private void DrawUI(SpriteBatch spriteBatch) {
        spriteBatch.begin();

        uiViewport.apply();
        spriteBatch.setProjectionMatrix(uiCamera.projection);
        spriteBatch.setTransformMatrix(uiCamera.view);

        float x = (uiCamera.position.x - uiViewport.getWorldWidth() / 2f);
        float y = (uiCamera.position.y - uiViewport.getWorldHeight() / 2f) + WORLD_SCALE;

        if (GlobalConstants.DEBUG) {
            if (GameState.COMPLETE.equals(gameState)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Match #");
                sb.append(matches);
                sb.append("\tWins: [");
                for (int i = 0; i < wins.length; i++) {
                    sb.append(wins[i]);
                    if (i + 1 < wins.length) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
                font.draw(spriteBatch, sb.toString(), WORLD_SCALE/2f, WORLD_SCALE/2f);
            } else {
                List<BoomPlayerController> controllers = gameMode.GetControllers();
                for (int i = 0; i < controllers.size(); i++) {
                    BoomPlayerController controller = controllers.get(i);
                    if (controller instanceof ArtificialIntelligenceController) {
                        String display = ((ArtificialIntelligenceController) controller).GetActiveNode();
                        font.draw(spriteBatch, i + ": " + display, WORLD_SCALE / 2f, WORLD_SCALE / 2 + WORLD_SCALE * i);
                    }
                }
            }
            //font.draw(spriteBatch, "(" + uiViewport.getWorldWidth() / 4 + ", " + uiViewport.getWorldHeight() + ")" , x, y + WORLD_SCALE);
            //font.draw(spriteBatch, "(" + mainCamera.position.x + ", " + mainCamera.position.y + ")" , x, y + WORLD_SCALE * 2);
        }
        spriteBatch.end();
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        if (!GameState.PLAYING.equals(gameState)) return;
        renderer.setProjectionMatrix(mainCamera.projection);
        renderer.setTransformMatrix(mainCamera.view);
        mainViewport.apply();

        renderer.begin(ShapeRenderer.ShapeType.Line);

        gameMode.DrawDebug(renderer);

        renderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        stage.dispose();
        gameMode.dispose();
        FontUtils.Dispose();
    }

    @Override
    public void OnButtonPress(ButtonPressEvent event) {

        switch(event.buttonType) {
            case RESTART:
                //gameState.ActivateGodMode();
                gameState = GameState.RESTART;
                preloadBuffer = matches = 0;
                break;
            case PAUSE:
                gameState = GameState.PAUSED;
                break;
            case PLAY:
                gameState = GameState.PLAYING;
                break;

        }
    }

    public void AddLoadingEventListener(LoadingEventListener listener) {
        if (loadingEventListeners.contains(listener)) return;
        loadingEventListeners.add(listener);
    }

    private void FireOnLoadStartEvent() {
        for (LoadingEventListener listener : loadingEventListeners) {
            listener.OnLoadStart();
        }
    }

    private void FireOnLoadFinishEvent() {
        for (LoadingEventListener listener : loadingEventListeners) {
            listener.OnLoadFinish();
        }
    }

    @Override
    public void OnEndGameTrigger(EndGameEvent event) {
        gameState = GameState.CONCLUDED;
        if (event.id >= 0) {
            wins[event.id]++;
        }
        matches++;

        if (matches >= 10) {
            gameState = GameState.COMPLETE;
        }
    }
}
