package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.checks.HasTargetPoint;
import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.checks.OnFirePath;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.FindBombArea;
import com.radius.system.ai.behaviortree.tasks.FindBonus;
import com.radius.system.ai.behaviortree.tasks.FindPlayer;
import com.radius.system.ai.behaviortree.tasks.FindSafeSpace;
import com.radius.system.ai.behaviortree.tasks.FindSpace;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.ai.behaviortree.tasks.PlantBomb;
import com.radius.system.states.BoardState;
import com.radius.system.objects.BoomUpdatable;

import java.util.ArrayList;

public abstract class Tree implements BoomUpdatable {

    protected final int id, fireThreshold;

    protected final BoardState boardState;

    private final int[][] boardCost;

    private final Node root;

    public Tree(int id, int fireThreshold, BoardState boardState) {

        this.id =  id;
        this.fireThreshold = fireThreshold;
        this.boardState = boardState;
        boardCost = new int[boardState.BOARD_WIDTH][boardState.BOARD_HEIGHT];

        root = SetupTree();
    }

    public void Restart() {
        root.Restart();
    }

    public void SetData(String key, Object value) {
        root.SetData(key, value);
    }

    public boolean ClearData(String key) {
        return root.ClearData(key);
    }

    public Object GetData(String key) {
        return root.GetData(key);
    }

    @Override
    public final void Update(float delta) {
        if (root != null) {
            boardState.CompileBoardCost(boardCost, fireThreshold, id);
            root.Evaluate(0, delta, boardCost);
        }
    }

    protected Node SetupTree() {
        Node root = new Selector("[+] ROOT");
        //root.AttachChild(new IsPlantingBomb());
        root.AttachChild(ConstructDefenseTree(fireThreshold));
        root.AttachChild(ConstructFindBonusTree());
        root.AttachChild(ConstructAttackPlayerTree());
        root.AttachChild(ConstructBombAreaTree());
        root.AttachChild(ConstructDefenseTree(2));

        return root;
    }

    protected Node ConstructDefenseTree(int fireThreshold) {
        Node findSafeSpaceTarget = new Selector("[+] FindSpace");
        findSafeSpaceTarget.AttachChild(new HasTargetPoint());
        findSafeSpaceTarget.AttachChild(new FindSafeSpace(fireThreshold));

        Node root = new Sequencer("[>] Defense" + fireThreshold);
        root.AttachChild(new OnFirePath(fireThreshold));
        root.AttachChild(findSafeSpaceTarget);
        root.AttachChild(new MoveToTarget());

        return root;
    }

    protected Node ConstructFindBonusTree() {
        Node root = new Sequencer("[>] Bonus");
        root.AttachChild(new FindBonus(fireThreshold, boardState));
        root.AttachChild(new MoveToTarget());

        return root;
    }

    protected Node ConstructAttackPlayerTree() {
        Node root = new Sequencer("[>] AttackP");
        root.AttachChild(new FindPlayer(id, fireThreshold, boardState));
        root.AttachChild(new MoveToTarget(new PlantBomb()));

        return root;
    }

    protected Node ConstructBombAreaTree() {
        Node findBombAreaTarget = new Selector("[+] FindArea");
        findBombAreaTarget.AttachChild(new HasTargetPoint());
        findBombAreaTarget.AttachChild(new FindBombArea(fireThreshold, boardState, boardState.GetPlayers().get(id)));

        Node root = new Sequencer("[>] AttackA");
        root.AttachChild(findBombAreaTarget);
        root.AttachChild(new MoveToTarget(new PlantBomb()));
        return root;
    }

}
