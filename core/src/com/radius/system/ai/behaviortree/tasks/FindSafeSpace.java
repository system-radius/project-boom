package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.checks.TheoreticalSafeSpaceCounter;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;
import com.radius.system.screens.game_ui.TimerDisplay;

import java.sql.Time;
import java.util.List;

public class FindSafeSpace extends BasicFindSafeSpace {

    private final Player player;

    private final BoardState boardState;

    public FindSafeSpace(int fireThreshold, BoardState boardState, Player player) {
        super(fireThreshold);
        this.boardState = boardState;
        this.player = player;
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        this.pathFinder = pathFinder;
        this.srcPoint = srcPoint;
        this.boardCost = ConditionalSolidifyBoardCopy(boardCost, fireThreshold);

        ((TheoreticalSafeSpaceCounter) theoryCrafter).ResetSpaceCount();
        List<Point> spaces = pathFinder.FindOpenSpaces(this.boardCost, srcPoint.x, srcPoint.y, GlobalConstants.WORLD_AREA, fireThreshold);
        Point targetPoint = SelectTarget(spaces);

        if (targetPoint == null) {
            // Attempt to find a fall back point for now, selecting the cell with the lowest cost.
            targetPoint = ForceSelectPoint(spaces);
            if (targetPoint == null) {
                //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target!");
                return Failure();
            }
        }

        List<Point> path = PathFinder.ReconstructPath(targetPoint);
        //TimerDisplay.LogTimeStamped("[" + displayId + "] Target point acquired: " + targetPoint);
        return Success(path.size(), targetPoint);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        boolean acceptedByCost = super.AcceptPoint(point);
        boolean acceptPoint = false;

        if (acceptedByCost) {
            int[][] updatedBoardCost = new int[boardCost.length][boardCost[0].length];
            boardState.CompileBoardCost(updatedBoardCost, fireThreshold, -1);
            List<Point> path = pathFinder.FindShortestPath(updatedBoardCost, srcPoint.x, srcPoint.y, point.x, point.y);
            acceptPoint = path != null;
            /*if (path != null) {
                float movementSpeed = player.GetMovementSpeed();
                acceptPoint = true;
                for (Point pathPoint : path) {
                    float remainingTime = RevertCost(boardCost[pathPoint.x][pathPoint.y]);
                    movementSpeed -= remainingTime;
                    if (movementSpeed <= 0) {
                        acceptPoint = false;
                        break;
                    }
                }
            }*/
        }

        return acceptPoint;
    }

    private Point ForceSelectPoint(List<Point> spaces) {
        Point targetPoint = null;

        lowestCost = Integer.MAX_VALUE;
        for (int i = 0; i < spaces.size(); i++) {
            Point space = spaces.get(i);
            if (ForceAcceptPoint(space)) {
                targetPoint = space;
                lowestCost = (int) space.GetCost();
            }
        }

        return targetPoint;
    }

    private boolean ForceAcceptPoint(Point point) {
        return !point.IsEqualPosition(srcPoint) && point.GetCost() < lowestCost;
    }

    /*
    private float RevertCost(int cost) {
        if (cost < 0) {
            return Short.MAX_VALUE;
        }

        if (cost == 0 || cost == 1) {
            return 0;
        }

        float percentRemaining = ((float)cost / GlobalConstants.WORLD_AREA);
        return GlobalConstants.BOMB_WAIT_TIMER * percentRemaining;
    }
     */
}
