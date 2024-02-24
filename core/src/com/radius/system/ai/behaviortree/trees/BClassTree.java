package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.objects.BoardState;

public class BClassTree extends Tree {
    public BClassTree(int id, BoardState boardState) {
        super(id, (int)(GlobalConstants.WORLD_AREA * 0.75f), boardState);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector("[B] Root");

        root.AttachChild(new IsPlantingBomb());
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        root.AttachChild(ConstructAttackPlayerTree());
        root.AttachChild(ConstructBombAreaTree());

        return root;
    }
}
