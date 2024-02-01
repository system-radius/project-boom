package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.radius.system.board.BoardState;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.MovementEventListener;
import com.radius.system.objects.players.Player;

public class HumanPlayerController extends PlayerController implements MovementEventListener, ButtonEventListener {

    private final int id;

    private ButtonPressTrigger buttonA;

    public HumanPlayerController(int id, BoardState boardState, float scale) {
        super(boardState, new Player(id, 1, 1, scale));
        this.id = id;

        boardState.AddToBoard(player);
        buttonA = new ButtonPressTrigger(id, 0, this);
    }

    public ButtonPressTrigger GetButtonA() {
        return buttonA;
    }

    @Override
    public void OnButtonPress(int id) {
        if (id == 0) {
            player.PlantBomb();
        }
    }

    @Override
    public void OnMove(int id, float x, float y) {
        if (this.id != id) {
            return;
        }

        player.SetVelX(x);
        player.SetVelY(y);

        player.Collide(boardState.GetSurroundingBlocks(player.GetWorldX(), player.GetWorldY()));
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(Batch batch) {

    }
}
