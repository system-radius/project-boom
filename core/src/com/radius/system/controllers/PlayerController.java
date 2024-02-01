package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public abstract class PlayerController {

    protected final BoardState boardState;

    protected final Player player;

    public PlayerController(BoardState boardState, Player player) {
        this.boardState = boardState;
        this.player = player;
    }

    public Player GetPlayer() {
        return player;
    }

    public abstract void Update(float delta);

    public abstract void Draw(Batch batch);

}
