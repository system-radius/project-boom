package com.radius.system.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.PlayerController;
import com.radius.system.board.BoardState;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;

public class GameScreen extends AbstractScreen {

    private final float WORLD_WIDTH = 31f;

    private final float WORLD_HEIGHT = 31f;

    private final float WORLD_SCALE = 20f;

    private final float ZOOM = 0.6f;

    private final float EFFECTIVE_VIEWPORT_DIVIDER = 2f;

    private final float scaledWorldWidth = WORLD_WIDTH * WORLD_SCALE;

    private final float scaledWorldHeight = WORLD_HEIGHT * WORLD_SCALE;

    private OrthographicCamera camera;

    private Viewport viewport;

    private BoardState boardState;

    private Player player;

    public GameScreen() {
        InitializeField();
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

        player = new Player(1, 1, WORLD_SCALE);
        boardState.AddToBoard(player);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new PlayerController(player));
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void UpdateCamera() {
        float effectiveViewportWidth = camera.viewportWidth / EFFECTIVE_VIEWPORT_DIVIDER;
        float effectiveViewportHeight = camera.viewportHeight / EFFECTIVE_VIEWPORT_DIVIDER;

        camera.position.x = player.GetX() * WORLD_SCALE;
        camera.position.y = player.GetY() * WORLD_SCALE;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth, scaledWorldWidth - effectiveViewportWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight, scaledWorldHeight - effectiveViewportHeight);

        camera.update();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(0, 0, 0);
        UpdateCamera();

        float viewportWidth = (WORLD_SCALE * 16) / ZOOM / EFFECTIVE_VIEWPORT_DIVIDER;
        float viewportHeight = (WORLD_SCALE * 9) / ZOOM / EFFECTIVE_VIEWPORT_DIVIDER;

        viewport = new FitViewport(viewportWidth, viewportHeight, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void Update(float delta) {
        boardState.Update(delta);
        UpdateCamera();
    }

    @Override
    public void Draw(SpriteBatch spriteBatch) {
        spriteBatch.setProjectionMatrix(camera.projection);
        spriteBatch.setTransformMatrix(camera.view);
        spriteBatch.begin();

        boardState.Draw(spriteBatch);

        spriteBatch.end();
    }
}
