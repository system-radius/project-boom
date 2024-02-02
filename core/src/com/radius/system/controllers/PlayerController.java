package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public abstract class PlayerController implements Disposable {

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

    public abstract void DrawDebug(ShapeRenderer renderer);

}
