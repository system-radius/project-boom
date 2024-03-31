package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.tasks.FindTarget;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.enums.NodeState;

import java.util.List;

public class TheoreticalSafeSpace extends FindTarget {

    protected final PathFinder pathFinder;

    private int[][] boardCost;

    public TheoreticalSafeSpace(BoardState boardState) {
        pathFinder = new PathFinder(boardState);
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        this.srcPoint = srcPoint;
        this.boardCost = boardCost;
        List<Point> spaces = this.pathFinder.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA, 2);
        Point targetPoint = SelectTarget(spaces);

        return targetPoint == null ? Failure() : Success(spaces.size());
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        //System.out.println(point + " board cost: " + boardCost[point.x][point.y]);
        return super.AcceptPoint(point) && boardCost[point.x][point.y] == 1;
    }
}
