package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.enums.NodeState;
import com.radius.system.screens.game_ui.TimerDisplay;

import java.util.List;

public class TheoreticalSafeSpaceCounter extends TheoreticalSafeSpace {

    private int spaceCount = 0;

    public TheoreticalSafeSpaceCounter(BoardState boardState) {
        super(boardState);
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {

        List<Point> spaces = this.pathFinder.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA, 2);
        int currentSafeSpaceCount = spaces.size();
        //TimerDisplay.LogTimeStamped("Found " + currentSafeSpaceCount + " safe spaces for " + srcPoint);
        boolean hasMoreSpace = currentSafeSpaceCount >= spaceCount;
        spaceCount = hasMoreSpace ? currentSafeSpaceCount : spaceCount;

        return hasMoreSpace ? Success(spaceCount) : Failure();
    }

    public void ResetSpaceCount() {
        spaceCount = 0;
    }

}
