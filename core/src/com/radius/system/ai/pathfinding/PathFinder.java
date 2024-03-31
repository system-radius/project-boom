package com.radius.system.ai.pathfinding;

import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathFinder {

    private final List<PathFinderPoint> openList = new ArrayList<>();

    private final List<PathFinderPoint> closedList = new ArrayList<>();

    private final PathFinderPoint[][] pointsPool;

    private int[][] maze;

    public PathFinder(BoardState boardState) {
        int width = boardState.BOARD_WIDTH, height = boardState.BOARD_HEIGHT;
        pointsPool = new PathFinderPoint[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                switch(boardState.GetBoardEntry(i, j)) {
                    case PERMANENT_BLOCK:
                    case VOID:
                        pointsPool[i][j] = null;
                        break;
                    default:
                        pointsPool[i][j] = new PathFinderPoint(null, -1, -1);
                }
            }
        }
    }

    public List<Point> FindShortestPath(int[][] boardCost, int srcX, int srcY, int dstX, int dstY) {
        return FindShortestPath(boardCost, srcX, srcY, dstX, dstY, GlobalConstants.WORLD_AREA + 1);
    }

    public List<Point> FindShortestPath(int[][] boardCost, int srcX, int srcY, int dstX, int dstY, float costThreshold) {
        Reset();

        maze = boardCost;
        PathFinderPoint now = new PathFinderPoint(null, srcX, srcY, boardCost[srcX][srcY], 0, 0);

        for (openList.add(now); openList.size() > 0;) {
            now = this.openList.remove(0);
            int x = now.x;
            int y = now.y;

            if (x == dstX && y == dstY) {
                return ReconstructPath(now);
            }

            if (now.selfCost < costThreshold) {
                closedList.add(now);
            }
            AddChildrenToOpenList(now, dstX, dstY);
        }

        return null;
    }

    public List<Point> FindOpenSpaces(int[][] boardCost, int srcX, int srcY, int depthLimit) {
        return FindOpenSpaces(boardCost, srcX, srcY, depthLimit, GlobalConstants.WORLD_AREA + 1);
    }

    public List<Point> FindOpenSpaces(int[][] boardCost, int srcX, int srcY, int depthLimit, float costThreshold) {
        Reset();

        maze = boardCost;
        PathFinderPoint now = new PathFinderPoint(null, srcX, srcY, boardCost[srcX][srcY], 0, 0);
        openList.add(now);

        for (int i = 0; openList.size() > 0; i++) {
            now = this.openList.remove(0);

            if (depthLimit >= 0 && i >= depthLimit) {
                break;
            }

            if (now.selfCost < costThreshold) {
                closedList.add(now);
            }
            AddChildrenToOpenList(now, srcX, srcY);
        }

        return ConvertPoints(closedList);
    }

    private void AddChildrenToOpenList(PathFinderPoint parent, int hX, int hY) {
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

                PathFinderPoint child = new PathFinderPoint(parent, childX, childY, maze[childX][childY], parentG + 1 + maze[childX][childY], ComputeHeuristic(childX, childY, hX, hY));
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

    private boolean FindInList(List<PathFinderPoint> points, PathFinderPoint point) {
        for (PathFinderPoint inList : points) {
            if (point.IsEqualPosition(inList)) {
                return true;
            }
        }

        return false;
    }

    private List<Point> ConvertPoints(List<PathFinderPoint> points) {
        List<Point> convertedPoints = new ArrayList<>();
        for (PathFinderPoint point : points) {
            convertedPoints.add(new Point(point));
        }
        return convertedPoints;
    }

    private void Reset() {
        openList.clear();
        closedList.clear();

        for (int i = 0; i < pointsPool.length; i++) {
            for (int j = 0; j < pointsPool[i].length; j++) {
                PathFinderPoint point = pointsPool[i][j];
                if (point != null) {
                    point.parent = null;
                    point.x = point.y = -1;
                    point.parentCost = point.selfCost = point.h = 0;
                }
            }
        }
    }

    public static List<Point> ReconstructPath(PathFinderPoint point) {
        List<Point> path = new ArrayList<>();
        path.add(new Point(point));
        for (PathFinderPoint parent = point.GetParent(); parent != null; parent = parent.GetParent()) {
            if (parent.GetParent() == null) {
                break;
            }

            path.add(0, new Point(parent));
        }

        return path;
    }

    public static void PrintBoardCost(int[][] boardCost) {
        StringBuilder sb = new StringBuilder();
        for (int j = boardCost.length - 1; j >= 0; j--) {
            for (int i = 0; i < boardCost[j].length; i++) {
                sb.append('[');
                int cost = boardCost[i][j];
                if (cost < 0) {
                    sb.append('x');
                } else if (cost > 1) {
                    sb.append('!');
                } else {
                    sb.append(' ');
                }
                sb.append("] ");
            }
            sb.append('\n');
        }

        System.out.println(sb);
    }

}
