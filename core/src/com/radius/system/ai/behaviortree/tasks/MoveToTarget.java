package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

import java.util.List;

public class MoveToTarget extends Selector {

    public MoveToTarget(Node... children) {
        super(children);
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        Point srcPoint = (Point) GetData(NodeKeys.SOURCE_POINT);
        Point dstPoint = (Point) GetData(NodeKeys.TARGET_POINT);
        if (srcPoint == null || dstPoint == null) {
            //System.out.println("[" + depth + ": MoveToTarget] Returning failure due to null target point!");
            return NodeState.FAILURE;
        }

        List<Point> currentPath = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, dstPoint.x, dstPoint.y);
        GetRoot().SetData(NodeKeys.MOVEMENT_PATH, currentPath);

        if (currentPath != null) {
            if (currentPath.size() == 1 && currentPath.get(0).IsEqualPosition(srcPoint)) {
                ClearData(NodeKeys.TARGET_POINT);
                if (children.size() > 0) {
                    return super.Evaluate(depth, delta, boardCost);
                }
            }
        }
        //System.out.println("[" + depth + ": MoveToTarget] Returning running!");
        return NodeState.RUNNING;
    }
}
