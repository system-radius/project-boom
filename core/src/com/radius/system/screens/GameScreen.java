package com.radius.system.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.controllers.PlayerController;
import com.radius.system.board.BoardState;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;
import com.radius.system.stages.GameStage;

public class GameScreen extends AbstractScreen {

    private final float WORLD_WIDTH = 31f;

    private final float WORLD_HEIGHT = 31f;

    private final float VIEWPORT_WIDTH = 16f;

    private final float VIEWPORT_HEIGHT = 9f;

    private final float WORLD_SCALE = 24f;

    //private final float ZOOM = 0.25785f;
    private final float ZOOM = 0.5f;

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private final float scaledWorldWidth = WORLD_WIDTH * WORLD_SCALE;

    private final float scaledWorldHeight = WORLD_HEIGHT * WORLD_SCALE;

    private final GameStage stage;

    private OrthographicCamera mainCamera;

    private OrthographicCamera uiCamera;

    private Viewport mainViewport;

    private Viewport uiViewport;

    private BoardState boardState;

    private Player player;

    private PlayerController controller;

    private BitmapFont font;

    private boolean initial;

    public GameScreen() {
        font = new BitmapFont();

        initial = true;
        InitializeView();
        InitializeField();

        stage = new GameStage(uiViewport, WORLD_SCALE);
        InitializeStage();
        UpdateCamera();
    }

    private void InitializeStage() {
        player = new Player(1, 1, WORLD_SCALE);
        boardState.AddToBoard(player);

        InputMultiplexer multiplexer = new InputMultiplexer();
        controller = new PlayerController(player, uiViewport, WORLD_SCALE);
        multiplexer.addProcessor(controller);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void InitializeView() {
        mainCamera = new OrthographicCamera();
        mainCamera.position.set(0, 0, 0);

        uiCamera = new OrthographicCamera();

        float viewportWidth = (WORLD_SCALE * VIEWPORT_WIDTH) / ZOOM / EFFECTIVE_VIEWPORT_DIVIDER;
        float viewportHeight = (WORLD_SCALE * VIEWPORT_HEIGHT) / ZOOM / EFFECTIVE_VIEWPORT_DIVIDER;

        mainViewport = new FitViewport(viewportWidth, viewportHeight, mainCamera);
        uiViewport = new ExtendViewport(VIEWPORT_WIDTH * WORLD_SCALE, VIEWPORT_HEIGHT * WORLD_SCALE, uiCamera);
    }

    public void InitializeField() {

        float spacing = 2f; // Allows for leaving spaces when generating hard blocks.
        boardState = new BoardState((int)WORLD_WIDTH, (int)WORLD_HEIGHT);

        for(int x = 0; x < WORLD_WIDTH; x++) {
            for(int y = 0; y < WORLD_HEIGHT; y++) {
                if (x == 0 || y == 0 || x + 1 == WORLD_WIDTH || y + 1 == WORLD_HEIGHT) {
                    // Create permanent blocks.
                    boardState.AddToBoard(new Block(x, y, WORLD_SCALE, WORLD_SCALE));
                } else if (x % spacing == 0 && y % spacing == 0) {
                    // Create hard blocks.
                    boardState.AddToBoard(new Block(x, y, WORLD_SCALE, WORLD_SCALE));
                }
            }
        }
    }

    private void UpdateCamera() {
        float effectiveViewportWidth = mainCamera.viewportWidth / EFFECTIVE_VIEWPORT_DIVIDER;
        float effectiveViewportHeight = mainCamera.viewportHeight / EFFECTIVE_VIEWPORT_DIVIDER;

        mainCamera.position.x = player.GetX() * WORLD_SCALE;
        mainCamera.position.y = player.GetY() * WORLD_SCALE;

        mainCamera.position.x = MathUtils.clamp(mainCamera.position.x, effectiveViewportWidth, scaledWorldWidth - effectiveViewportWidth);
        mainCamera.position.y = MathUtils.clamp(mainCamera.position.y, effectiveViewportHeight, scaledWorldHeight - effectiveViewportHeight);

        if (initial) {
            uiCamera.position.x = mainCamera.position.x;
            uiCamera.position.y = mainCamera.position.y;

            uiCamera.update();
            initial = false;
        }
        mainCamera.update();
    }

    @Override
    public void show() {
        mainCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        mainViewport.update(width, height);
    }

    @Override
    public void Update(float delta) {
        boardState.SetEvents(stage.HasEventA(), stage.HasEventB());
        boardState.Update(delta);

        stage.ResetEventA();
        stage.ResetEventB();

        UpdateCamera();
        controller.Update(delta);
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
        boardState.Draw(spriteBatch);

        spriteBatch.end();
    }

    private void DrawUI(SpriteBatch spriteBatch) {
        spriteBatch.begin();

        uiViewport.apply();
        spriteBatch.setProjectionMatrix(uiCamera.projection);
        spriteBatch.setTransformMatrix(uiCamera.view);
        controller.Draw(spriteBatch);

        float x = (uiCamera.position.x - uiViewport.getWorldWidth() / 2f);
        float y = (uiCamera.position.y - uiViewport.getWorldHeight() / 2f) + 20f;

        font.draw(spriteBatch, "(" + Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight() + ")" , x, y);
        //font.draw(spriteBatch, "(" + resizeWidth + ", " + resizeHeight + ")" , (uiCamera.position.x), (uiCamera.position.y) + 20);

        spriteBatch.end();
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        renderer.setProjectionMatrix(mainCamera.projection);
        renderer.setTransformMatrix(mainCamera.view);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        boardState.DrawDebug(renderer);

        renderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        controller.dispose();
        stage.dispose();
    }
}
