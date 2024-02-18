package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.checks.OnFirePath;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.FindBonus;
import com.radius.system.ai.behaviortree.tasks.FindPlayer;
import com.radius.system.ai.behaviortree.tasks.FindSpace;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.ai.behaviortree.tasks.PlantBomb;
import com.radius.system.states.BoardState;

public class DefaultTree extends Tree {

    public DefaultTree(int id, int fireThreshold, BoardState boardState) {
        super(id, fireThreshold, boardState);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector();

        Node moveToSafety = new Sequencer();
        moveToSafety.AttachChild(new OnFirePath(fireThreshold));
        moveToSafety.AttachChild(new FindSpace());
        moveToSafety.AttachChild(new MoveToTarget());
        root.AttachChild(moveToSafety);

        Node findBonus = new Sequencer();
        findBonus.AttachChild(new FindBonus(fireThreshold, boardState));
        findBonus.AttachChild(new MoveToTarget());
        root.AttachChild(findBonus);

        Node attackPlayer = new Sequencer();
        attackPlayer.AttachChild(new FindPlayer(id, fireThreshold, boardState));
        attackPlayer.AttachChild(new MoveToTarget());
        attackPlayer.AttachChild(new PlantBomb());

        root.AttachChild(attackPlayer);

        root.ClearData(NodeKeys.PLANT_BOMB);
        return root;
    }
}
