package com.radius.system.objects.bombs;

import com.radius.system.objects.players.Player;

public class TickingDummyBomb extends Bomb {
    public TickingDummyBomb(float x, float y, float width, float height, float scale) {
        super(null, x, y, width, height, scale);
        range = 99;
    }

    @Override
    public void Move(float x, float y) {
        // Do nothing, remote mines do not move.
    }

    @Override
    protected void UpdateBreathing(float delta) {
        if (preExplosionTime == WAIT_TIMER) {
            return;
        }
        preExplosionTime += delta;

        if (preExplosionTime >= WAIT_TIMER) {
            preExplosionTime = WAIT_TIMER;
        }
    }
}
