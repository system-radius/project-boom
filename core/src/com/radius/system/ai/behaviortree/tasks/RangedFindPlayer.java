package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.PlayerTarget;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.board.BoardState;
import com.radius.system.enums.NodeState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RangedFindPlayer extends BasicFindPlayer {

    protected int range;

    public RangedFindPlayer(int id, int fireThreshold, BoardState boardState) {
        super(id, fireThreshold, boardState);
        this.id = "[!] RangedAttackPlayer";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        if (playerTargets != null) {
            ComputeDistances();
        } else {
            InitializePlayerTargets(boardState.GetPlayers());
        }

        this.srcPoint = srcPoint;
        range = owner.GetFirePower();
        int[][] solidifiedBoardCost = SolidifyBoardCopy(boardCost, fireThreshold);
        List<Point> path = null;
        Point targetPoint = srcPoint;
        Collections.sort(playerTargets);
        for (PlayerTarget playerTarget : playerTargets) {
            if (!playerTarget.IsTargetAlive()) {
                continue;
            }

            path = FindRangedPath(pathFinder.FindShortestPath(solidifiedBoardCost, srcPoint.x, srcPoint.y, playerTarget.GetWorldX(), playerTarget.GetWorldY()));
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

    protected List<Point> FindRangedPath(List<Point> path) {

        if (path == null) {
            return null;
        }

        List<Point> points = new ArrayList<>();

        // Fix the path so that a portion is removed based on the player's range.
        Point lastPoint = path.remove(path.size() - 1);
        points.add(lastPoint);
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
            points.add(lastPoint);
        }

        return points;
    }
}
