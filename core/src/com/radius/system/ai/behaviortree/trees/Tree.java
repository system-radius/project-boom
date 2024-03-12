package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.checks.OnFirePath;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.BasicFindBombArea;
import com.radius.system.ai.behaviortree.tasks.BasicFindPlayer;
import com.radius.system.ai.behaviortree.tasks.FindBombArea;
import com.radius.system.ai.behaviortree.tasks.FindBonus;
import com.radius.system.ai.behaviortree.tasks.FindPlayer;
import com.radius.system.ai.behaviortree.tasks.FindSafeSpace;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.ai.behaviortree.tasks.PlantBomb;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;
import com.radius.system.board.BoardState;
import com.radius.system.objects.BoomUpdatable;

import java.util.List;

public abstract class Tree implements BoomUpdatable {

    protected final int id, fireThreshold;

    protected final BoardState boardState;

    protected final int[][] boardCost;

    protected final Node root;

    protected final Point srcPoint;

    protected final PathFinder pathFinder;

    public Tree(int id, int fireThreshold, BoardState boardState) {

        this.id =  id;
        this.fireThreshold = fireThreshold;
        this.boardState = boardState;
        boardCost = new int[boardState.BOARD_WIDTH][boardState.BOARD_HEIGHT];

        srcPoint = new Point(null, -1, -1);
        this.pathFinder = new PathFinder();

        root = SetupTree();
    }

    public void Restart() {
        root.Restart();
    }

    public boolean ClearData(String key) {
        return root.ClearData(key);
    }

    public Object GetData(String key) {
        return root.GetData(key);
    }

    public void SetSourcePoint(int x, int y) {
        srcPoint.x = x;
        srcPoint.y = y;
    }

    @Override
    public final void Update(float delta) {
        if (root != null) {
            boardState.CompileBoardCost(boardCost, fireThreshold, id);
            Evaluate(boardCost);
        }
    }

    protected void Evaluate(int[][] boardCost) {
        NodeState state = root.Start(srcPoint, pathFinder, boardCost);
        List<Node> children = root.GetChildren();
        for (Node child : children) {
            if (state.equals(child.GetState())) {
                child.Execute();
                break;
            }
        }
    }

    protected abstract Node SetupTree();

    protected Node ConstructDefenseTree(int fireThreshold, boolean backup) {
        Node findSafeSpaceTarget = new Selector("[+] FindSpace");
        //findSafeSpaceTarget.AttachChild(new HasTargetPoint());
        findSafeSpaceTarget.AttachChild(new FindSafeSpace(fireThreshold));

        Node root = new Sequencer("[>] Defense" + fireThreshold);
        root.AttachChild(new OnFirePath(fireThreshold, backup));
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
        root.AttachChild(new BasicFindPlayer(id, fireThreshold, boardState));
        root.AttachChild(new MoveToTarget(new PlantBomb()));

        return root;
    }

    protected Node ConstructBombAreaTree() {
        Node findBombAreaTarget = new Selector("[+] FindArea");
        //findBombAreaTarget.AttachChild(new HasTargetPoint());
        findBombAreaTarget.AttachChild(new BasicFindBombArea(fireThreshold, boardState, boardState.GetPlayers().get(id)));

        Node root = new Sequencer("[>] AttackA");
        root.AttachChild(findBombAreaTarget);
        root.AttachChild(new MoveToTarget(new PlantBomb()));
        return root;
    }

}
