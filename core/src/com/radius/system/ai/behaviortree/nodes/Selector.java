package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.enums.NodeState;

import java.util.List;

public class Selector extends Node {

    public Selector(Node... children) {
        super(children);
    }

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        for (Node node : children) {
            switch (node.Evaluate(depth + 1, delta, boardCost)) {
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

}
