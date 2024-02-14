package com.radius.system.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {

    private static AStar instance;

    private final List<Node> openLlist = new ArrayList<>();

    private final List<Node> closedList = new ArrayList<>();

    private int[][] maze;

    private AStar() {}

    public static List<Node> FindShortestPath(int[][] boardCost, int srcX, int srcY, int dstX, int dstY) {
        if (instance == null) {
            instance = new AStar();
        }

        return instance.FindShortestPathInternal(boardCost, srcX, srcY, dstX, dstY);
    }

    private List<Node> FindShortestPathInternal(int[][] boardCost, int srcX, int srcY, int dstX, int dstY) {
        openLlist.clear();
        closedList.clear();

        maze = boardCost;
        Node now = new Node(null, srcX, srcY, 0, 0);

        for (openLlist.add(now); openLlist.size() > 0;) {
            now = this.openLlist.remove(0);
            int x = now.x;
            int y = now.y;

            if (x == dstX && y == dstY) {
                return ReconstructPath(now);
            }

            closedList.add(now);
            AddChildrenToOpenList(now, dstX, dstY);
        }

        return null;
    }

    private void AddChildrenToOpenList(Node parent, int dstX, int dstY) {
        int parentX = parent.x, parentY = parent.y;
        float parentG = parent.g;

        for (int x = -1; x <= 1; x++) {

            int childX = parentX + x;
            if (childX < 0 || childX >= maze.length) {
                continue;
            }

            for (int y = -1; y <= 1; y++) {
                int childY = parentY + y;
                if ((x != 0 && y != 0) || childY < 0 || childY >= maze[0].length || maze[childX][childY] < 0) {
                    continue;
                }

                if (x == y || (Math.abs(x) == Math.abs(y))) {
                    continue;
                }

                Node child = new Node(parent, childX, childY, parentG + 1 + maze[childX][childY], ComputeHeuristic(childX, childY, dstX, dstY));
                if (!(FindInList(openLlist, child) || FindInList(closedList, child))) {
                    openLlist.add(child);
                }
            }
        }

        Collections.sort(openLlist);
    }

    private float ComputeHeuristic(int x, int y, int dstX, int dstY) {
        return (float) Math.abs(x - dstX) + Math.abs(y - dstY);
    }

    private boolean FindInList(List<Node> nodes, Node node) {
        for (Node inList : nodes) {
            if (node.x == inList.x && node.y == inList.y) {
                return true;
            }
        }

        return false;
    }

    private List<Node> ReconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        path.add(node);
        for (Node parent = node.GetParent(); parent != null; parent = parent.GetParent()) {
            if (parent.GetParent() == null) {
                break;
            }

            path.add(0, parent);
        }

        return path;
    }

}
