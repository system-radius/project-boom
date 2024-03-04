package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.board.BoardState;

public class EClassTree extends Tree {

    public EClassTree(int id, BoardState boardState) {
        super(id, 2, boardState);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector("[E] Root");
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        return root;
    }
}
