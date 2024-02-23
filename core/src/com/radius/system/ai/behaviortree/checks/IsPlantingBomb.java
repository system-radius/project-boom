package com.radius.system.ai.behaviortree.checks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.NoExecuteNode;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

public class IsPlantingBomb extends NoExecuteNode {

    public IsPlantingBomb() {
        id = "[?] IsPlantingBomb";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {

        Boolean isPlantingBomb = (Boolean) GetRoot().GetData(NodeKeys.PLANT_BOMB);
        if (isPlantingBomb != null && isPlantingBomb) {
            return Success(0);
        }

        return Failure();
    }
}
