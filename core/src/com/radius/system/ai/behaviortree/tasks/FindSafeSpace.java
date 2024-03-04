package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.checks.TheoreticalSafeSpace;
import com.radius.system.ai.behaviortree.checks.TheoreticalSafeSpaceCounter;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;
import com.radius.system.screens.game_ui.TimerDisplay;

import java.util.List;

public class FindSafeSpace extends Solidifier {

    private final Node theoryCrafter;

    private int[][] boardCost;

    public FindSafeSpace(int fireThreshold) {
        super(fireThreshold);
        theoryCrafter = new TheoreticalSafeSpaceCounter();
        id = "[!] FindSafeSpace";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        super.Evaluate(srcPoint, boardCost);
        this.srcPoint = srcPoint;
        this.boardCost = solidifiedBoard == null ? boardCost : solidifiedBoard;

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
            NodeState state = theoryCrafter.Evaluate(point, ModifyBoardCost());
            acceptPoint = NodeState.SUCCESS.equals(state);
        }

        return acceptPoint;
    }

    private int[][] ModifyBoardCost() {
        int width = boardCost.length, height = boardCost[0].length;
        int[][] modifiedBoardCost = new int[width][height];

        for (int i = 0; i < width; i++) {
            System.arraycopy(boardCost[i], 0, modifiedBoardCost[i], 0, height);
        }

        modifiedBoardCost = Solidify(modifiedBoardCost);
        return modifiedBoardCost;
    }
}
