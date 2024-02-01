package com.radius.system.controllers;

import com.radius.system.board.BoardState;
import com.radius.system.events.ButtonEventListener;
import com.radius.system.events.MovementEventListener;
import com.radius.system.objects.players.Player;

public class PlayerController implements MovementEventListener, ButtonEventListener {

    private final BoardState boardState;

    private final Player player;

    private final int id;

    private ButtonPressTrigger buttonA;

    public PlayerController(int id, BoardState boardState, float scale) {
        this.boardState = boardState;
        this.id = id;

        this.player = new Player(id, 1, 1, scale);
        boardState.AddToBoard(player);

        buttonA = new ButtonPressTrigger(id, 0, this);
    }

    public Player GetPlayer() {
        return player;
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
}
