package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public class CClassTree extends Tree {
    public CClassTree(int id, BoardState boardState, Player player) {
        super(id, 2, boardState, player);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector("[C] Root");

        root.AttachChild(new IsPlantingBomb());
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        root.AttachChild(ConstructBombAreaTree());
        root.AttachChild(ConstructAttackPlayerTree());

        return root;
    }
}
