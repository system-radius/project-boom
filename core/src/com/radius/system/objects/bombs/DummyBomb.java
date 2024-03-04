package com.radius.system.objects.bombs;

import com.radius.system.enums.BombState;
import com.radius.system.objects.players.Player;

public class DummyBomb extends Bomb {
    public DummyBomb(float x, float y, float width, float height, float scale) {
        super(null, x, y, width, height, scale);
        range = 99;
    }

    public int GetCost() {
        return MAX_COST;
    }

    @Override
    public void Move(float x, float y) {
        // Do nothing, remote mines do not move.
    }

    @Override
    protected void UpdateBreathing(float delta) {
    }
}
