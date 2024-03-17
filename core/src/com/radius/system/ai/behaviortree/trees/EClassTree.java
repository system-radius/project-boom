package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public class EClassTree extends Tree {

    public EClassTree(int id, BoardState boardState, Player player) {
        super(id, 2, boardState, player);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector("[E] Root");
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        return root;
    }
}
