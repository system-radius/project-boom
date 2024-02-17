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

    public FindPlayer(int id, int fireThreshold, BoardState boardState) {
        super(fireThreshold);
        this.id = id;
        this.fireThreshold = fireThreshold;
        this.players = boardState.GetPlayers();
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        super.Evaluate(depth, delta, boardCost);

        Point srcPoint = (Point) GetData(NodeKeys.SOURCE_POINT);
        if (srcPoint == null) {
            //System.out.println("[" + depth + ": FindPlayer] Returning failure!");
            GetRoot().ClearData(NodeKeys.MOVEMENT_PATH);
            return NodeState.FAILURE;
        }

        int pathCount = Integer.MAX_VALUE;
        Player target = null;

        for (Player player : players) {
            if (id == player.id || !player.IsAlive()) {
                continue;
            }

            List<Point> path = AStar.FindShortestPath(boardCost, srcPoint.x, srcPoint.y, player.GetWorldX(), player.GetWorldY());
            if (path != null && path.size() < pathCount && !(boardCost[player.GetWorldX()][player.GetWorldY()] > fireThreshold)) {
                target = player;
                pathCount = path.size();
            }
        }

        if (target == null) {
            //System.out.println("[" + depth + ": FindPlayer] Returning failure!");
            GetRoot().ClearData(NodeKeys.MOVEMENT_PATH);
            return NodeState.FAILURE;
        }

        Point targetPoint = new Point(null, target.GetWorldX(), target.GetWorldY(), 0, 0, 0);
        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        //System.out.println("[" + depth + ": FindPlayer] Returning success! Found player at: " + targetPoint.x + ", " + targetPoint.y + "!");
        return NodeState.SUCCESS;
    }
}
