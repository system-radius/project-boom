package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.tasks.FindTarget;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;
import com.radius.system.screens.game_ui.TimerDisplay;

public abstract class Solidifier extends FindTarget {

    protected final int fireThreshold;

    protected int[][] solidifiedBoard;

    public Solidifier(int fireThreshold) {
        this.fireThreshold = fireThreshold;
    }


    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        Boolean onFirePath = (Boolean) GetRoot().GetData(NodeKeys.ON_FIRE_PATH);
        RefreshInternalBoardCost(boardCost);
        if (onFirePath == null || !onFirePath) {
            Solidify(boardCost);
        }

        return NodeState.SUCCESS;
    }

    protected final int[][] Solidify(int [][] boardCost) {
        return this.Solidify(boardCost, fireThreshold, false);
    }

    protected final void RefreshInternalBoardCost(int[][] boardCost) {
        if (solidifiedBoard == null || solidifiedBoard.length != boardCost.length || solidifiedBoard[0].length != boardCost[0].length) {
            solidifiedBoard = new int[boardCost.length][boardCost[0].length];
        }

        for (int i = 0; i < boardCost.length; i++) {
            System.arraycopy(boardCost[i], 0,  solidifiedBoard[i], 0, boardCost[i].length);
        }
    }

    protected final int[][] Solidify(int[][] boardCost, int fireThreshold, boolean simplify) {
        RefreshInternalBoardCost(boardCost);
        for (int i = 0; i < boardCost.length; i++) {
            for (int j = 0; j < boardCost[i].length; j++) {
                int cost = boardCost[i][j];
                solidifiedBoard[i][j] = cost >= fireThreshold ? -1 : cost;
                if (simplify) {
                    solidifiedBoard[i][j] = cost > 0 && cost < fireThreshold ? 1 : boardCost[i][j];
                }
            }
        }

        return solidifiedBoard;
    }

}
