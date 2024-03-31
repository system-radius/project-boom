package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.ai.behaviortree.tasks.PlantBomb;
import com.radius.system.ai.behaviortree.tasks.RangedFindPlayer;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public class AClassTree extends Tree {
    public AClassTree(int id, BoardState boardState, Player player) {
        super(id, 2, boardState, player);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector("[A] ROOT");

        root.AttachChild(new IsPlantingBomb());
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        root.AttachChild(ConstructFindBonusTree());
        root.AttachChild(ConstructAttackPlayerTree());
        root.AttachChild(ConstructBombAreaTree());

        return root;
    }

    @Override
    protected Node ConstructAttackPlayerTree() {
        Node root = new Sequencer("[>] AttackP");
        root.AttachChild(new RangedFindPlayer(id, fireThreshold, boardState));
        root.AttachChild(new MoveToTarget(new PlantBomb()));

        return root;
    }
}
