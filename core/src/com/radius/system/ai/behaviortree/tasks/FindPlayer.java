package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.PlayerTarget;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.BoardRep;
import com.radius.system.board.BoardState;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindPlayer extends Solidifier {

    private final BoardState boardState;

    private final int playerId;

    private Player owner;

    private List<PlayerTarget> playerTargets;

    private int range;

    public FindPlayer(int id, int fireThreshold, BoardState boardState) {
        super(fireThreshold);
        this.playerId = id;

        this.boardState = boardState;

        this.id = "[!] FindPlayer";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        if (playerTargets != null) {
            ComputeDistances();
        } else {
            InitializePlayerTargets(boardState.GetPlayers());
        }

        int[][] modifiedBoardCost = CreateModifiedBoardCost(boardCost);
        super.Evaluate(srcPoint, boardCost);
        this.srcPoint = srcPoint;

        range = owner.GetFirePower();
        List<Point> path = null;
        Point targetPoint = srcPoint;
        Collections.sort(playerTargets);
        for (PlayerTarget playerTarget : playerTargets) {
            if (!playerTarget.IsTargetAlive()) {
                continue;
            }

            path = FindRangedPath(AStar.FindShortestPath(modifiedBoardCost, srcPoint.x, srcPoint.y, playerTarget.GetWorldX(), playerTarget.GetWorldY()));
            if (path != null) {
                //  && !(boardCost[player.GetWorldX()][player.GetWorldY()] > fireThreshold)
                //System.out.println("Found shorter path detecting player " + player.id + ", size: " + path.size());
                // Verify that the last point on the path can be reached.
                Point tempTargetPoint = srcPoint;
                if (path.size() > 0) {
                    tempTargetPoint = path.get(path.size() - 1);
                }
                List<Point> internalPath = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, tempTargetPoint.x, tempTargetPoint.y);
                if (internalPath != null) {
                    targetPoint = tempTargetPoint;
                }
            }
        }

        if (path == null) {
            GetRoot().ClearData(NodeKeys.MOVEMENT_PATH);
            //System.out.println("[" + displayId + "] Failed to select target due to null path/target!!");
            return Failure();
        }

        if (BoardRep.BOMB.equals(boardState.GetBoardEntry(targetPoint.x, targetPoint.y))) {
            //System.out.println("[" + displayId + "] Failed to select target due to bomb placement!");
            return Failure();
        }

        GetParent(1).SetData(NodeKeys.TARGET_POINT, targetPoint);
        //System.out.println("[" + displayId + "] Target point acquired: " + targetPoint);
        return Success(path.size());
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
}
