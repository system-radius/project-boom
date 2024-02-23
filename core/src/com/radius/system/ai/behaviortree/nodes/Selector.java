package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

import java.util.List;

public class Selector extends Node {

    public Selector(String id) {
        this.id = id;
    }

    public Selector(String id, Node... children) {
        super(id, children);
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        for (Node node : children) {
            switch (node.Evaluate(srcPoint, boardCost)) {
                case FAILURE:
                    continue;
                case SUCCESS:
                    state = NodeState.SUCCESS;
                    return state;
                case RUNNING:
                    state = NodeState.RUNNING;
                    return state;
            }
        }

        state = NodeState.FAILURE;
        return state;
    }

    @Override
    public void Execute() {
        for (Node child : children) {
            // Only execute the child that has succeeded or is running.
            switch (child.state) {
                case SUCCESS:
                case RUNNING:
                    child.Execute();
                    return;
            }
        }
    }

}
