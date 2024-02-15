package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.enums.ButtonType;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.events.parameters.MovementEvent;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;

public class HumanPlayerController extends BoomPlayerController implements MovementEventListener, ButtonPressListener {

    private final int id;

    public HumanPlayerController(int id, BoardState boardState, PlayerConfig config, float scale) {
        super(boardState, new Player(id, config.GetPlayerSpawnPoint(id), config.GetSpritePath(), scale, GlobalConstants.GODMODE));
        this.id = id;
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
