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

import java.util.List;

public class FindSafeSpace extends Solidifier {

    private final Node theoryCrafter;

    private int[][] boardCost;

    private float delta;

    private int depth;

    public FindSafeSpace(int fireThreshold) {
        super(fireThreshold);
        theoryCrafter = new TheoreticalSafeSpaceCounter();
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        super.Evaluate(depth, delta, boardCost);

        srcPoint = (Point) GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            return NodeState.FAILURE;
        }

        this.depth = depth;
        this.delta = delta;
        this.boardCost = boardCost;

        ((TheoreticalSafeSpaceCounter) theoryCrafter).ResetSpaceCount();
        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            //System.out.println("[FindSafeSpace] Failed to select target!");
            return NodeState.FAILURE;
        }

        GetParent(1).SetData(NodeKeys.TARGET_POINT, targetPoint);
        //System.out.println(" = = = final target point: (" + targetPoint.x + ", " + targetPoint.y + ") = = =");
        return NodeState.SUCCESS;
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        boolean acceptedBySuper = super.AcceptPoint(point);
        boolean acceptPoint = false;

        if (acceptedBySuper) {
            ((TheoreticalSafeSpace) theoryCrafter).SetSourcePoint(point);
            NodeState state = theoryCrafter.Evaluate(depth, delta, ModifyBoardCost());
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

        Solidify(modifiedBoardCost);
        return modifiedBoardCost;
    }
}
