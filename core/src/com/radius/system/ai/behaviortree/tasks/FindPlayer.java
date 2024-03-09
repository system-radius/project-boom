package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.PlayerTarget;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.BoardRep;
import com.radius.system.board.BoardState;
import com.radius.system.enums.Direction;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;
import com.radius.system.screens.game_ui.TimerDisplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FindPlayer extends FindBombArea {

    private final BoardState boardState;

    private final int playerId, fireThreshold;

    private Player owner;

    private List<PlayerTarget> playerTargets;

    private int range;

    public FindPlayer(int id, int fireThreshold, BoardState boardState) {
        super(fireThreshold, boardState, boardState.GetPlayers().get(id));
        this.fireThreshold = fireThreshold;
        this.playerId = id;

        this.boardState = boardState;

        this.id = "[!] FindPlayer";
    }

    @Override
    public void Restart() {
        super.Restart();
        playerTargets = null;
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {

        if (playerTargets != null) {
            ComputeDistances();
        } else {
            InitializePlayerTargets(boardState.GetPlayers());
        }

        if (owner.GetAvailableBombs() == 0) {
            return Failure();
        }

        this.boardCost = ConditionalSolidifyBoardCopy(boardCost, fireThreshold);
        int[][] modifiedBoardCost = CreateModifiedBoardCost(boardCost);
        int[][] solidifiedBoard = SolidifyBoardCopy(boardCost, fireThreshold);
        this.srcPoint = srcPoint;
        this.pathFinder = pathFinder;

        range = owner.GetFirePower();
        List<Point> path = null;
        Point targetPoint = srcPoint;
        Collections.sort(playerTargets);
        PlayerTarget currentTarget = null;
        for (PlayerTarget playerTarget : playerTargets) {
            if (!playerTarget.IsTargetAlive()) {
                continue;
            }

            path = FindRangedPath(pathFinder.FindShortestPath(modifiedBoardCost, srcPoint.x, srcPoint.y, playerTarget.GetWorldX(), playerTarget.GetWorldY()));
            if (path != null) {
                //  && !(boardCost[player.GetWorldX()][player.GetWorldY()] > fireThreshold)
                //TimerDisplay.LogTimeStamped("[" + displayId + "] Got ranged path!");
                // Verify that the last point on the path can be reached.
                Point tempTargetPoint = srcPoint;
                if (path.size() > 0) {
                    tempTargetPoint = path.get(path.size() - 1);
                }
                List<Point> internalPath = pathFinder.FindShortestPath(solidifiedBoard, srcPoint.x, srcPoint.y, tempTargetPoint.x, tempTargetPoint.y);
                if (internalPath != null && AcceptPoint(tempTargetPoint)) {
                    targetPoint = tempTargetPoint;
                    currentTarget = playerTarget;
                    //path = internalPath;
                    break;
                }
            }
        }

        if (path == null || currentTarget == null) {
            //GetRoot().ClearData(NodeKeys.MOVEMENT_PATH);
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target due to null path/target!!");
            return Failure();
        }

        if (BoardRep.BOMB.equals(boardState.GetBoardEntry(targetPoint.x, targetPoint.y))) {
            //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target due to bomb placement!");
            return Failure();
        }

        Node root = GetRoot();
        Point setTargetPoint = (Point) root.GetData(NodeKeys.TARGET_POINT);
        if (setTargetPoint != null && owner.GetDistance(setTargetPoint) < owner.GetDistance(targetPoint)) {
            String setterId = root.GetData(NodeKeys.TARGET_SETTER).toString();
            List<Point> setPath = pathFinder.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, setTargetPoint.x, setTargetPoint.y);
            if (setPath != null && setPath.size() < path.size() && displayId.equals(setterId)) {
                //TimerDisplay.LogTimeStamped("[" + displayId + "] Failed to select target due to another target being closer!");
                return Success(setPath.size());
            }
        }

        //TimerDisplay.LogTimeStamped("[" + displayId + "] Target point acquired: " + targetPoint + ", target ID: " + currentTarget.targetId);
        return Success(path.size(), targetPoint);
    }

    @Override
    protected boolean AcceptPoint(Point point) {
        Map<Direction, Integer> rangeMapping = AssessBombArea(point.x, point.y);
        return NodeState.SUCCESS.equals(theoryCrafter.Evaluate(point, pathFinder, ModifyBoardCost(rangeMapping, point)));
    }

    private int[][] CreateModifiedBoardCost(int[][] boardCost) {
        int[][] modifiedBoardCost = new int[boardCost.length][boardCost[0].length];
        for (int i = 0; i < boardCost.length; i++) {
            System.arraycopy(boardCost[i], 0, modifiedBoardCost[i], 0, boardCost[i].length);
        }

        for (int i = 0; i < modifiedBoardCost.length; i++) {
            for (int j = 0; j < modifiedBoardCost[i].length; j++) {
                modifiedBoardCost[i][j] = Math.min(1, modifiedBoardCost[i][j]);
            }
        }

        //AStar.PrintBoardCost(boardCost);
        //AStar.PrintBoardCost(modifiedBoardCost);
        return modifiedBoardCost;
    }

    private List<Point> FindRangedPath(List<Point> path) {

        if (path == null) {
            return null;
        }

        // Fix the path so that a portion is removed based on the player's range.
        Point lastPoint = path.remove(path.size() - 1);
        boolean xDiff, yDiff, xDiffTrack = false, yDiffTrack = false;
        for (int i = 1; i < range; i++) {
            if (path.size() == 0) {
                xDiff = srcPoint.x != lastPoint.x;
                yDiff = srcPoint.y != lastPoint.y;

                if ((xDiffTrack && yDiff) || (yDiffTrack && xDiff)) {
                    path.add(lastPoint);
                }

                break;
            }
            Point currentPoint = path.get(path.size() - 1);
            xDiff = currentPoint.x != lastPoint.x;
            yDiff = currentPoint.y != lastPoint.y;

            if (xDiff && !xDiffTrack) {
                xDiffTrack = true;
                if (yDiffTrack) {
                    path.add(lastPoint);
                    break;
                }
            }

            if (yDiff && !yDiffTrack) {
                yDiffTrack = true;
                if (xDiffTrack) {
                    path.add(lastPoint);
                    break;
                }
            }
            lastPoint = path.remove(path.size() - 1);
        }

        return path;
    }

    private void InitializePlayerTargets(List<Player> players) {
        playerTargets = new ArrayList<>();
        owner = players.get(playerId);
        for (Player player : players) {
            if (owner.equals(player)) continue;

            playerTargets.add(new PlayerTarget(owner, player, 1, 1));
        }
    }

    private void ComputeDistances() {
        for (PlayerTarget playerTarget : playerTargets) {
            playerTarget.ComputeDistance();
        }
    }

    private void LogPlayerTargets() {
        StringBuilder sb = new StringBuilder();
        sb.append(displayId);
        sb.append(": ");
        for (PlayerTarget playerTarget : playerTargets) {
            playerTarget.ComputeDistance();
            sb.append(playerTarget);
            sb.append(" ");
        }
        TimerDisplay.LogTimeStamped(sb.toString());
    }
}
