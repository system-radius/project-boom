package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.states.BoardState;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.players.Player;

import java.util.List;

public class FindPlayer extends Node {

    private List<Player> players;

    private int id;

    public FindPlayer(int id, BoardState boardState) {
        this.id = id;
        this.players = boardState.GetPlayers();
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

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
            if (path != null && path.size() < pathCount) {
                target = player;
                pathCount = path.size();
            }
        }

        if (target == null) {
            //System.out.println("[" + depth + ": FindPlayer] Returning failure!");
            GetRoot().ClearData(NodeKeys.MOVEMENT_PATH);
            return NodeState.FAILURE;
        }

        Point targetPoint = new Point(null, target.GetWorldX(), target.GetWorldY(), 0, 0);
        GetRoot().SetData(NodeKeys.TARGET_POINT, targetPoint);
        //System.out.println("[" + depth + ": FindPlayer] Returning success! Found player at: " + targetPoint.x + ", " + targetPoint.y + "!");
        return NodeState.SUCCESS;
    }
}
