package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.enums.NodeState;

public class PlantBomb extends Node {
    @Override
    public NodeState Evaluate(int depth, float delta, int[][] boardCost) {
        GetRoot().SetData(NodeKeys.PLANT_BOMB, true);
        return NodeState.SUCCESS;
    }
}
