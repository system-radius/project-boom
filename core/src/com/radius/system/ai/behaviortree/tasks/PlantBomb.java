package com.radius.system.ai.behaviortree.tasks;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.nodes.NoExecuteNode;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;
import com.radius.system.objects.BoardState;
import com.radius.system.objects.players.Player;

public class PlantBomb extends NoExecuteNode {

    public PlantBomb() {
        id = "[!] PlantBomb";
    }

    @Override
    public NodeState Evaluate(Point srcPoint, int[][] boardCost) {
        GetRoot().SetData(NodeKeys.PLANT_BOMB, true);
        ClearFullData(NodeKeys.TARGET_POINT);

        return Success();
    }
}
