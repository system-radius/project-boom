package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.checks.TheoreticalSafeSpaceCounter;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;

import java.util.List;

public class BasicFindSafeSpace extends Solidifier {

    protected final Node theoryCrafter;

    protected final int fireThreshold;

    protected int[][] boardCost;

    protected PathFinder pathFinder;

    public BasicFindSafeSpace(int fireThreshold) {
        this.fireThreshold = fireThreshold;
        theoryCrafter = new TheoreticalSafeSpaceCounter();
        id = "[!] BasicFindSafeSpace";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        this.pathFinder = pathFinder;
        this.srcPoint = srcPoint;
        this.boardCost = ConditionalSolidifyBoardCopy(boardCost, fireThreshold);

        ((TheoreticalSafeSpaceCounter) theoryCrafter).ResetSpaceCount();
        List<Point> spaces = pathFinder.FindOpenSpaces(this.boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA, fireThreshold);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target!");
            return Failure();
        }

        List<Point> path = PathFinder.ReconstructPath(targetPoint);
        //TimerDisplay.LogTimeStamped("[" + displayId + "] Target point acquired: " + targetPoint);
        return Success(path.size(), targetPoint);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        boolean acceptedBySuper = super.AcceptPoint(point);
        boolean acceptPoint = false;

        if (acceptedBySuper) {
            NodeState state = theoryCrafter.Evaluate(point, pathFinder, boardCost);
            acceptPoint = NodeState.SUCCESS.equals(state);
        }

        return acceptPoint;
    }
}
