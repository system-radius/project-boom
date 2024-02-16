package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.FindPlayer;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.states.BoardState;

public class DefaultTree extends Tree {

    public DefaultTree(int id, BoardState boardState) {
        super(id, boardState);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector();

        Node moveToTarget = new Sequencer();
        moveToTarget.AttachChild(new FindPlayer(id, boardState));
        moveToTarget.AttachChild(new MoveToTarget());

        root.AttachChild(moveToTarget);

        return root;
    }
}
