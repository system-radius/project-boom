package com.radius.system.configs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.board.BoardState;
import com.radius.system.enums.BoardRep;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.blocks.HardBlock;
import com.radius.system.objects.blocks.SoftBlock;

import java.util.Random;

import javax.annotation.processing.SupportedSourceVersion;
import javax.imageio.ImageIO;

public class FieldConfig {

    private static final Texture burnTest = GlobalAssets.LoadTexture(GlobalAssets.BURN_TEST_PATH);

    private int width = 31, height = 17;

    public FieldConfig() {
    }

    public void LoadField(BoardState boardState, float scale) {
        if (!burnTest.getTextureData().isPrepared()) {
            burnTest.getTextureData().prepare();
        }

        width = burnTest.getWidth();
        height = burnTest.getHeight();

        boardState.Resize(width, height);

        Pixmap pixmap = burnTest.getTextureData().consumePixmap();
        int fieldIndex = new Random(System.currentTimeMillis()).nextInt(7);
        char[][] field = new char[burnTest.getWidth()][burnTest.getHeight()];
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < burnTest.getHeight(); y++) {
            for (int x = 0; x < burnTest.getWidth(); x++) {
                Color color = new Color(pixmap.getPixel(x, y));
                sb.append("[");
                if (Color.RED.equals(color)) {
                    boardState.AddToBoard(new HardBlock(fieldIndex, x, y, scale, scale));
                    sb.append(BoardRep.HARD_BLOCK);
                } else if (Color.WHITE.equals(color)) {
                    boardState.AddToBoard(new Block(fieldIndex, x, y, scale, scale));
                    sb.append(BoardRep.PERMANENT_BLOCK);
                } else if (Color.YELLOW.equals(color)) {
                    boardState.AddToBoard(new SoftBlock(fieldIndex, x, y, scale, scale));
                    sb.append(BoardRep.SOFT_BLOCK);
                } else if (Color.BLUE.equals(color)) {
                    sb.append(BoardRep.EMPTY);
                } else {
                    System.out.println(color);
                }
                sb.append("] ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }

}
