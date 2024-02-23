package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.NoExecuteNode;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

import java.util.List;

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

        List<Point> path = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, targetPoint.x, targetPoint.y);
        if (path == null) {
            //System.out.println("[" + displayId + "] Target point no longer reachable!");
            return Failure();
        }

        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": SUCCESS");
        //System.out.println("[" + displayId + "] Already has target: " + targetPoint);
        return Success();
    }

}
