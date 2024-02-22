package com.radius.system.modes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.controllers.ArtificialIntelligenceController;
import com.radius.system.controllers.HumanPlayerController;
import com.radius.system.controllers.BoomPlayerController;
import com.radius.system.enums.BoardRep;
import com.radius.system.events.RestartEventListener;
import com.radius.system.events.listeners.EndGameEventListener;
import com.radius.system.events.listeners.LoadingEventListener;
import com.radius.system.events.parameters.EndGameEvent;
import com.radius.system.objects.BoardState;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.blocks.Bonus;
import com.radius.system.objects.blocks.HardBlock;
import com.radius.system.objects.blocks.SoftBlock;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMode implements Disposable {

    private final float WORLD_WIDTH = GlobalConstants.WORLD_WIDTH;

    private final float WORLD_HEIGHT = GlobalConstants.WORLD_HEIGHT;

    private final float WORLD_SCALE = GlobalConstants.WORLD_SCALE;

    private final BoardState boardState;

    private final List<BoomPlayerController> controllers = new ArrayList<>();

    private final List<EndGameEventListener> endGameEventListeners = new ArrayList<>();

    private final EndGameEvent endGameEvent;

    private Thread restartThread;

    private int mainPlayer = -1;

    private boolean loading = false;

    public GameMode() {
        boardState = new BoardState((int) WORLD_WIDTH, (int) WORLD_HEIGHT, (int) WORLD_SCALE);
        endGameEvent = new EndGameEvent();
    }

    public void AddPlayers(List<PlayerConfig> configs) {

        for (int i = 0; i < configs.size(); i++) {
            PlayerConfig config = configs.get(i);
            if (config.isHuman) {
                controllers.add(new HumanPlayerController(i, boardState, config, WORLD_SCALE));
                mainPlayer = i;
            } else {
                // Supposed to be adding AI controller.
                controllers.add(new ArtificialIntelligenceController(i, boardState, config, WORLD_SCALE));
            }
        }

    }

    public HumanPlayerController GetMainController() {
        if (mainPlayer < 0) {
            return null;
        }

        return (HumanPlayerController) controllers.get(mainPlayer);
    }

    public void ActivateGodMode() {
        if (mainPlayer >= 0) {
            GetMainController().GetPlayer().ActivateGodMode();
        }
    }

    public List<BoomPlayerController> GetControllers() {
        return controllers;
    }

    public void Restart(float delta) {
        loading = true;
        restartThread = new Thread(() -> RestartField(delta));
        restartThread.start();
    }

    private void RestartControllers() {
        for (BoomPlayerController controller : controllers) {
            controller.Restart();
            controller.ResetPlayer();
        }
    }

    public void RestartField(float delta) {
        RestartField(delta, false);
    }

    public void RestartField(float delta, boolean secondTime) {

        boardState.ClearBoard();
        float spacing = 2f; // Allows for leaving spaces when generating hard blocks.
        int fieldIndex = new Random(System.currentTimeMillis()).nextInt(7);

        for(int x = 0; x < WORLD_WIDTH; x++) {
            for(int y = 0; y < WORLD_HEIGHT; y++) {
                if (x == 0 || y == 0 || x + 1 == WORLD_WIDTH || y + 1 == WORLD_HEIGHT) {
                    // Create permanent blocks.
                    boardState.AddToBoard(new Block(fieldIndex, x, y, WORLD_SCALE, WORLD_SCALE));
                } else if (x % spacing == 0 && y % spacing == 0) {
                    // Create hard blocks.
                    boardState.AddToBoard(new HardBlock(fieldIndex, x, y, WORLD_SCALE, WORLD_SCALE));
                }
            }
        }

        //RandomizeBonus();
        RandomizeField(fieldIndex);

        // Sleep for half a second to let the board complete its stuff.
        Sleep(0.5f);
        RestartControllers();

        // Sleep for a second to finish restarting the controllers.
        Sleep(1);
        try {
            Update(delta);
        } catch (Exception e) {
            e.printStackTrace();
            RestartControllers();
        }

        FinishLoading();
    }

    private void Sleep(float sleepRate) {
        try {
            Thread.sleep((long)(sleepRate * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void FinishLoading() {
        loading = false;
        try {
            restartThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void RandomizeBonus() {
        int totalBonuses = 50;
        for (int i = 0; i < totalBonuses; i++) {
            int x = (int) (Math.random() * WORLD_WIDTH);
            int y = (int) (Math.random() * WORLD_HEIGHT);

            if (boardState.GetBoardEntry(x, y) != BoardRep.EMPTY || IsBoardCorner(x, y)) {
                continue;
            }

            boardState.AddToBoard(new Bonus(x, y, WORLD_SCALE, WORLD_SCALE));
        }
    }

    private void RandomizeField(int fieldIndex) {
        int totalArea = GlobalConstants.WORLD_AREA;
        int boundingBlocks = (int) (WORLD_WIDTH * 2) + (int) (WORLD_HEIGHT * 2);
        int placeableBlocks = totalArea - boundingBlocks;

        for (int i = 0; i < placeableBlocks; i++) {
            int x = (int) (Math.random() * WORLD_WIDTH);
            int y = (int) (Math.random() * WORLD_HEIGHT);

            if (boardState.GetBoardEntry(x, y) != BoardRep.EMPTY || IsBoardCorner(x, y)) {
                continue;
            }

            boardState.AddToBoard(new SoftBlock(fieldIndex, x, y, WORLD_SCALE, WORLD_SCALE));
        }
    }

    private boolean IsBoardCorner(int x, int y) {
        return ((x >= WORLD_WIDTH - 3 || x <= 2) && (y >= WORLD_HEIGHT - 3 || y <= 2));
    }

    public boolean IsDoneLoading() {
        return !loading;
    }

    public void Update(float delta) {

        boardState.Update(delta);
        for (BoomPlayerController controller : controllers) {
            controller.Update(delta);
        }

        if (!ContinueGame()) {
            FireEndGameEvent();
        }
    }

    public boolean ContinueGame() {
        boolean hasOneAlive = false;
        for (BoomPlayerController controller : controllers) {
            Player player = controller.GetPlayer();
            if (player.GetRemainingLife() > 0) {
                if (!hasOneAlive) {
                    hasOneAlive = true;
                    endGameEvent.playerName = player.name;
                } else {
                    // Return true as there are still more than one player alive.
                    endGameEvent.playerName = null;
                    return true;
                }
            }
        }

        // The game is to be terminated.
        return false;
    }

    public void Draw(Batch batch) {
        boardState.Draw(batch);
        for (BoomPlayerController controller : controllers) {
            controller.Draw(batch);
        }
    }

    public void DrawDebug(ShapeRenderer renderer) {
        boardState.DrawDebug(renderer);
        for (BoomPlayerController controller : controllers) {
            controller.DrawDebug(renderer);
        }
    }

    @Override
    public void dispose() {
        for (BoomPlayerController controller : controllers) {
            controller.dispose();
        }
    }

    public void AddEndGameEventListener(EndGameEventListener listener) {
        if (endGameEventListeners.contains(listener)) return;
        endGameEventListeners.add(listener);
    }

    private void FireEndGameEvent() {
        for (EndGameEventListener listener : endGameEventListeners) {
            listener.OnEndGameTrigger(endGameEvent);
        }
    }
}
