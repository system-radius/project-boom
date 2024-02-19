package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;

import java.util.List;

public class TheoreticalSafeSpaceCounter extends TheoreticalSafeSpace {

    private int spaceCount = 0;

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        if (srcPoint == null) {
            //System.out.println("[TheoryCraftSafeSpace] No source point found!");
            return NodeState.FAILURE;
        }

        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        int currentSafeSpaceCount = spaces.size();
        System.out.println(srcPoint + " Spaces found: " + spaces.size());

        boolean hasMoreSpace = currentSafeSpaceCount > spaceCount;
        spaceCount = hasMoreSpace ? currentSafeSpaceCount : spaceCount;

        System.out.println(srcPoint + " result: " + (hasMoreSpace ? NodeState.SUCCESS : NodeState.FAILURE));

        return hasMoreSpace ? NodeState.SUCCESS : NodeState.FAILURE;
        //System.out.println("[TheoryCraftSafeSpace]" + srcPoint + " Result: " + result);
    }

    public void ResetSpaceCount() {
        spaceCount = 0;
    }

}
