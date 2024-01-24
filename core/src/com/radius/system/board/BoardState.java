package com.radius.system.board;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.radius.system.objects.BoomGameObject;
import com.radius.system.objects.GameObject;
import com.radius.system.objects.blocks.Block;

public class BoardState extends GameObject {

    private final BoomGameObject[][] board;

    private final char[][] boardRep;

    private final int boardWidth;

    private final int boardHeight;

    public BoardState(int boardWidth, int boardHeight) {
        super(0, 0);

        this.board = new BoomGameObject[boardWidth][boardHeight];
        this.boardRep = new char[boardWidth][boardHeight];

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    public void AddToBoard(Block block) {
        int x = (int) block.GetX();
        int y = (int) block.GetY();

        board[x][y] = block;
        boardRep[x][y] = block.GetCharRep();
    }

    @Override
    public void Update(float delta) {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (board[i][j] != null) {
                    board[i][j].Update(delta);
                }
            }
        }
    }

    @Override
    public void Draw(SpriteBatch batch) {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (board[i][j] != null) {
                    board[i][j].Draw(batch);
                }
            }
        }
    }
}
