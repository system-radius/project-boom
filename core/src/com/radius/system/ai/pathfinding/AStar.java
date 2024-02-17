package com.radius.system.ai.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {

    private static AStar instance;

    private final List<Point> openList = new ArrayList<>();

    private final List<Point> closedList = new ArrayList<>();

    private int[][] maze;

    private AStar() {}

    public static List<Point> FindShortestPath(int[][] boardCost, int srcX, int srcY, int dstX, int dstY) {
        if (instance == null) {
            instance = new AStar();
        }

        return instance.FindShortestPathInternal(boardCost, srcX, srcY, dstX, dstY);
    }

    public static List<Point> FindOpenSpaces(int[][] boardCost, int srcX, int srcY, int depthLimit) {
        if (instance == null) {
            instance = new AStar();
        }

        return instance.FindOpenSpacesInternal(boardCost, srcX, srcY, depthLimit);
    }

    private List<Point> FindShortestPathInternal(int[][] boardCost, int srcX, int srcY, int dstX, int dstY) {
        openList.clear();
        closedList.clear();

        maze = boardCost;
        Point now = new Point(null, srcX, srcY, boardCost[srcX][srcY], 0, 0);

        for (openList.add(now); openList.size() > 0;) {
            now = this.openList.remove(0);
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

    private List<Point> FindOpenSpacesInternal(int[][] boardCost, int srcX, int srcY, int depthLimit) {
        openList.clear();
        closedList.clear();

        maze = boardCost;
        Point now = new Point(null, srcX, srcY, boardCost[srcX][srcY], 0, 0);
        openList.add(now);

        for (int i = 0; openList.size() > 0; i++) {
            now = this.openList.remove(0);

            if (depthLimit >= 0 && i >= depthLimit) {
                break;
            }

            closedList.add(now);
            AddChildrenToOpenList(now, srcX, srcY);
        }

        return closedList;
    }

    private void AddChildrenToOpenList(Point parent, int hX, int hY) {
        int parentX = parent.x, parentY = parent.y;
        float parentG = parent.parentCost;

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

                Point child = new Point(parent, childX, childY, maze[childX][childY], parentG + 1 + maze[childX][childY], ComputeHeuristic(childX, childY, hX, hY));
                if (!(FindInList(openList, child) || FindInList(closedList, child))) {
                    openList.add(child);
                }
            }
        }

        Collections.sort(openList);
    }

    private float ComputeHeuristic(int x, int y, int dstX, int dstY) {

        if (dstX < 0 || dstY < 0) {
            return 0;
        }

        return (float) Math.abs(x - dstX) + Math.abs(y - dstY);
    }

    private boolean FindInList(List<Point> points, Point point) {
        for (Point inList : points) {
            if (point.IsEqualPosition(inList)) {
                return true;
            }
        }

        return false;
    }

    private List<Point> ReconstructPath(Point point) {
        List<Point> path = new ArrayList<>();
        path.add(point);
        for (Point parent = point.GetParent(); parent != null; parent = parent.GetParent()) {
            if (parent.GetParent() == null) {
                break;
            }

            path.add(0, parent);
        }

        return path;
    }

}
