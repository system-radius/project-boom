package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class PlantBomb extends Node {

    public PlantBomb() {
        id = "[!] PlantBomb";
        Success(0);
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        return Success(0);
    }

    @Override
    public void Execute() {
        GetRoot().SetData(NodeKeys.PLANT_BOMB, true);
        ClearFullData(NodeKeys.TARGET_POINT);
    }
}
