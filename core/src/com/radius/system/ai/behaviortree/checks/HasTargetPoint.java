package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.NoExecuteNode;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class HasTargetPoint extends NoExecuteNode {

    public HasTargetPoint() {
        id = "[?] HasTargetPoint";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        Point targetPoint = (Point) GetParent(1).GetData(NodeKeys.TARGET_POINT, false);
        if (targetPoint == null) {
            //System.out.println("[" + displayId + "] No target found!");
            return Failure();
        }
        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": SUCCESS");
        //System.out.println("[" + displayId + "] Already has target: " + targetPoint);
        return Success();
    }

}
