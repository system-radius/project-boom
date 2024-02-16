package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.enums.NodeState;

import java.util.List;

public class Sequencer extends Node {

    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        boolean anyRunning = false;
        for (Node node : children) {
            switch (node.Evaluate(depth + 1, delta, boardCost)) {
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
}
