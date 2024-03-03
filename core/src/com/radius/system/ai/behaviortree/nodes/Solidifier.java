package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.tasks.FindTarget;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public abstract class Solidifier extends FindTarget {

    protected final int fireThreshold;

    public Solidifier(int fireThreshold) {
        this.fireThreshold = fireThreshold;
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        Boolean onFirePath = (Boolean) GetRoot().GetData(NodeKeys.ON_FIRE_PATH);
        if (onFirePath == null || !onFirePath) {
            Solidify(boardCost);
        }

        return NodeState.SUCCESS;
    }

    protected final int[][] Solidify(int [][] boardCost) {
        return this.Solidify(boardCost, fireThreshold);
    }

    protected final int[][] Solidify(int[][] boardCost, int fireThreshold) {
        for (int i = 0; i < boardCost.length; i++) {
            for (int j = 0; j < boardCost[i].length; j++) {
                boardCost[i][j] = boardCost[i][j] > fireThreshold ? -1 : boardCost[i][j];
            }
        }

        return boardCost;
    }

}
