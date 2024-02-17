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
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        super.Evaluate(depth, delta, boardCost);

        Point srcPoint = (Point) GetRoot().GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            return NodeState.FAILURE;
        }

        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        int lowestCost = Integer.MAX_VALUE;
        Point targetPoint = null;
        for (Point point : spaces) {
            //System.out.println("potential target point: (" + point.x + ", " + point.y + ") ---> cost: " + point.GetCost() + ", tile cost: " + point.selfCost);
            if (!point.IsEqualPosition(srcPoint) &&
                    point.GetCost() < lowestCost &&
                    point.selfCost <= 1 &&
                    BoardRep.BONUS.equals(boardState.GetBoardEntry(point.x, point.y))
            ) {
                lowestCost = (int) point.GetCost();
                targetPoint = point;
            }
        }

        if (targetPoint == null) {
            return NodeState.FAILURE;
        }

        System.out.println("Target point for find bonus: " + targetPoint);
        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        return NodeState.SUCCESS;
    }
}
