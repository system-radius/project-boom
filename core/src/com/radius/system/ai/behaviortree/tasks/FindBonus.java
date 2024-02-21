package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.NodeState;
import com.radius.system.states.BoardState;

import java.util.List;

public class FindBonus extends Solidifier {

    private final BoardState boardState;

    public FindBonus(int fireThreshold, BoardState boardState) {
        super(fireThreshold);
        this.boardState = boardState;
        id = "[!] FindBonus";
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        super.Evaluate(depth, delta, boardCost);

        srcPoint = (Point) GetRoot().GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": FAILURE");
            return NodeState.FAILURE;
        }

        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": FAILURE");
            return NodeState.FAILURE;
        }

        //System.out.println("Target point for find bonus: " + targetPoint);
        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + ": SUCCESS");
        return NodeState.SUCCESS;
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        return super.AcceptPoint(point) && BoardRep.BONUS.equals(boardState.GetBoardEntry(point.x, point.y));
    }
}
