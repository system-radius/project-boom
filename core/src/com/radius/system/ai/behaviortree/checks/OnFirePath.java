package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class OnFirePath extends Node {

    private final int threshold;

    public OnFirePath(int threshold) {
        this.threshold = threshold;
        id = "[?] OnFirePath";
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        Point srcPoint = (Point) GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": FAILURE");
            return NodeState.FAILURE;
        }

        if (boardCost[srcPoint.x][srcPoint.y] > threshold) {
            GetRoot().SetData(NodeKeys.ON_FIRE_PATH, true);
            GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": SUCCESS");
            return NodeState.SUCCESS;
        }

        GetRoot().ClearData(NodeKeys.ON_FIRE_PATH);
        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": FAILURE");
        return NodeState.FAILURE;
    }
}
