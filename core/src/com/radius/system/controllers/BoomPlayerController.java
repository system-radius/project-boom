package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.objects.BoardState;
import com.radius.system.objects.players.Player;

public abstract class BoomPlayerController implements Disposable {

    protected final BoardState boardState;

    protected Player player;

    public BoomPlayerController(BoardState boardState, Player player) {
        this.boardState = boardState;
        this.player = player;

        boardState.AddToBoard(player);
    }

    public void Restart() {
        player.Reset();
    }

    public Player GetPlayer() {
        return player;
    }

    public final void PlantBomb() {
        boardState.AddBombToBoard(player.PlantBomb(boardState));
    }

    public final void DetonateBomb() {
        player.DetonateBomb();
    }

    public abstract void Update(float delta);

    public abstract void Draw(Batch batch);

    public abstract void DrawDebug(ShapeRenderer renderer);

}
