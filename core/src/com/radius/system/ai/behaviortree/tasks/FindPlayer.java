package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Solidifier;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.states.BoardState;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;

import java.util.List;

public class FindPlayer extends Solidifier {

    private final List<Player> players;

    private final int id, fireThreshold;

    private Point srcPoint;

    private int range;

    public FindPlayer(int id, int fireThreshold, BoardState boardState) {
        super(fireThreshold);
        this.id = id;
        this.fireThreshold = fireThreshold;
        this.players = boardState.GetPlayers();
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        super.Evaluate(depth, delta, boardCost);

        srcPoint = (Point) GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            //System.out.println("[" + depth + ": FindPlayer] Returning failure!");
            GetRoot().ClearData(NodeKeys.MOVEMENT_PATH);
            return NodeState.FAILURE;
        }

        int pathCount = Integer.MAX_VALUE;
        range = players.get(id).GetFirePower();
        Player target = null;
        List<Point> path = null;
        for (Player player : players) {
            if (id == player.id || !player.IsAlive()) {
                continue;
            }

            path = FindRangedPath(AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, player.GetWorldX(), player.GetWorldY()));
            if (path != null && path.size() < pathCount && !(boardCost[player.GetWorldX()][player.GetWorldY()] > fireThreshold)) {
                target = player;
                pathCount = path.size();
            }
        }

        if (target == null || path == null) {
            //System.out.println("[" + depth + ": FindPlayer] Returning failure!");
            GetRoot().ClearData(NodeKeys.MOVEMENT_PATH);
            return NodeState.FAILURE;
        }

        Point targetPoint = srcPoint;
        if (path.size() > 0) {
            targetPoint = path.get(path.size() - 1);
            //System.out.println("[" + depth + ": FindPlayer] Returning success! Found player at: " + targetPoint.x + ", " + targetPoint.y + "!");
        }
        GetParent(1).SetData(NodeKeys.TARGET_POINT, targetPoint);
        return NodeState.SUCCESS;
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
}
