package com.radius.system.objects.bombs;

import com.radius.system.states.BoardState;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombState;
import com.radius.system.enums.BombType;
import com.radius.system.enums.Direction;
import com.radius.system.objects.AnimatedGameObject;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;

public class GodBomb extends Bomb {
    public GodBomb(Player player, float x, float y, float width, float height, float scale) {
        super(BombType.GODMODE, player, x, y, width, height, scale);
    }

    @Override
    protected int CheckObstacle(BoardState boardState, int x, int y, Direction direction, int counter) {
        if (counter > range) return 1;

        BoardRep rep = boardState.GetBoardEntry(x, y);
        AnimatedGameObject object = boardState.GetBoardObject(x, y);

        if (BoardRep.PERMANENT_BLOCK.equals(rep)) {
            return 2;
        } else if (BoardRep.HARD_BLOCK.equals(rep)) {
            if (((Block) object).GetLife() > 1) {
                return 2;
            }
        }

        counter++;

        switch (direction) {
            case NORTH:
                return 1 + CheckObstacle(boardState, x, y + 1, direction, counter);
            case SOUTH:
                return 1 + CheckObstacle(boardState, x, y - 1, direction, counter);
            case WEST:
                return 1 + CheckObstacle(boardState, x - 1, y, direction, counter);
            case EAST:
                return 1 + CheckObstacle(boardState, x + 1, y, direction, counter);
        }

        return 1;
    }

    @Override
    protected void UpdateBreathing(float delta) {
        if (state == BombState.SET_TO_EXPLODE) {
            super.UpdateBreathing(delta);
        }
    }
}
