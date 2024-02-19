package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.events.listeners.FirePathListener;
import com.radius.system.events.parameters.FirePathEvent;
import com.radius.system.states.BoardState;
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

    private FirePathEvent firePathEvent;

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
        if (boardCost[x][y] > 1 && !firePathEvent.onFirePath) {
            firePathEvent.onFirePath = true;
            FireOnFirePathEvent();
        } else if (boardCost[x][y] <= 1 && firePathEvent.onFirePath) {
            firePathEvent.onFirePath = false;
            FireOnFirePathEvent();
        }

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
