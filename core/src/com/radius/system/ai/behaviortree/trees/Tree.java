package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.states.BoardState;
import com.radius.system.objects.BoomUpdatable;

public abstract class Tree implements BoomUpdatable {

    protected final int id, fireThreshold;

    protected final BoardState boardState;

    private final int[][] boardCost;

    private Node root;

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

    protected abstract Node SetupTree();

}
