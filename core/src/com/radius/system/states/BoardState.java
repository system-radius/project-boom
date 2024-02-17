package com.radius.system.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.Direction;
import com.radius.system.objects.BoomDrawable;
import com.radius.system.objects.BoomUpdatable;
import com.radius.system.objects.bombs.Bomb;
import com.radius.system.objects.AnimatedGameObject;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.blocks.Bonus;
import com.radius.system.objects.players.Player;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardState implements BoomUpdatable, BoomDrawable {

    public final int BOARD_WIDTH;

    public final int BOARD_HEIGHT;

    private final AnimatedGameObject[][] board;

    private final BoardRep[][] boardRep;

    private final int[][] boardCost;

    private final int scale;

    private final List<Player> players;

    private final List<Bomb> bombs;

    private final BitmapFont font;

    public BoardState(int boardWidth, int boardHeight, int scale) {

        this.board = new AnimatedGameObject[boardWidth][boardHeight];
        this.boardRep = new BoardRep[boardWidth][boardHeight];
        this.boardCost = new int[boardWidth][boardHeight];
        this.players = new ArrayList<>();
        this.bombs = new ArrayList<>();

        this.BOARD_WIDTH = boardWidth;
        this.BOARD_HEIGHT = boardHeight;
        this.scale = scale;

        font = FontUtils.GetFont((int) GlobalConstants.WORLD_SCALE / 6, Color.WHITE, 1, Color.BLACK);

        ClearBoard();
    }

    public void ClearBoard() {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                boardRep[x][y] = BoardRep.EMPTY;
                board[x][y] = null;
            }
        }
        bombs.clear();
    }

    public void AddToBoard(Block block) {
        int x = block.GetWorldX();
        int y = block.GetWorldY();

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

    public List<Player> GetPlayers() {
        return players;
    }

    public void MoveItemInBoard(int srcX, int srcY, int dstX, int dstY) {

        board[dstX][dstY] = board[srcX][srcY];
        board[srcX][srcY] = null;

        boardRep[dstX][dstY] = boardRep[srcX][srcY];
        boardRep[srcX][srcY] = BoardRep.EMPTY;
    }

    public void RemoveFromBoard(Block block) {
        int x = block.GetWorldX();
        int y = block.GetWorldY();

        if (block.HasBonus()) {
            board[x][y] = new Bonus(x, y, scale, scale);
            boardRep[x][y] = BoardRep.BONUS;
        } else {
            board[x][y] = null;
            boardRep[x][y] = BoardRep.EMPTY;
        }
    }

    public void RemoveBombFromBoard(Bomb bomb) {
        RemoveFromBoard(bomb);
        bombs.remove(bomb);
    }

    public BoardRep GetBoardEntry(int x, int y) {

        if (x < 0 || y < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT) {
            return null;
        }

        return boardRep[x][y];
    }

    public AnimatedGameObject GetBoardObject(int x, int y) {
        return board[x][y];
    }

    public List<Block> GetSurroundingBlocks(int x, int y) {
        List<Block> blocks = new ArrayList<>();

        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i >= BOARD_WIDTH) continue;
            for (int j = y - 1; j <= y + 1; j++) {
                if (j < 0 || j >= BOARD_HEIGHT) continue;
                if (board[i][j] instanceof Block) {
                    blocks.add((Block)board[i][j]);
                }
            }
        }

        return blocks;
    }

    public void UpdateBoardCost() {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                switch(boardRep[i][j]) {
                    case HARD_BLOCK:
                    case SOFT_BLOCK:
                    case PERMANENT_BLOCK:
                        boardCost[i][j] = -1;
                        break;
                    default:
                        boardCost[i][j] = 1;
                }
            }
        }

        for (Bomb bomb : bombs) {

            int x = bomb.GetWorldX(), y = bomb.GetWorldY();
            Map<Direction, Integer> fireRanges = bomb.GetRangeValues();

            int cost = bomb.GetCost();

            SetCostInRange(boardCost, x, y, fireRanges.get(Direction.NORTH), 1, cost, Direction.NORTH);
            SetCostInRange(boardCost, x, y, fireRanges.get(Direction.SOUTH), 1, cost, Direction.SOUTH);
            SetCostInRange(boardCost, x, y, fireRanges.get(Direction.WEST), 1, cost, Direction.WEST);
            SetCostInRange(boardCost, x, y, fireRanges.get(Direction.EAST), 1, cost, Direction.EAST);
        }
    }

    public void CompileBoardCost(int[][] boardCost, int fireThreshold, int id) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            /*
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                boardCost[i][j] = this.boardCost[i][j] > fireThreshold ? -1 : this.boardCost[i][j];
            }

             */
            System.arraycopy(this.boardCost[i], 0, boardCost[i], 0, BOARD_HEIGHT);
        }

        for (Bomb bomb : bombs) {
            int x = bomb.GetWorldX(), y = bomb.GetWorldY();
            boardCost[x][y] = bomb.HasActiveCollision(players.get(id)) ? -1 : this.boardCost[x][y];
        }
    }

    private void SetCostInRange(int[][] boardCost, int x, int y, int range, int counter, int cost, Direction direction) {
        if (counter > range || x < 0 || y < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT) {
            return;
        }

        if (boardCost[x][y] > 0 ) {
            boardCost[x][y] = cost;
        }
        switch (direction) {
            case NORTH:
                SetCostInRange(boardCost, x, y + 1, range, counter + 1, cost, direction);
                break;
            case SOUTH:
                SetCostInRange(boardCost, x, y - 1, range, counter + 1, cost, direction);
                break;
            case WEST:
                SetCostInRange(boardCost, x - 1, y, range, counter + 1, cost, direction);
                break;
            case EAST:
                SetCostInRange(boardCost, x + 1, y, range, counter + 1, cost, direction);
                break;
        }
    }

    public void AttemptBurnPlayers(Bomb bomb) {
        for (Player player : players) {
            Rectangle playerRect = player.GetBurnRect();

            if (bomb.HasContact(playerRect)) {
                player.Burn();
            }
        }
    }

    @Override
    public void Update(float delta) {
        UpdateBombs(delta);
        UpdateBoard(delta);
        UpdateBoardCost();
    }

    private void UpdateBombs(float delta) {
        for (int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            int srcX = bomb.GetPastX();
            int srcY = bomb.GetPastY();

            switch(bomb.GetState()) {
                case MOVING:
                    boolean hasCollision = bomb.Collide(GetSurroundingBlocks(srcX, srcY));
                    if (bomb.UpdatePosition(delta)) {
                        int dstX = bomb.GetWorldX();
                        int dstY = bomb.GetWorldY();

                        MoveItemInBoard(srcX, srcY, dstX, dstY);
                    }
                    if (hasCollision) {
                        bomb.velocity.x = 0;
                        bomb.velocity.y = 0;
                        bomb.Explode();
                    }
                case BREATHING:
                case SET_TO_EXPLODE:
                    //System.out.println("(" + bomb.velocity.x + ", " + bomb.velocity.y + ")");
                    bomb.UpdateCollisionBounds();
                    bomb.UpdateBounds(this);
                    break;
                case EXPLODING:
                    AttemptBurnPlayers(bomb);
                    bomb.BurnObjects(this);
                    break;
                case EXPLODED:
                    RemoveBombFromBoard(bomb);
            }
        }
    }

    private void UpdateBoard(float delta) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                if (board[i][j] != null) {
                    board[i][j].Update(delta);

                    if (board[i][j] instanceof Block) {
                        Block block = (Block) board[i][j];
                        if (block.IsDestroyed()) {
                            RemoveFromBoard(block);
                        }
                    }
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

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                if (board[i][j] != null && boardRep[i][j] != BoardRep.BOMB) {
                    board[i][j].Draw(batch);
                }
            }
        }

        if (GlobalConstants.DEBUG) {
            for (int i = 0; i < BOARD_WIDTH; i++) {
                for (int j = 0; j < BOARD_HEIGHT; j++) {
                    font.draw(batch, "(" + i + ", " + j + ")", i * GlobalConstants.WORLD_SCALE, j * GlobalConstants.WORLD_SCALE + GlobalConstants.WORLD_SCALE);
                    font.draw(batch, "[" + boardCost[i][j] + "]", i * GlobalConstants.WORLD_SCALE, j * GlobalConstants.WORLD_SCALE + GlobalConstants.WORLD_SCALE - 20);
                }
            }
        }
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
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