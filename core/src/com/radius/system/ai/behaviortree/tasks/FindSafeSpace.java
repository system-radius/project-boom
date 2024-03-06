package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.checks.TheoreticalSafeSpaceCounter;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;

import java.util.List;

public class FindSafeSpace extends Solidifier {

    private final Node theoryCrafter;

    private final int fireThreshold;

    private int[][] boardCost;

    public FindSafeSpace(int fireThreshold) {
        this.fireThreshold = fireThreshold;
        theoryCrafter = new TheoreticalSafeSpaceCounter();
        id = "[!] FindSafeSpace";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        this.srcPoint = srcPoint;
        this.boardCost = ConditionalSolidifyBoardCopy(boardCost, fireThreshold);

        ((TheoreticalSafeSpaceCounter) theoryCrafter).ResetSpaceCount();
        List<Point> spaces = AStar.FindOpenSpaces(this.boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target!");
            return Failure();
        }

        List<Point> path = AStar.ReconstructPath(targetPoint);
        //TimerDisplay.LogTimeStamped("[" + displayId + "] Target point acquired: " + targetPoint);
        return Success(path.size(), targetPoint);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        boolean acceptedBySuper = super.AcceptPoint(point);
        boolean acceptPoint = false;

        if (acceptedBySuper) {
            NodeState state = theoryCrafter.Evaluate(point, boardCost);
            acceptPoint = NodeState.SUCCESS.equals(state);
        }

        return acceptPoint;
    }
}
