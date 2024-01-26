package com.radius.system.board;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.radius.system.objects.BoomGameObject;
import com.radius.system.objects.GameObject;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class BoardState extends GameObject {

    private final BoomGameObject[][] board;

    private final char[][] boardRep;

    private final int boardWidth;

    private final int boardHeight;

    private final List<Player> players;

    public BoardState(int boardWidth, int boardHeight) {
        super(0, 0);

        this.board = new BoomGameObject[boardWidth][boardHeight];
        this.boardRep = new char[boardWidth][boardHeight];
        this.players = new ArrayList<>();

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    public void AddToBoard(Block block) {
        int x = (int) block.GetX();
        int y = (int) block.GetY();

        board[x][y] = block;
        boardRep[x][y] = block.GetCharRep();
    }

    public void AddToBoard(Player player) {
        players.add(player);
    }

    public List<Block> GetSurroundingBlocks(int x, int y) {
        List<Block> blocks = new ArrayList<>();

        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i > boardWidth) continue;
            for (int j = y - 1; j <= y + 1; j++) {
                if (j < 0 || j > boardHeight) continue;
                if (board[i][j] instanceof Block) {
                    blocks.add((Block)board[i][j]);
                }
            }
        }

        return blocks;
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

        for (Player player : players) {
            player.Update(delta);
            player.Collide(GetSurroundingBlocks(player.GetWorldX(), player.GetWorldY()));
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

        for (Player player : players) {
            player.Draw(batch);
        }
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (board[i][j] != null) {
                    board[i][j].DrawDebug(renderer);
                }
            }
        }

        for (Player player : players) {
            player.DrawDebug(renderer);
        }
    }
}
