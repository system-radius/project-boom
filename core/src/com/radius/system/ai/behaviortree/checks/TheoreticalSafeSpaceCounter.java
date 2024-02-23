package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;

import java.util.List;

public class TheoreticalSafeSpaceCounter extends TheoreticalSafeSpace {

    private int spaceCount = 0;

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        int currentSafeSpaceCount = spaces.size();
        boolean hasMoreSpace = currentSafeSpaceCount > spaceCount;
        spaceCount = hasMoreSpace ? currentSafeSpaceCount : spaceCount;

        return hasMoreSpace ? Success() : Failure();
    }

    public void ResetSpaceCount() {
        spaceCount = 0;
    }

}
