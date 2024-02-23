package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.BoardState;

import java.util.List;

public class FindBonus extends Solidifier {

    private final BoardState boardState;

    private final int defaultSuccessWeight;

    public FindBonus(int fireThreshold, BoardState boardState) {
        this(-1, fireThreshold, boardState);
    }

    public FindBonus(int defaultSuccessWeight, int fireThreshold, BoardState boardState) {
        super(fireThreshold);
        this.boardState = boardState;
        this.defaultSuccessWeight = defaultSuccessWeight;
        id = "[!] FindBonus";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        super.Evaluate(srcPoint, boardCost);
        this.srcPoint = srcPoint;

        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            //System.out.println("[" + displayId + "] Failed to select target!");
            return Failure();
        }

        List<Point> path = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, targetPoint.x, targetPoint.y);
        if (path == null) {
            return Failure();
        }

        //System.out.println("Target point for find bonus: " + targetPoint);
        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        //System.out.println("[" + displayId + "] Target point acquired: " + targetPoint);
        return Success(defaultSuccessWeight < 0 ? path.size() : defaultSuccessWeight);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        return super.AcceptPoint(point) && BoardRep.BONUS.equals(boardState.GetBoardEntry(point.x, point.y));
    }
}
