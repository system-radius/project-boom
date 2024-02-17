package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class OnFirePath extends Node {

    private final int threshold;

    public OnFirePath(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        Point srcPoint = (Point) GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            return NodeState.FAILURE;
        }

        if (boardCost[srcPoint.x][srcPoint.y] > threshold) {
            GetRoot().SetData(NodeKeys.ON_FIRE_PATH, true);
            return NodeState.SUCCESS;
        }

        GetRoot().ClearData(NodeKeys.ON_FIRE_PATH);
        return NodeState.FAILURE;
    }
}
