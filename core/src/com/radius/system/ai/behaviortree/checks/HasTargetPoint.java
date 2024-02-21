package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class HasTargetPoint extends Node {

    public HasTargetPoint() {
        id = "[?] HasTargetPoint";
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        Point targetPoint = (Point) GetParent(1).GetData(NodeKeys.TARGET_POINT, false);
        if (targetPoint == null) {
            GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": FAILURE");
            //System.out.println("[" + displayId + "] No target point found!");
            return NodeState.FAILURE;
        }
        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": SUCCESS");
        //System.out.println("[" + displayId + "] Already has target: " + targetPoint);
        return NodeState.SUCCESS;
    }
}
