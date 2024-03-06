package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.checks.TheoreticalSafeSpace;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombType;
import com.radius.system.enums.Direction;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;
import com.radius.system.board.BoardState;
import com.radius.system.screens.game_ui.TimerDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindBombArea extends Solidifier {

    private final Node theoryCrafter;

    private final BoardState boardState;

    private final Player player;

    private final int fireThreshold;

    private int[][] boardCost;

    private int maxBurnCount = 0, currentBurnCount, range;

    private boolean hasPierceBomb;

    public FindBombArea(int fireThreshold, BoardState boardState, Player player) {
        this.fireThreshold = fireThreshold;
        this.boardState = boardState;
        this.player = player;

        id = "[!] FindBombArea";

        theoryCrafter = new TheoreticalSafeSpace();
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        this.srcPoint = srcPoint;

        this.boardCost = ConditionalSolidifyBoardCopy(boardCost, fireThreshold);
        List<Point> spaces = AStar.FindOpenSpaces(this.boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target!");
            return Failure();
        }

        Node root = GetRoot();
        Point setTargetPoint = (Point) root.GetData(NodeKeys.TARGET_POINT);
        List<Point> path = AStar.ReconstructPath(targetPoint);
        int setPathSize = 0, pathSize = path.size();
        if (setTargetPoint != null) {
            AssessBombArea(setTargetPoint.x, setTargetPoint.y);
            List<Point> setPath = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, setTargetPoint.x, setTargetPoint.y);
            Object setterObject = root.GetData(NodeKeys.TARGET_SETTER);
            String setterId = root.GetData(NodeKeys.TARGET_SETTER).toString();
            if (setPath != null) {
                setPathSize = setPath.size();
                if ((setPathSize - currentBurnCount) <= (pathSize - maxBurnCount) &&  setPathSize < pathSize && displayId.equals(setterId)) {
                    //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target due to closer path!");
                    return Success(setPathSize);
                }
            }

            if (srcPoint.IsEqualPosition(setTargetPoint) && player.GetAvailableBombs() <= 0 && boardCost[srcPoint.x][srcPoint.y] > 1) {
                return Failure();
            }
        }

        //TimerDisplay.LogTimeStamped("[" + displayId + "]  Target point acquired: " + targetPoint + ", burnCount: " + maxBurnCount + ", path size: " + path.size());
        //TimerDisplay.LogTimeStamped("[" + displayId + "]  Data map:\n" + BuildMapString());
        return Success(pathSize, targetPoint);
    }

    @Override
    protected Point SelectTarget(List<Point> spaces) {
        maxBurnCount = 0;
        return super.SelectTarget(spaces);
    }

    @Override
    protected boolean AcceptPoint(Point point) {

        Map<Direction, Integer> rangeMapping = AssessBombArea(point.x, point.y);

        boolean hasMoreBurnCount = currentBurnCount > maxBurnCount && BoardRep.EMPTY.equals(boardState.GetBoardEntry(point.x, point.y));
        //System.out.println(point + " Accepted by super: " + acceptedBySuper + ", has more burn count (" + currentBurnCount + " > " + maxBurnCount + "): " + hasMoreBurnCount);

        boolean acceptPoint = false;

        if (hasMoreBurnCount) {
            NodeState state = theoryCrafter.Evaluate(point, ModifyBoardCost(rangeMapping, point));
            acceptPoint = NodeState.SUCCESS.equals(state);
            if (acceptPoint) {
                maxBurnCount = currentBurnCount;
            }
        }

        return acceptPoint;
    }

    private int[][] ModifyBoardCost(Map<Direction, Integer> rangeMapping, Point point) {
        int width = boardCost.length, height = boardCost[0].length;
        int[][] modifiedBoardCost = new int[width][height];
        for (int i = 0; i < boardCost.length; i++) {
            System.arraycopy(boardCost[i], 0, modifiedBoardCost[i], 0, boardCost[i].length);
        }

        if (BoardRep.BOMB.equals(boardState.GetBoardEntry(srcPoint.x, srcPoint.y))) {
            modifiedBoardCost[srcPoint.x][srcPoint.y] = -1;
        }

        int x = point.x, y = point.y;

        for (int i = 1; i <= range; i++) {

            int rangeNorth = rangeMapping.get(Direction.NORTH);
            if (CheckValue(i, rangeNorth, y, height, true)) {
                SetCost(modifiedBoardCost, x, y + i);
            }

            int rangeSouth = rangeMapping.get(Direction.SOUTH);
            if (CheckValue(i, rangeSouth, y, 0, false)) {
                SetCost(modifiedBoardCost, x, y - i);
                //modifiedBoardCost[x][y - i] = GlobalConstants.WORLD_AREA;
            }

            int rangeWest = rangeMapping.get(Direction.WEST);
            if (CheckValue(i, rangeWest, x, 0, false)) {
                SetCost(modifiedBoardCost, x - i, y);
                //modifiedBoardCost[x - i][y] = GlobalConstants.WORLD_AREA;
            }

            int rangeEast = rangeMapping.get(Direction.EAST);
            if (CheckValue(i, rangeEast, x, width, true)) {
                SetCost(modifiedBoardCost, x + i, y);
                //modifiedBoardCost[x + i][y] = GlobalConstants.WORLD_AREA;
            }

        }

        modifiedBoardCost[x][y] = GlobalConstants.WORLD_AREA;
        return modifiedBoardCost;
    }

    private void SetCost(int[][] boardCost, int x, int y) {
        if (boardCost[x][y] < 0) {
            return;
        }
        boardCost[x][y] = GlobalConstants.WORLD_AREA;
    }

    private boolean CheckValue(int counter, int range, int axis, int limit, boolean positive) {
        boolean goodAxis = positive ? axis + counter < limit : axis - counter >= limit;
        return counter <= range && goodAxis;
    }

    private Map<Direction, Integer> AssessBombArea(int x, int y) {
        range = player.GetFirePower();
        hasPierceBomb = BombType.PIERCE.equals(player.GetBombType());
        currentBurnCount = 0;

        Map<Direction, Integer> rangeMapping = new HashMap<>();
        rangeMapping.put(Direction.NORTH, CheckObstacle(boardState, x, y + 1, Direction.NORTH, 1));
        //System.out.println("Checked north: " + burntArea);
        rangeMapping.put(Direction.SOUTH, CheckObstacle(boardState, x, y - 1, Direction.SOUTH, 1));
        //System.out.println("Checked north + south: " + burntArea);
        rangeMapping.put(Direction.WEST, CheckObstacle(boardState, x - 1, y, Direction.WEST, 1));
        //System.out.println("Checked north + south + west: " + burntArea);
        rangeMapping.put(Direction.EAST, CheckObstacle(boardState, x + 1, y, Direction.EAST,  1));
        //System.out.println("Checked north + south + west + east: " + burntArea);

        return rangeMapping;
    }

    protected int CheckObstacle(BoardState boardState, int x, int y, Direction direction, int counter) {
        if (counter > range) return 1;

        BoardRep rep = boardState.GetBoardEntry(x, y);

        if (rep == null) {
            return 1;
        }

        switch (rep) {
            case PERMANENT_BLOCK:
            case HARD_BLOCK:
                return 2;
            case SOFT_BLOCK:
                currentBurnCount++;
                if (!hasPierceBomb) {
                    return 2;
                }
        }

        counter++;

        switch (direction) {
            case NORTH:
                return 1 + CheckObstacle(boardState, x, y + 1, direction, counter);
            case SOUTH:
                return 1 + CheckObstacle(boardState, x, y - 1, direction, counter);
            case WEST:
                return 1 + CheckObstacle(boardState, x - 1, y, direction, counter);
            case EAST:
                return 1 + CheckObstacle(boardState, x + 1, y, direction, counter);
        }

        return 1;
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
