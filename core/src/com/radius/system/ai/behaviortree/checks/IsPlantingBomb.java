package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.enums.NodeState;

public class IsPlantingBomb extends Node {
    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {

        Boolean isPlantingBomb = (Boolean) GetRoot().GetData(NodeKeys.PLANT_BOMB);
        if (isPlantingBomb != null && isPlantingBomb) {
            return NodeState.SUCCESS;
        }

        return NodeState.FAILURE;
    }
}
