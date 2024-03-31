package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombType;
import com.radius.system.enums.Direction;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;
import com.radius.system.board.BoardState;

import java.util.List;

public class BasicFindBombArea extends Solidifier {

    private final BoardState boardState;

    private final Player player;

    private int maxBurnCount = 0, range;

    private boolean hasPierceBomb;

    public BasicFindBombArea(int fireThreshold, BoardState boardState, Player player) {
        this.boardState = boardState;
        this.player = player;
        id = "[!] BasicFindBombArea";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {

        List<Point> spaces = pathFinder.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target!");
            return NodeState.FAILURE;
        }

        //TimerDisplay.LogTimeStamped("[" + displayId + "]  Target point acquired: " + targetPoint + ", burnCount: " + maxBurnCount + ", path size: " + path.size());
        return Success(0, targetPoint);
    }

    @Override
    protected Point SelectTarget(List<Point> spaces) {
        maxBurnCount = 0;
        return super.SelectTarget(spaces);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        int currentBurnCount = AssessBombArea(point.x, point.y);

        boolean hasMoreBurnCount = currentBurnCount > maxBurnCount;
        boolean acceptedBySuper = point.selfCost <= 1;
        if (hasMoreBurnCount) {
            maxBurnCount = currentBurnCount;
        }

        return acceptedBySuper && hasMoreBurnCount;
    }

    private int AssessBombArea(int x, int y) {
        range = player.GetFirePower();
        hasPierceBomb = BombType.PIERCE.equals(player.GetBombType());
        int burntArea = CheckObstacle(boardState, x, y + 1, Direction.NORTH, 1, 0);
        burntArea = CheckObstacle(boardState, x, y - 1, Direction.SOUTH, 1, burntArea);
        burntArea = CheckObstacle(boardState, x - 1, y, Direction.WEST, 1, burntArea);
        burntArea = CheckObstacle(boardState, x + 1, y, Direction.EAST,  1, burntArea);

        return burntArea;
    }

    protected int CheckObstacle(BoardState boardState, int x, int y, Direction direction, int counter, int burntArea) {
        if (counter > range) return burntArea;

        BoardRep rep = boardState.GetBoardEntry(x, y);

        if (rep == null) {
            return burntArea;
        }

        switch (rep) {
            case PERMANENT_BLOCK:
            case HARD_BLOCK:
                return burntArea;
            case SOFT_BLOCK:
                burntArea++;
                //PrintStackTrace("[" + x + ", " + y + "] Increased burn area: " + burntArea);
                //System.out.println("[" + x + ", " + y + "] Increased burn area: " + burntArea);
                if (!hasPierceBomb) {
                    return burntArea;
                }
        }

        counter++;

        switch (direction) {
            case NORTH:
                return CheckObstacle(boardState, x, y + 1, direction, counter, burntArea);
            case SOUTH:
                return CheckObstacle(boardState, x, y - 1, direction, counter, burntArea);
            case WEST:
                return CheckObstacle(boardState, x - 1, y, direction, counter, burntArea);
            case EAST:
                return CheckObstacle(boardState, x + 1, y, direction, counter, burntArea);
        }

        return burntArea;
    }

    /*
    private void PrintStackTrace(String msg) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        System.out.println(msg);
        for (StackTraceElement trace : stackTrace) {
            System.out.println(trace);
        }

        System.out.println("= = = = = = = = = = = = = = = = = = =");
    }
    */
}
