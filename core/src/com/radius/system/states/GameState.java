package com.radius.system.states;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.board.BoardState;
import com.radius.system.controllers.HumanPlayerController;
import com.radius.system.controllers.PlayerController;
import com.radius.system.objects.blocks.Block;
import com.radius.system.screens.GameCamera;
import com.radius.system.stages.GameStage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState implements Disposable {

    private final float WORLD_WIDTH;

    private final float WORLD_HEIGHT;

    private final float WORLD_SCALE;

    private BoardState boardState;

    private List<PlayerController> controllers;

    public GameState(float worldWidth, float worldHeight, float scale, GameStage stage, GameCamera camera) {
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        this.WORLD_SCALE = scale;

        InitializeField();
        InitializeControllers(stage, camera);
    }

    private void InitializeField() {

        float spacing = 2f; // Allows for leaving spaces when generating hard blocks.
        boardState = new BoardState((int) WORLD_WIDTH, (int) WORLD_HEIGHT);

        int fieldIndex = new Random(System.currentTimeMillis()).nextInt(7);
        System.out.println(fieldIndex);

        for(int x = 0; x < WORLD_WIDTH; x++) {
            for(int y = 0; y < WORLD_HEIGHT; y++) {
                if (x == 0 || y == 0 || x + 1 == WORLD_WIDTH || y + 1 == WORLD_HEIGHT) {
                    // Create permanent blocks.
                    boardState.AddToBoard(new Block(fieldIndex, x, y, WORLD_SCALE, WORLD_SCALE));
                } else if (x % spacing == 0 && y % spacing == 0) {
                    // Create hard blocks.
                    boardState.AddToBoard(new Block(fieldIndex, x, y, WORLD_SCALE, WORLD_SCALE));
                }
            }
        }
    }

    private void InitializeControllers(GameStage stage, GameCamera camera) {
        controllers = new ArrayList<>();
        controllers.add(CreateHumanPlayerController(0, stage, camera));

    }

    private PlayerController CreateHumanPlayerController(int id, GameStage stage, GameCamera camera) {
        HumanPlayerController controller = new HumanPlayerController(id, boardState, WORLD_SCALE);
        controller.GetPlayer().AddCoordinateEventListener(camera);
        stage.AddMovementEventListener(controller);
        stage.AddButtonAListener(controller.GetButtonA());

        return controller;
    }

    public void Update(float delta) {
        boardState.Update(delta);
        for (PlayerController controller : controllers) {
            controller.Update(delta);
        }
    }

    public void Draw(Batch batch) {
        boardState.Draw(batch);
        for (PlayerController controller : controllers) {
            controller.Draw(batch);
        }
    }

    public void DrawDebug(ShapeRenderer renderer) {
        boardState.DrawDebug(renderer);
        for (PlayerController controller : controllers) {
            controller.DrawDebug(renderer);
        }
    }

    @Override
    public void dispose() {
        for (PlayerController controller : controllers) {
            controller.dispose();
        }
    }
}
