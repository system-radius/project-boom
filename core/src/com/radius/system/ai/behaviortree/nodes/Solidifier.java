package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.tasks.FindTarget;

public abstract class Solidifier extends FindTarget {

    protected final int[][] ConditionalSolidifyBoardCopy(int[][] boardCost, int fireThreshold) {
        Boolean onFirePath = (Boolean) GetRoot().GetData(NodeKeys.ON_FIRE_PATH);
        int[][] boardCopy = CopyBoardCost(boardCost);
        if (onFirePath == null || !onFirePath) {
            Solidify(boardCopy, fireThreshold);
        }

        return boardCopy;
    }

    protected final int[][] CopyBoardCost(int[][] boardCost) {
        int[][] boardCostCopy = new int[boardCost.length][boardCost[0].length];

        for (int i = 0; i < boardCost.length; i++) {
            System.arraycopy(boardCost[i], 0,  boardCostCopy[i], 0, boardCost[i].length);
        }

        return boardCostCopy;
    }

    private int[][] Simplify(int[][] boardCost, int fireThreshold) {
        for (int i = 0; i < boardCost.length; i++) {
            for (int j = 0; j < boardCost[i].length; j++) {
                int cost = boardCost[i][j];
                boardCost[i][j] = cost > 0 && cost < fireThreshold ? 1 : cost;
            }
        }

        return boardCost;
    }

    protected final int[][] SimplifyBoardCopy(int[][] boardCost, int fireThreshold) {
        return Simplify(CopyBoardCost(boardCost), fireThreshold);
    }

    private int[][] Solidify(int[][] boardCost, int fireThreshold) {
        for (int i = 0; i < boardCost.length; i++) {
            for (int j = 0; j < boardCost[i].length; j++) {
                int cost = boardCost[i][j];
                boardCost[i][j] = cost >= fireThreshold ? -1 : cost;
            }
        }

        return boardCost;
    }

    protected final int[][] SolidifyBoardCopy(int[][] boardCost, int fireThreshold) {
        return Solidify(CopyBoardCost(boardCost), fireThreshold);
    }

}
