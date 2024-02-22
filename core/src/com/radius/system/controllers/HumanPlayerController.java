package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.Direction;
import com.radius.system.events.listeners.FirePathListener;
import com.radius.system.events.parameters.FirePathEvent;
import com.radius.system.objects.BoardState;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.events.parameters.MovementEvent;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;

import java.util.ArrayList;
import java.util.List;

public class HumanPlayerController extends BoomPlayerController implements MovementEventListener, ButtonPressListener {

    private final List<FirePathListener> firePathListeners = new ArrayList<>();

    private final int id;

    private final FirePathEvent firePathEvent;

    public HumanPlayerController(int id, BoardState boardState, PlayerConfig config, float scale) {
        super(boardState, new Player(id, config.GetPlayerSpawnPoint(id), config.GetSpritePath(), scale, GlobalConstants.GODMODE));
        this.id = id;
        firePathEvent = new FirePathEvent(id);
    }

    public void AddFirePathEventListener(FirePathListener listener) {
        if (firePathListeners.contains(listener)) return;
        firePathListeners.add(listener);
    }

    public void FireOnFirePathEvent() {
        for (FirePathListener listener : firePathListeners) {
            listener.OnFirePathTrigger(firePathEvent);
        }
    }

    @Override
    public final void OnButtonPress(ButtonPressEvent event) {
        switch (event.buttonType) {
            case A:
                PlantBomb();
                break;
            case B:
                DetonateBomb();
                break;
        }
    }

    @Override
    public void OnMove(MovementEvent event) {
        if (event.playerId >= 0 && event.playerId != id) {
            return;
        }

        player.MoveAlongX(event.x);
        player.MoveAlongY(event.y);
    }

    @Override
    public void Update(float delta) {
        player.Update(delta);
        player.Collide(boardState.GetSurroundingBlocks(player.GetWorldX(), player.GetWorldY()));

        int[][] boardCost = boardState.GetBoardCost();
        int x = player.GetWorldX(), y = player.GetWorldY();
        if (boardCost[x][y] > GlobalConstants.WORLD_AREA / 2) {
            DetectDirectionality();
            FireOnFirePathEvent();
        } else if (boardCost[x][y] <= 1 && firePathEvent.onFirePath) {
            ResetFirePathEvent();
            FireOnFirePathEvent();
        }

    }

    private void DetectDirectionality() {
        int detectionRangeX = 8, detectionRangeY = 4;
        int x = player.GetWorldX(), y = player.GetWorldY();

        int offScreenX = detectionRangeX + AdjustAxialDetectionRange(detectionRangeX, x, (int)GlobalConstants.WORLD_WIDTH);
        int offScreenY = detectionRangeY + AdjustAxialDetectionRange(detectionRangeY, y, (int)GlobalConstants.WORLD_HEIGHT);

        firePathEvent.onFirePath = true;
        firePathEvent.hasNorth = !DetectBombAtCoordinate(x, y, detectionRangeX, 1, Direction.SOUTH) && DetectBombAtCoordinate(x, y + offScreenY, Direction.NORTH);
        firePathEvent.hasSouth = !DetectBombAtCoordinate(x, y, detectionRangeX, 1, Direction.NORTH) && DetectBombAtCoordinate(x, y - offScreenY, Direction.SOUTH);
        firePathEvent.hasWest = !DetectBombAtCoordinate(x, y, detectionRangeX, 1, Direction.EAST) && DetectBombAtCoordinate(x - offScreenX, y, Direction.WEST);
        firePathEvent.hasEast = !DetectBombAtCoordinate(x, y, detectionRangeX, 1, Direction.WEST) && DetectBombAtCoordinate(x + offScreenX, y, Direction.EAST);
    }

    private int AdjustAxialDetectionRange(int detectionRange, int position, int limit) {
        int additionalRange = 0;
        if (position - detectionRange < 0) {
            while (position - detectionRange + additionalRange < 0) {
                additionalRange++;
            }
        } else if (position + detectionRange >= limit) {
            while (position + detectionRange - additionalRange >= limit) {
                additionalRange++;
            }
        }

        return additionalRange;
    }

    private void ResetFirePathEvent() {
        firePathEvent.onFirePath = false;
        firePathEvent.hasNorth = false;
        firePathEvent.hasSouth = false;
        firePathEvent.hasWest = false;
        firePathEvent.hasEast = false;
    }

    private boolean DetectBombAtCoordinate(int x, int y, Direction direction) {
        return DetectBombAtCoordinate(x, y, -1, -1, direction);
    }

    private boolean DetectBombAtCoordinate(int x, int y, int range, int counter, Direction direction) {
        if (x < 0 || y < 0 || x >= GlobalConstants.WORLD_WIDTH || y >= GlobalConstants.WORLD_HEIGHT || (range > 0 && counter > range)) {
            return false;
        }

        if (BoardRep.BOMB.equals(boardState.GetBoardEntry(x, y))) {
            return true;
        }

        switch (direction) {
            case NORTH:
                return DetectBombAtCoordinate(x, y + 1, range, counter + 1, direction);
            case SOUTH:
                return DetectBombAtCoordinate(x, y - 1, range, counter + 1, direction);
            case WEST:
                return DetectBombAtCoordinate(x - 1, y, range, counter + 1, direction);
            case EAST:
                return DetectBombAtCoordinate(x + 1, y, range, counter + 1, direction);
        }

        return false;
    }

    @Override
    public void Draw(Batch batch) {
        player.Draw(batch);
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        player.DrawDebug(renderer);
    }

    @Override
    public void dispose() {
        player.dispose();
    }
}
