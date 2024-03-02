package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.NoExecuteNode;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;

import java.util.List;

public class FindTarget extends NoExecuteNode {

    protected Point srcPoint;

    protected int lowestCost;

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        this.srcPoint = srcPoint;
        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            return state = NodeState.FAILURE;
        }

        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        //System.out.println("final target point: (" + targetPoint.x + ", " + targetPoint.y + ")");
        return state = NodeState.SUCCESS;
    }

    protected Point SelectTarget(List<Point> spaces) {

        Point targetPoint = null;

        lowestCost = Integer.MAX_VALUE;
        for (int i = 0; i < spaces.size(); i++) {
            Point space = spaces.get(i);
            if (AcceptPoint(space)) {
                targetPoint = space;
                lowestCost = (int) space.GetCost();
            }
        }

        return targetPoint;
    }

    protected boolean AcceptPoint(Point point) {
        return !point.IsEqualPosition(srcPoint) && point.GetCost() < lowestCost && point.selfCost <= 1;
    }
}
