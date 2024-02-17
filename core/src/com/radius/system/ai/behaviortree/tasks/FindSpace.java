package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;

import java.util.List;

public class FindSpace extends Node {
    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        Point srcPoint = (Point) GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            return NodeState.FAILURE;
        }

        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        // Find the lowest space cost.
        int lowestCost = Integer.MAX_VALUE;
        Point targetPoint = null;
        for (Point point : spaces) {
            //System.out.println("potential target point: (" + point.x + ", " + point.y + ") ---> cost: " + point.GetCost() + ", tile cost: " + point.selfCost);
            if (!point.IsEqualPosition(srcPoint) && point.GetCost() < lowestCost && point.selfCost <= 1) {
                lowestCost = (int) point.GetCost();
                targetPoint = point;
            }
        }

        if (targetPoint == null) {
            return NodeState.FAILURE;
        }

        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        //System.out.println("final target point: (" + targetPoint.x + ", " + targetPoint.y + ")");
        return NodeState.SUCCESS;
    }
}
