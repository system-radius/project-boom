package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombType;
import com.radius.system.enums.Direction;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;
import com.radius.system.states.BoardState;

import java.util.ArrayList;
import java.util.List;

public class FindBombArea extends Solidifier {

    private final BoardState boardState;

    private final Player player;

    private int maxBurnCount = 0, range;

    private boolean hasPierceBomb;

    public FindBombArea(int fireThreshold, BoardState boardState, Player player) {
        super(fireThreshold);
        this.boardState = boardState;
        this.player = player;
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        super.Evaluate(depth, delta, boardCost);

        srcPoint = (Point) GetRoot().GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null || player.GetAvailableBombs() <= 0) {
            return NodeState.FAILURE;
        }

        List<Point> spaces = AStar.FindOpenSpaces(boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            System.out.println("Failed to select target!");
            return NodeState.FAILURE;
        }

        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        System.out.println(targetPoint + " Selected target point: " + maxBurnCount);
        return NodeState.SUCCESS;
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
        System.out.println(point + " Accepted by super: " + acceptedBySuper + ", has more burn count (" + currentBurnCount + " > " + maxBurnCount + "): " + hasMoreBurnCount);
        if (hasMoreBurnCount) {
            maxBurnCount = currentBurnCount;
        }

        return acceptedBySuper && hasMoreBurnCount;
    }

    private int AssessBombArea(int x, int y) {
        range = player.GetFirePower();
        hasPierceBomb = BombType.PIERCE.equals(player.GetBombType());
        int burntArea = CheckObstacle(boardState, x, y + 1, Direction.NORTH, 1, 0);
        //System.out.println("Checked north: " + burntArea);
        burntArea = CheckObstacle(boardState, x, y - 1, Direction.SOUTH, 1, burntArea);
        //System.out.println("Checked north + south: " + burntArea);
        burntArea = CheckObstacle(boardState, x - 1, y, Direction.WEST, 1, burntArea);
        //System.out.println("Checked north + south + west: " + burntArea);
        burntArea = CheckObstacle(boardState, x + 1, y, Direction.EAST,  1, burntArea);
        //System.out.println("Checked north + south + west + east: " + burntArea);

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
                System.out.println("[" + x + ", " + y + "] Increased burn area: " + burntArea);
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
