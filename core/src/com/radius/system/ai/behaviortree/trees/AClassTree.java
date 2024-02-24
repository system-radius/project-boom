package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.RootSelector;
import com.radius.system.board.BoardState;

public class AClassTree extends Tree {
    public AClassTree(int id, BoardState boardState) {
        super(id, 2, boardState);
    }

    @Override
    protected Node SetupTree() {
        Node root = new RootSelector("[A] ROOT");

        root.AttachChild(new IsPlantingBomb());
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        root.AttachChild(ConstructFindBonusTree());
        root.AttachChild(ConstructAttackPlayerTree());
        root.AttachChild(ConstructBombAreaTree());

        return root;
    }
}
