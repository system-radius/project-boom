package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;

import java.util.List;

public class TheoreticalSafeSpace extends FindSpace {

    private int[][] boardCost;

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        if (srcPoint == null) {
            //System.out.println("[TheoryCraftSafeSpace] No source point found!");
            return NodeState.FAILURE;
        }

        this.boardCost = boardCost;
        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        return targetPoint == null ? NodeState.FAILURE : NodeState.SUCCESS;
        //System.out.println("[TheoryCraftSafeSpace]" + srcPoint + " Result: " + result);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        //System.out.println(point + " board cost: " + boardCost[point.x][point.y]);
        return super.AcceptPoint(point) && boardCost[point.x][point.y] == 1;
    }

    public void SetSourcePoint(Point point) {
        this.srcPoint = point;
    }
}
