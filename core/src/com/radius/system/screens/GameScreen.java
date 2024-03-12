package com.radius.system.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.configs.FieldConfig;
import com.radius.system.configs.GameConfig;
import com.radius.system.controllers.ArtificialIntelligenceController;
import com.radius.system.controllers.BoomPlayerController;
import com.radius.system.controllers.HumanPlayerController;
import com.radius.system.enums.BotLevel;
import com.radius.system.enums.GameState;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.EndGameEventListener;
import com.radius.system.events.listeners.ExitGameListener;
import com.radius.system.events.listeners.LoadingEventListener;
import com.radius.system.events.listeners.StartGameListener;
import com.radius.system.events.listeners.WorldSizeChangeListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.events.parameters.EndGameEvent;
import com.radius.system.modes.TestMode;
import com.radius.system.objects.players.Player;
import com.radius.system.configs.PlayerConfig;
import com.radius.system.screens.config_ui.ConfigStage;
import com.radius.system.screens.game_ui.BoomGameStage;
import com.radius.system.screens.game_ui.GameCamera;
import com.radius.system.modes.GameMode;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameScreen extends AbstractScreen implements StartGameListener, ButtonPressListener, EndGameEventListener, WorldSizeChangeListener {

    private int WORLD_WIDTH = 0;

    private int WORLD_HEIGHT = 0;

    private final float WORLD_SCALE = GlobalConstants.WORLD_SCALE;

    private final float VIEWPORT_WIDTH = 16f;

    private final float VIEWPORT_HEIGHT = 9f;

    //private final float ZOOM = 0.25785f;
    private final float ZOOM = 0.5f, preloadLimit = 1f, gameEndLimit = 3f;

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private final List<LoadingEventListener> loadingEventListeners = new ArrayList<>();

    private final List<ExitGameListener> exitGameListeners = new ArrayList<>();

    private GameConfig gameConfig;

    private GameState gameState;

    private BoomGameStage gameStage;

    private GameCamera mainCamera;

    private GameMode gameMode;

    private OrthographicCamera uiCamera;

    private Viewport mainViewport;

    private Viewport uiViewport;

    private BitmapFont font;

    private Date startDate, endDate;

    private boolean maxZoomOut = true, hasController = false;

    private float preloadBuffer = 0f, speedMultiplier = 1f;

    private int[] wins, kills, deaths, selfBurns;

    private int matches, crashes, watchId = -1, tiedMatches;

    private String matchResults, dateTime;

    public GameScreen() {
        font = FontUtils.GetFont((int) WORLD_SCALE / 4, Color.WHITE, 1, Color.BLACK);

        InitializeView();
        InitializeStage();

        gameState = GameState.COMPLETE;
        matches = 0;
    }

    private void InitializeEvents() {
        gameStage.AddButtonPressListener(this);
        gameStage.GetTimer().ClearOverTimeListeners();
        gameStage.GetTimer().AddOverTimeListener(gameMode);
        gameMode.AddEndGameEventListener(gameStage);
        gameMode.AddEndGameEventListener(this);

        gameMode.AddWorldSizeChangeListener(this);
        this.AddLoadingEventListener(gameStage);
        HumanPlayerController controller = gameMode.GetMainController();
        hasController = controller != null;
        if (!hasController) {
            float newZoom = ComputeZoomValue();
            mainCamera.SetZoom(newZoom);
            speedMultiplier = 2f;
            SetupCameraListener();
            return;
        }

        gameStage.AddMovementEventListener(controller);
        gameStage.AddButtonPressListener(controller);

        Player player = controller.GetPlayer();
        player.AddCoordinateEventListener(mainCamera);
        player.AddStatChangeListeners(gameStage.GetStatChangeListeners());
        controller.AddFirePathEventListener(player);

        mainCamera.SetWatchId(player.id);
    }

    private void SetupCameraListener() {
        for (BoomPlayerController controller : gameMode.GetControllers()) {
            controller.GetPlayer().AddCoordinateEventListener(mainCamera);
        }
    }

    private void InitializeStage() {
        //stage = new GameStage(0, uiViewport, WORLD_SCALE);;
        gameStage = new BoomGameStage(uiViewport, WORLD_SCALE);
        gameStage.OnLoadStart();
    }

    private void InitializeView() {
        if (mainCamera == null) {
            mainCamera = new GameCamera(WORLD_SCALE);
            mainViewport = mainCamera.GetViewport();
        }
        watchId = -1;
        mainCamera.SetWorldSize(WORLD_WIDTH, WORLD_HEIGHT);
        mainCamera.SetWatchId(watchId);
        float zoom = ZOOM;
        if (maxZoomOut) {
            zoom = ComputeZoomValue();
        }
        mainCamera.SetZoom(zoom);

        if (uiViewport == null) {
            uiCamera = new OrthographicCamera();
            uiViewport = new ExtendViewport(VIEWPORT_WIDTH * WORLD_SCALE, VIEWPORT_HEIGHT * WORLD_SCALE, uiCamera);

            float scaledWorldWidth = WORLD_WIDTH * WORLD_SCALE, scaledWorldHeight = WORLD_HEIGHT * WORLD_SCALE;
            mainCamera.position.set(scaledWorldWidth / 2, scaledWorldHeight / 2, 0);


            uiCamera.position.x = mainCamera.position.x;
            uiCamera.position.y = mainCamera.position.y;
            uiCamera.update();
        }

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

        //configs.add(CreatePlayerConfig(false, true, BotLevel.S_CLASS));
        /*
        configs.add(CreatePlayerConfig(false, false, BotLevel.A_CLASS));
        configs.add(CreatePlayerConfig(false, true, BotLevel.B_CLASS));
        configs.add(CreatePlayerConfig(false, false, BotLevel.D_CLASS));
        /**/

        List<PlayerConfig> playerConfigs = gameConfig.GetPlayerConfigs();

        switch (gameConfig.GetGameMode()) {
            case TEST:
                gameMode = new TestMode(gameConfig.GetFieldConfig(), playerConfigs);
                break;
            case CLASSIC:
            default:
                gameMode = new GameMode(gameConfig.GetFieldConfig(), playerConfigs);
        }

        //gameMode.AddPlayers(configs);
    }

    private void InitializeKillDeathStats(int size) {
        wins = new int[size];
        kills = new int[size];
        deaths = new int[size];
        selfBurns = new int[size];
    }

    @Override
    public void show() {
        if (gameStage != null) {
            gameStage.Resize();
        }
    }

    @Override
    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height, true);
        mainViewport.update(width, height);
        mainCamera.update();
        gameStage.Resize();
    }

    @Override
    public void Update(float delta) {

        switch (gameState) {
            case START:
                // Do things that need to be done only once, then set state to restart.
                if (gameConfig != null) {
                    startDate = new Date(System.currentTimeMillis());
                    List<PlayerConfig> playerConfigs = gameConfig.GetPlayerConfigs();
                    InitializeKillDeathStats(playerConfigs.size());
                    gameState = GameState.RESTART;
                }
                break;
            case RESTART:
                InitializeGameState();
                gameState = GameState.LOADING;
                FireOnLoadStartEvent();
                gameMode.Restart(delta);
                gameStage.Restart();
                break;
            case LOADING:
                // Wait for loading to complete.
                if (gameMode.IsDoneLoading()) {
                    gameState = GameState.LOAD_FINISH;
                }
                break;
            case LOAD_FINISH:
                // Basically a marker to start playing.
                InitializeEvents();
                FireOnLoadFinishEvent();
                gameState = GameState.PLAYING;
                break;
            case PLAYING:
                gameMode.Update(delta * speedMultiplier);
                gameStage.act(delta * speedMultiplier);
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
        gameStage.draw();
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
            if (GameState.COMPLETE.equals(gameState) || GameState.CONCLUDED.equals(gameState)) {
                font.draw(spriteBatch, matchResults, WORLD_SCALE/2f, WORLD_SCALE);
                font.draw(spriteBatch, dateTime, WORLD_SCALE/2f, WORLD_SCALE * 2);
            } else if (gameMode != null) {
                List<BoomPlayerController> controllers = gameMode.GetControllers();
                for (int i = 0; i < controllers.size(); i++) {
                    BoomPlayerController controller = controllers.get(i);
                    if (controller instanceof ArtificialIntelligenceController) {
                        String display = ((ArtificialIntelligenceController) controller).GetActiveNode();
                        font.draw(spriteBatch, (i + 1) + ": " + display, WORLD_SCALE / 2f, WORLD_SCALE / 2 + WORLD_SCALE * i);
                    }
                }

                if (matchResults == null) {
                    font.draw(spriteBatch, "Match #" + (matches + 1) + " / Crashes: " + crashes, 0, uiViewport.getWorldHeight() - WORLD_SCALE);
                } else {
                    font.draw(spriteBatch, matchResults, 0, uiViewport.getWorldHeight() - WORLD_SCALE);
                }
            }
            //font.draw(spriteBatch, "(" + uiViewport.getWorldWidth() / 4 + ", " + uiViewport.getWorldHeight() + ")" , x, y + WORLD_SCALE);
            //font.draw(spriteBatch, "(" + mainCamera.position.x + ", " + mainCamera.position.y + ")" , x, y + WORLD_SCALE * 2f);
        }

        if (gameConfig != null && gameMode != null) {
            float targetWidth = uiViewport.getWorldWidth(), xPos = 0, yPos = uiViewport.getWorldHeight() - WORLD_SCALE * 1.5f;
            List<BoomPlayerController> controllers = gameMode.GetControllers();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < controllers.size(); i++) {
                sb.clear();
                BoomPlayerController controller = controllers.get(i);
                Player player = controller.GetPlayer();
                sb.append(player.GetKills());
                sb.append(" / ");
                sb.append(player.GetDeaths());
                sb.append(" / ");
                sb.append(player.GetSelfBurn());
                font.draw(spriteBatch, (i + 1) + ": " + sb, xPos, yPos - ((WORLD_SCALE / 3.5f) * i), targetWidth - WORLD_SCALE / 2, Align.right, false);
            }
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
        gameStage.dispose();
        gameMode.dispose();
        FontUtils.Dispose();
    }

    @Override
    public void OnButtonPress(ButtonPressEvent event) {

        switch(event.buttonType) {
            case RESTART:
                //gameState.ActivateGodMode();
                gameState = GameState.RESTART;
                preloadBuffer = matches = crashes = 0;
                gameMode.ResetKDStats();
                matchResults = "";
                startDate = new Date(System.currentTimeMillis());
                break;
            case PAUSE:
                gameState = GameState.PAUSED;
                break;
            case PLAY:
                gameState = GameState.PLAYING;
                break;
            case CANCEL:
                gameState = GameState.COMPLETE;
                FireExitGameEvent();
                break;
            case A:
                AdjustWatchId(1);
                break;
            case B:
                AdjustWatchId(-1);
                break;
        }
    }

    public void AdjustWatchId(int adjustment) {
        if (hasController) return;

        int controllersAmount = gameMode.GetControllers().size();
        watchId = (watchId + 1 + adjustment) % (controllersAmount + 1);
        watchId = watchId < 0 ? controllersAmount : watchId;
        watchId -= 1;
        mainCamera.SetWatchId(watchId);
    }

    public void AddLoadingEventListener(LoadingEventListener listener) {
        if (loadingEventListeners.contains(listener)) return;
        loadingEventListeners.add(listener);
    }

    public void AddExitGameListener(ExitGameListener listener) {
        if (exitGameListeners.contains(listener)) return;
        exitGameListeners.add(listener);
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

    private void FireExitGameEvent() {
        for (ExitGameListener listener : exitGameListeners) {
            listener.OnExitGame();
        }
    }

    @Override
    public void OnEndGameTrigger(EndGameEvent event) {
        gameState = GameState.CONCLUDED;

        if (!event.crashed) {
            matches++;
            if (event.id >= 0) {
                wins[event.id]++;
            } else {
                tiedMatches++;
            }
            PrintMatchState(event);
        } else {
            crashes++;
            PrintCrashState(event);
        }

        System.out.println("= = = = = = = =");

        if (matches >= 100) {
            gameState = GameState.COMPLETE;
        }
    }

    private void PrintCrashState(EndGameEvent event) {

        System.out.print(" = = = = = = = C R A S H E D = = = = = = = \n");
        PrintMatchState(event);
        System.out.println(event.crashMessage);
    }

    private void PrintMatchState(EndGameEvent event) {
        endDate = new Date(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        sb.append("Match #");
        sb.append(matches);
        sb.append(" (Crashes: ");
        sb.append(crashes);
        sb.append(")\nWins: [");
        for (int i = 0; i < wins.length; i++) {
            sb.append("Player");
            sb.append(i + 1);
            sb.append(": ");
            sb.append(wins[i]);
            sb.append(" -> ");
            kills[i] += event.killCount[i];
            sb.append(kills[i]);
            sb.append("/");
            deaths[i] += event.deathCount[i];
            sb.append(deaths[i]);
            sb.append("/");
            selfBurns[i] += event.selfBurn[i];
            sb.append(selfBurns[i]);
            if (i + 1 < wins.length) {
                sb.append(",    ");
            }
        }
        sb.append("]\t\nTied Matches: ");
        sb.append(tiedMatches);
        matchResults = sb.toString();
        sb.clear();

        System.out.println(matchResults);

        if (startDate != null && endDate != null) {
            sb.append("Start Time: ");
            sb.append(startDate.toString());
            sb.append("\nEnd Time: ");
            sb.append(endDate.toString());
            dateTime = sb.toString();
            System.out.println(dateTime);
        }
    }

    @Override
    public void OnWorldSizeChange(int width, int height) {
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
        InitializeView();
    }

    @Override
    public void OnGameStart(GameConfig gameConfig) {
        FieldConfig fieldConfig = gameConfig.GetFieldConfig();
        this.gameConfig = gameConfig;

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameStage);
        Gdx.input.setInputProcessor(multiplexer);

        WORLD_WIDTH = fieldConfig.GetWidth();
        WORLD_HEIGHT = fieldConfig.GetHeight();
        InitializeView();

        gameState = GameState.START;
    }
}
