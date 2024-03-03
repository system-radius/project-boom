package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.NoExecuteNode;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;
import com.radius.system.screens.game_ui.TimerDisplay;

import java.util.List;

public class HasTargetPoint extends NoExecuteNode {

    public HasTargetPoint() {
        id = "[?] HasTargetPoint";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        Point targetPoint = (Point) GetRoot().GetData(NodeKeys.TARGET_POINT);
        if (targetPoint == null) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] No target found!");
            return Failure();
        }

        List<Point> path = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, targetPoint.x, targetPoint.y);
        if (path == null) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Target point no longer reachable!");
            return Failure();
        }

        //TimerDisplay.LogTimeStamped("[" + displayId + "] Already has target: " + targetPoint);
        return Success(0);
    }

}
