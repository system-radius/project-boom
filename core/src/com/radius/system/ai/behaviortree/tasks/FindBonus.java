package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.NodeState;
import com.radius.system.board.BoardState;

import java.util.List;

public class FindBonus extends Solidifier {

    private final BoardState boardState;

    private final int defaultSuccessWeight, fireThreshold;

    public FindBonus(int fireThreshold, BoardState boardState) {
        this(-1, fireThreshold, boardState);
    }

    public FindBonus(int defaultSuccessWeight, int fireThreshold, BoardState boardState) {
        this.fireThreshold = fireThreshold;
        this.boardState = boardState;
        this.defaultSuccessWeight = defaultSuccessWeight;
        id = "[!] FindBonus";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        int[][] solidifiedBoard = SolidifyBoardCopy(boardCost, fireThreshold);
        this.srcPoint = srcPoint;

        List<Point> spaces = pathFinder.FindOpenSpaces(solidifiedBoard, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target!");
            return Failure();
        }

        List<Point> path = PathFinder.ReconstructPath(targetPoint);
        //TimerDisplay.LogTimeStamped("[" + displayId + "] Target point acquired: " + targetPoint);
        return Success(defaultSuccessWeight < 0 ? path.size() : defaultSuccessWeight, targetPoint);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        return super.AcceptPoint(point) && BoardRep.BONUS.equals(boardState.GetBoardEntry(point.x, point.y));
    }
}
