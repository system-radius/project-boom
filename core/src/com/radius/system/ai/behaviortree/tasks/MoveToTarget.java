package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;
import com.radius.system.screens.game_ui.TimerDisplay;

import java.util.List;

public class MoveToTarget extends Selector {

    private List<Point> currentPath;

    private Point srcPoint;

    public MoveToTarget(Node... children) {
        super("[!] MoveToTarget", children);
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        this.srcPoint = srcPoint;
        Point dstPoint = (Point) GetData(NodeKeys.TARGET_POINT);
        if (dstPoint == null) {
            //System.out.println(displayId + " Returning failure due to null target point!");
            return Failure();
        }

        currentPath = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, dstPoint.x, dstPoint.y);
        if (currentPath != null) {
            if (currentPath.size() == 1 && currentPath.get(0).IsEqualPosition(srcPoint)) {
                if (children.size() > 0) {
                    super.Evaluate(srcPoint, boardCost);
                }
                ClearData(NodeKeys.TARGET_POINT);
                //TimerDisplay.LogTimeStamped("[" + displayId + "] Cleared target point!");
                Success(currentPath.size());
            }

            //TimerDisplay.LogTimeStamped("[" + displayId + "] Returning running!");
            return Running(Short.MAX_VALUE);
        }

        //TimerDisplay.LogTimeStamped("[" + displayId + "] Returning failure!");
        return Failure();
    }

    @Override
    public void Execute() {

        GetRoot().SetData(NodeKeys.MOVEMENT_PATH, currentPath);

        if (currentPath != null) {
            if (currentPath.size() == 1 && currentPath.get(0).IsEqualPosition(srcPoint)) {
                Success(currentPath.size());
                if (children.size() > 0) {
                    super.Execute();
                }
                //ClearFullData(NodeKeys.TARGET_POINT);
            }
        }
    }
}
