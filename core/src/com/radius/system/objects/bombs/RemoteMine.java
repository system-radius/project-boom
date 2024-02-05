package com.radius.system.objects.bombs;

import com.radius.system.enums.BombState;
import com.radius.system.enums.BombType;
import com.radius.system.objects.players.Player;

public class RemoteMine extends Bomb {
    public RemoteMine(Player player, float x, float y, float width, float height, float scale) {
        super(BombType.REMOTE, player, x, y, width, height, scale);
    }

    @Override
    protected void UpdateBreathing(float delta) {
        if (state == BombState.SET_TO_EXPLODE) {
            super.UpdateBreathing(delta);
        }
    }
}
