package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public class EmptyController extends BoomPlayerController {


    public EmptyController() {
        super(null, null);
    }

    @Override
    public int GetRemainingLives() {
        return -1;
    }

    @Override
    public int GetTotalDeaths() {
        return -1;
    }

    @Override
    public int GetTotalKills() {
        return -1;
    }

    @Override
    public int GetTotalSelfBurn() {
        return -1;
    }

    @Override
    public void ResetKDStats() {
    }

    @Override
    public void ResetPlayer() {
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(Batch batch) {

    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {

    }

    @Override
    public void dispose() {

    }
}
