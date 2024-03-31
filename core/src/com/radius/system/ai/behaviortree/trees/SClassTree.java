package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.checks.OnFirePath;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.FindBombArea;
import com.radius.system.ai.behaviortree.tasks.FindPlayer;
import com.radius.system.ai.behaviortree.tasks.FindSafeSpace;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.ai.behaviortree.tasks.PlantBomb;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;

public class SClassTree extends Tree {

    public SClassTree(int id, BoardState boardState, Player player) {
        super(id, (int)(GlobalConstants.WORLD_AREA * 0.65f), boardState, player);
    }

    @Override
    protected void Evaluate(int[][] boardCost) {
        super.Evaluate(boardCost);
    }

    @Override
    protected Node SetupTree() {
        Node root = new Selector("[S] ROOT");

        root.AttachChild(new IsPlantingBomb());
        root.AttachChild(ConstructDefenseTree(fireThreshold, false));
        root.AttachChild(ConstructFindBonusTree());
        root.AttachChild(ConstructAttackPlayerTree());
        root.AttachChild(ConstructBombAreaTree());
        root.AttachChild(ConstructDefenseTree(2, true));

        return root;
    }

    @Override
    protected Node ConstructDefenseTree(int fireThreshold, boolean backup) {
        Node findSafeSpaceTarget = new Selector("[+] FindSpace");
        //findSafeSpaceTarget.AttachChild(new HasTargetPoint());
        findSafeSpaceTarget.AttachChild(new FindSafeSpace(fireThreshold, boardState, boardState.GetPlayers().get(id)));

        Node root = new Sequencer("[>] Defense" + fireThreshold);
        root.AttachChild(new OnFirePath(fireThreshold, backup));
        root.AttachChild(findSafeSpaceTarget);
        root.AttachChild(new MoveToTarget());

        return root;
    }

    @Override
    protected Node ConstructAttackPlayerTree() {
        Node root = new Sequencer("[>] AttackP");
        root.AttachChild(new FindPlayer(id, fireThreshold, boardState));
        root.AttachChild(new MoveToTarget(new PlantBomb()));

        return root;
    }

    @Override
    protected Node ConstructBombAreaTree() {
        Node findBombAreaTarget = new Selector("[+] FindArea");
        //findBombAreaTarget.AttachChild(new HasTargetPoint());
        findBombAreaTarget.AttachChild(new FindBombArea(fireThreshold, boardState, boardState.GetPlayers().get(id)));

        Node root = new Sequencer("[>] AttackA");
        root.AttachChild(findBombAreaTarget);
        root.AttachChild(new MoveToTarget(new PlantBomb()));
        return root;
    }
}
