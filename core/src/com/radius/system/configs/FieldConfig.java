package com.radius.system.configs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.enums.BoardRep;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.blocks.HardBlock;
import com.radius.system.objects.blocks.SoftBlock;
import com.radius.system.utils.FontUtils;

import java.util.Random;

public class FieldConfig {

    private static final Random RANDOMIZER = new Random(System.currentTimeMillis());

    private final float scale;

    private final BitmapFont font;

    private final Color player1 = new Color(0xf0f000ff), player2 = new Color(0xf1f100ff);

    private final Color player3 = new Color(0xf2f200ff), player4 = new Color(0xf3f300ff);

    private int width = 31, height = 17, fieldIndex;

    private BoardRep[][] boardRep = new BoardRep[width][height];

    public FieldConfig() {
        scale = GlobalConstants.WORLD_SCALE;
        font = FontUtils.GetFont((int) GlobalConstants.WORLD_SCALE / 6, Color.WHITE, 1, Color.BLACK);
    }

    public void LoadField(String texturePath) {
        Texture fieldTexture = GlobalAssets.LoadTexture(texturePath);
        if (!fieldTexture.getTextureData().isPrepared()) {
            fieldTexture.getTextureData().prepare();
        }

        int width = fieldTexture.getWidth();
        int height = fieldTexture.getHeight();

        if (this.width != width || this.height != height) {
            boardRep = new BoardRep[width][height];
            this.width = width;
            this.height = height;
            GlobalConstants.WORLD_AREA = width * height;
        }

        Pixmap pixmap = fieldTexture.getTextureData().consumePixmap();
        StringBuilder sb = new StringBuilder();
        for (int pixelY = height - 1; pixelY >= 0; pixelY--) {
            for (int x = 0; x < fieldTexture.getWidth(); x++) {
                Color color = new Color(pixmap.getPixel(x, pixelY));
                sb.append("[");
                int y = height - pixelY - 1;
                if (Color.RED.equals(color)) {
                    boardRep[x][y] = BoardRep.HARD_BLOCK;
                    sb.append(BoardRep.HARD_BLOCK);
                } else if (Color.WHITE.equals(color)) {
                    boardRep[x][y] = BoardRep.PERMANENT_BLOCK;
                    sb.append(BoardRep.PERMANENT_BLOCK);
                } else if (Color.GREEN.equals(color)) {
                    boardRep[x][y] = BoardRep.SOFT_BLOCK;
                    sb.append(BoardRep.SOFT_BLOCK);
                } else if (Color.BLACK.equals(color)) {
                    boardRep[x][y] = BoardRep.EMPTY;
                    sb.append(BoardRep.EMPTY);
                } else if (Color.YELLOW.equals(color)) {
                    boardRep[x][y] = BoardRep.PLAYER;
                    sb.append(BoardRep.PLAYER);
                } else {
                    sb.append(SetPlayerSpawnPoint(x, y, color));
                }
                sb.append("] ");
            }
            sb.append("\n");
        }

        if (GlobalConstants.DEBUG) {
            //System.out.println(sb);
        }
    }

    private char SetPlayerSpawnPoint(int x, int y, Color color) {
        BoardRep rep = BoardRep.EMPTY;
        if (player1.equals(color)) {
            rep = BoardRep.PLAYER_1;
        } else if (player2.equals(color)) {
            rep = BoardRep.PLAYER_2;
        } else if (player3.equals(color)) {
            rep = BoardRep.PLAYER_3;
        } else if (player4.equals(color)) {
            rep = BoardRep.PLAYER_4;
        }

        boardRep[x][y] = rep;
        return rep.GetRep();
    }

    public BoardState CreateBoardState() {
        BoardState boardState = new BoardState(width, height, scale, font);
        RestartBoard(boardState);

        return boardState;
    }

    public void RestartBoard(BoardState boardState) {
        fieldIndex = RANDOMIZER.nextInt(7);

        boolean randomize = true;
        int spawnPoints = 0;
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                switch (boardRep[x][y]) {
                    case PERMANENT_BLOCK:
                        boardState.AddToBoard(new Block(fieldIndex, x, y, scale, scale));
                        break;
                    case HARD_BLOCK:
                        boardState.AddToBoard(new HardBlock(fieldIndex, x, y, scale, scale));
                        break;
                    case SOFT_BLOCK:
                        randomize = false;
                        boardState.AddToBoard(new SoftBlock(fieldIndex, x, y, scale, scale));
                        break;
                    case PLAYER:
                        boardState.AddToBoard(spawnPoints, new Vector2(x, y));
                        break;
                    case PLAYER_1:
                        boardState.AddToBoard(0, new Vector2(x, y));
                        break;
                    case PLAYER_2:
                        boardState.AddToBoard(1, new Vector2(x, y));
                        break;
                    case PLAYER_3:
                        boardState.AddToBoard(2, new Vector2(x, y));
                        break;
                    case PLAYER_4:
                        boardState.AddToBoard(3, new Vector2(x, y));
                        break;
                    default:
                }
            }
        }

        if (randomize) {
            RandomizeField(boardState);
            for (int i = 0; i < 4; i++) {
                Vector2 point = boardState.GetSpawnPoint(i);

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        int xPos = (int)point.x + x;
                        int yPos = (int)point.y + y;

                        if (xPos < 0 || yPos < 0 || xPos >= width || yPos >= height || !BoardRep.SOFT_BLOCK.equals(boardState.GetBoardEntry(xPos, yPos))) {
                            continue;
                        }

                        boardState.RemoveFromBoard((Block)boardState.GetBoardObject(xPos, yPos));
                    }
                }

            }
        }
    }

    private void RandomizeField(BoardState boardState) {
        int totalArea = GlobalConstants.WORLD_AREA;
        int boundingBlocks = width * 2 + height * 2;
        int placeableBlocks = totalArea - boundingBlocks;

        for (int i = 0; i < placeableBlocks; i++) {
            int x = RANDOMIZER.nextInt(width);
            int y = RANDOMIZER.nextInt(height);

            if (!BoardRep.EMPTY.equals(boardRep[x][y])) {
                continue;
            }

            boardState.AddToBoard(new SoftBlock(fieldIndex, x, y, scale, scale));
        }
    }

    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }

}
