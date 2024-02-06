package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.radius.system.board.BoardState;
import com.radius.system.enums.ButtonType;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.MovementEventListener;
import com.radius.system.objects.players.Player;

public class HumanPlayerController extends PlayerController implements MovementEventListener, ButtonEventListener {

    private final int id;

    private final ButtonPressTrigger buttonA;

    private final ButtonPressTrigger buttonB;

    public HumanPlayerController(int id, BoardState boardState, float scale) {
        super(boardState, new Player(id, 1, 1, scale));
        this.id = id;
        buttonA = new ButtonPressTrigger(id, ButtonType.A, this);
        buttonB = new ButtonPressTrigger(id, ButtonType.B, this);
    }

    public ButtonPressTrigger GetButtonA() {
        return buttonA;
    }

    public ButtonPressTrigger GetButtonB() {
        return buttonB;
    }

    @Override
    public void OnButtonPress(int id) {
        if (id == ButtonType.A.GetID()) {
            PlantBomb();
        } else if (id == ButtonType.B.GetID()) {
            DetonateBomb();
        }
    }

    @Override
    public void OnMove(int id, float velX, float velY) {
        if (this.id != id) {
            return;
        }

        player.MoveAlongX(velX);
        player.MoveAlongY(velY);
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
