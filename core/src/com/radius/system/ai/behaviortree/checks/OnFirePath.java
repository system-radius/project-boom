package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.NoExecuteNode;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class OnFirePath extends NoExecuteNode {

    private final int threshold;

    public OnFirePath(int threshold) {
        this.threshold = threshold;
        id = "[?] OnFirePath";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        if (boardCost[srcPoint.x][srcPoint.y] > threshold) {
            GetRoot().SetData(NodeKeys.ON_FIRE_PATH, true);
            return Success();
        }

        GetRoot().ClearData(NodeKeys.ON_FIRE_PATH);
        ClearData(NodeKeys.TARGET_POINT);
        return Failure();
    }
}
