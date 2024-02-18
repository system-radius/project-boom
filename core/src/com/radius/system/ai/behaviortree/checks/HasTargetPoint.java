package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class HasTargetPoint extends Node {
    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        Point targetPoint = (Point) GetRoot().GetData(NodeKeys.TARGET_POINT);
        if (targetPoint == null) {
            return NodeState.FAILURE;
        }

        return NodeState.SUCCESS;
    }
}
