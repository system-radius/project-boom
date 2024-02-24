package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public abstract class BoomPlayerController implements Disposable {

    protected final BoardState boardState;

    protected Player player;

    protected int kills, deaths, selfBurn;

    public BoomPlayerController(BoardState boardState, Player player) {
        this.boardState = boardState;
        this.player = player;

        boardState.AddToBoard(player);
    }

    public void Restart() {
    }

    public void ResetPlayer() {
        player.Reset();
    }

    public void ResetKDStats() {
        kills = deaths = selfBurn = 0;
    }

    public Player GetPlayer() {
        return player;
    }

    public int GetTotalKills() {
        return kills += player.GetKills();
    }

    public int GetTotalDeaths() {
        return deaths += player.GetDeaths();
    }

    public int GetTotalSelfBurn() {
        return selfBurn += player.GetSelfBurn();
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
