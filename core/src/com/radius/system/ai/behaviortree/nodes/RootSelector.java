package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class RootSelector extends Selector {
    public RootSelector(String id) {
        super(id);
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        boolean hasSuccess = false;
        boolean hasRunning = false;

        state = NodeState.FAILURE;
        for (Node node : children) {
            switch (node.Start(srcPoint, pathFinder, boardCost)) {
                case FAILURE:
                    continue;
                case SUCCESS:
                    if (hasRunning) continue;
                    hasSuccess = true;
                    state = NodeState.SUCCESS;
                    break;
                case RUNNING:
                    if (hasSuccess) continue;
                    hasRunning = true;
                    state = NodeState.RUNNING;
                    break;
            }
        }

        return state;
    }
}
