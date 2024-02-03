package com.radius.system.board;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.enums.BoardRep;
import com.radius.system.objects.bombs.Bomb;
import com.radius.system.objects.BoomGameObject;
import com.radius.system.objects.GameObject;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;

import java.util.ArrayList;
import java.util.List;

public class BoardState extends GameObject {

    private final BoomGameObject[][] board;

    private final BoardRep[][] boardRep;

    private final int boardWidth;

    private final int boardHeight;

    private final List<Player> players;

    private final List<Bomb> bombs;

    public BoardState(int boardWidth, int boardHeight) {
        super(0, 0);

        this.board = new BoomGameObject[boardWidth][boardHeight];
        this.boardRep = new BoardRep[boardWidth][boardHeight];
        this.players = new ArrayList<>();
        this.bombs = new ArrayList<>();

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        ClearBoard();
    }

    public void ClearBoard() {
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                boardRep[x][y] = BoardRep.EMPTY;
                board[x][y] = null;
            }
        }
    }

    public void AddToBoard(Block block) {
        int x = (int) block.GetX();
        int y = (int) block.GetY();

        board[x][y] = block;
        boardRep[x][y] = block.GetRep();
    }

    public void AddBombToBoard(Bomb bomb) {
        if (bomb == null) {
            return;
        }

        AddToBoard(bomb);
        bombs.add(bomb);

        bomb.AddPlayerCollision(players);
    }

    public void AddToBoard(Player player) {
        players.add(player);
    }

    public void RemoveFromBoard(Block block) {
        int x = (int) block.GetX();
        int y = (int) block.GetY();

        board[x][y] = null;
        boardRep[x][y] = BoardRep.EMPTY;
    }

    public void RemoveBombFromBoard(Bomb bomb) {
        RemoveFromBoard(bomb);
        bombs.remove(bomb);
    }

    public BoardRep GetBoardEntry(int x, int y) {
        return boardRep[x][y];
    }

    public BoomGameObject GetBoardObject(int x, int y) {
        return board[x][y];
    }

    public List<Block> GetSurroundingBlocks(int x, int y) {
        List<Block> blocks = new ArrayList<>();

        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i >= boardWidth) continue;
            for (int j = y - 1; j <= y + 1; j++) {
                if (j < 0 || j >= boardHeight) continue;
                if (board[i][j] instanceof Block) {
                    blocks.add((Block)board[i][j]);
                }
            }
        }

        return blocks;
    }

    public void AttemptBurnPlayers(Bomb bomb) {
        for (Player player : players) {
            Rectangle playerRect = player.GetCollisionRect();

            if (bomb.HasContact(playerRect)) {
                player.Burn();
            }
        }
    }

    @Override
    public void Update(float delta) {
        UpdateBombs();
        UpdateBoard(delta);
    }

    private void UpdateBombs() {
        for (int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            if (bomb.IsWaiting()) {
                bomb.UpdateBounds(this);
            } else if (bomb.IsExploding()) {
                AttemptBurnPlayers(bomb);
                bomb.BurnObjects(this);
            } else if (bomb.IsExploded()) {
                RemoveBombFromBoard(bomb);
            }
        }
    }

    private void UpdateBoard(float delta) {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (board[i][j] != null) {
                    board[i][j].Update(delta);
                }
            }
        }
    }

    @Override
    public void Draw(Batch batch) {

        for (int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            if (bomb.IsExploded()) {
                continue;
            }

            bomb.Draw(batch);
        }

        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (board[i][j] != null && boardRep[i][j] != BoardRep.BOMB) {
                    board[i][j].Draw(batch);
                }
            }
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
