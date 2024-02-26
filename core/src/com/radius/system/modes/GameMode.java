package com.radius.system.modes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.configs.FieldConfig;
import com.radius.system.controllers.ArtificialIntelligenceController;
import com.radius.system.controllers.HumanPlayerController;
import com.radius.system.controllers.BoomPlayerController;
import com.radius.system.enums.BoardRep;
import com.radius.system.events.OverTimeListener;
import com.radius.system.events.listeners.EndGameEventListener;
import com.radius.system.events.listeners.WorldSizeChangeListener;
import com.radius.system.events.parameters.EndGameEvent;
import com.radius.system.board.BoardState;
import com.radius.system.objects.blocks.Bonus;
import com.radius.system.objects.blocks.SoftBlock;
import com.radius.system.objects.players.Player;
import com.radius.system.configs.PlayerConfig;

import java.util.ArrayList;
import java.util.List;

public class GameMode implements Disposable, OverTimeListener {

    protected int WORLD_WIDTH = 31;

    protected int WORLD_HEIGHT = 17;

    protected final float WORLD_SCALE = GlobalConstants.WORLD_SCALE;

    protected final List<BoomPlayerController> controllers = new ArrayList<>();

    protected final List<WorldSizeChangeListener> worldSizeChangeListeners = new ArrayList<>();

    protected final List<EndGameEventListener> endGameEventListeners = new ArrayList<>();

    protected final EndGameEvent endGameEvent;

    protected BoardState boardState;

    protected FieldConfig field;

    protected Thread restartThread;

    protected int mainPlayer = -1;

    protected boolean loading = false;

    public GameMode() {
        boardState = new BoardState(WORLD_WIDTH, WORLD_HEIGHT, WORLD_SCALE);
        field = new FieldConfig();
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

        endGameEvent.killCount = new int[configs.size()];
        endGameEvent.deathCount = new int[configs.size()];
        endGameEvent.selfBurn = new int[configs.size()];

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

    public void Restart(float delta, List<PlayerConfig> configs) {
        loading = true;
        restartThread = new Thread(() -> RestartField(delta, configs));
        restartThread.start();
    }

    private void RestartControllers() {
        for (BoomPlayerController controller : controllers) {
            controller.Restart();
            controller.ResetPlayer();
        }
    }

    public void RestartField(float delta, List<PlayerConfig> configs) {

        field.LoadField(boardState, WORLD_SCALE);
        WORLD_WIDTH = field.GetWidth();
        WORLD_HEIGHT = field.GetHeight();
        FireWorldSizeChange(WORLD_WIDTH, WORLD_HEIGHT);
        AddPlayers(configs);
        //boardState.ClearBoard();
        float spacing = 2f; // Allows for leaving spaces when generating hard blocks.

        /*
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
         */

        // Sleep for half a second to let the board complete its stuff.
        Sleep(0.5f);
        RestartControllers();
        endGameEvent.playerName = null;
        endGameEvent.id = -1;
        endGameEvent.crashed = false;

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
            restartThread.join(1000);
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

    public void ResetKDStats() {
        for (BoomPlayerController controller : controllers) {
            controller.ResetKDStats();
        }
    }

    public void Update(float delta) {

        try {
            boardState.Update(delta);
            for (BoomPlayerController controller : controllers) {
                controller.Update(delta);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            FireCrashedEvent();
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
                    endGameEvent.id = player.id;
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

    public void AddWorldSizeChangeListener(WorldSizeChangeListener listener) {
        if (worldSizeChangeListeners.contains(listener)) return;
        worldSizeChangeListeners.add(listener);
    }

    public void AddEndGameEventListener(EndGameEventListener listener) {
        if (endGameEventListeners.contains(listener)) return;
        endGameEventListeners.add(listener);
    }

    protected void FireWorldSizeChange(int width, int height) {
        for (WorldSizeChangeListener listener : worldSizeChangeListeners) {
            listener.OnWorldSizeChange(width, height);
        }
    }

    private void FireCrashedEvent() {
        endGameEvent.crashed = true;
        FireEndGameEvent();
    }

    private void FireEndGameEvent() {
        for (BoomPlayerController controller : controllers) {
            int id = controller.GetPlayer().id;
            endGameEvent.killCount[id] = controller.GetTotalKills();
            endGameEvent.deathCount[id] = controller.GetTotalDeaths();
            endGameEvent.selfBurn[id] = controller.GetTotalSelfBurn();
        }

        for (EndGameEventListener listener : endGameEventListeners) {
            listener.OnEndGameTrigger(endGameEvent);
        }
    }

    @Override
    public void OverTime() {
        int highestHP = Integer.MIN_VALUE;
        for (BoomPlayerController controller : controllers) {
            int hp = controller.GetPlayer().GetRemainingLife();
            if (hp > highestHP) {
                highestHP = hp;
                endGameEvent.id = controller.GetPlayer().id;
                endGameEvent.playerName = controller.GetPlayer().name;
            } else if (hp == highestHP) {
                endGameEvent.id = -1;
                endGameEvent.playerName = null;
            }
        }

        FireEndGameEvent();
    }
}
