package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class Sequencer extends Node {

    public Sequencer(String id) {
        this.id = id;
    }

    public Sequencer(String id, Node... children) {
        super(id, children);
    }

    @Override
    public NodeState Evaluate(Point srcPoint, PathFinder pathFinder, int[][] boardCost) {
        boolean anyRunning = false;
        for (Node node : children) {
            switch (node.Evaluate(srcPoint, pathFinder, boardCost)) {
                case FAILURE:
                    state = NodeState.FAILURE;
                    return state;
                case SUCCESS:
                    continue;
                case RUNNING:
                    anyRunning = true;
                    continue;
                default:
                    state = NodeState.SUCCESS;
                    return state;
            }
        }

        state = anyRunning ? NodeState.RUNNING : NodeState.SUCCESS;
        return state;
    }

    @Override
    public void Execute() {
        // The fact that this method is called means that all children from this node succeeded.
        for (Node child : children) {
            child.Execute();
        }
    }
}
