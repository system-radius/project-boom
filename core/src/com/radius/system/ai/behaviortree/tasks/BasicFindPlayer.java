package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.PlayerTarget;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.board.BoardState;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicFindPlayer extends Solidifier {

    protected final BoardState boardState;

    protected final int playerId, fireThreshold;

    protected Player owner;

    protected List<PlayerTarget> playerTargets;

    public BasicFindPlayer(int id, int fireThreshold, BoardState boardState) {
        this.boardState = boardState;
        this.fireThreshold = fireThreshold;
        this.playerId = id;
        this.id = "[!] BasicAttackPlayer";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        if (playerTargets != null) {
            ComputeDistances();
        } else {
            InitializePlayerTargets(boardState.GetPlayers());
        }

        int[][] solidifiedBoardCost = SolidifyBoardCopy(boardCost, fireThreshold);
        List<Point> path = null;
        Point targetPoint = srcPoint;
        Collections.sort(playerTargets);
        for (PlayerTarget playerTarget : playerTargets) {
            if (!playerTarget.IsTargetAlive()) {
                continue;
            }

            path = pathFinder.FindShortestPath(solidifiedBoardCost, srcPoint.x, srcPoint.y, playerTarget.GetWorldX(), playerTarget.GetWorldY());
            if (path != null) {
                targetPoint = path.get(path.size() - 1);
                break;
            }
        }

        if (path == null || targetPoint == null) {
            return Failure();
        }

        return Success(path.size(), targetPoint);
    }

    protected void InitializePlayerTargets(List<Player> players) {
        playerTargets = new ArrayList<>();
        owner = players.get(playerId);
        for (Player player : players) {
            if (owner.equals(player)) continue;

            playerTargets.add(new PlayerTarget(owner, player, 1, 1));
        }
    }

    protected void ComputeDistances() {
        for (PlayerTarget playerTarget : playerTargets) {
            playerTarget.ComputeDistance();
        }
    }

}
