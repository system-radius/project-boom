package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.RootSelector;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;

public class OmegaTree extends Tree {

    public OmegaTree(int id, BoardState boardState) {
        super(id, (int)(GlobalConstants.WORLD_AREA * 0.75f), boardState);
    }

    @Override
    protected Node SetupTree() {
        Node root = new RootSelector("[0] ROOT");

        root.AttachChild(new IsPlantingBomb());
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        root.AttachChild(ConstructFindBonusTree());
        root.AttachChild(ConstructAttackPlayerTree());
        root.AttachChild(ConstructBombAreaTree());
        root.AttachChild(ConstructDefenseTree(2, true));

        return root;
    }
}
