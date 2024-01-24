package com.radius.system.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends AbstractScreen {

    private final float WORLD_WIDTH = 17f;

    private final float WORLD_HEIGHT = 17f;

    private final float WORLD_SCALE = 20f;

    public GameScreen() {
        InitializeField();
    }

    public void InitializeField() {

        float spacing = 2f; // Allows for leaving spaces when generating hard blocks.

        for(int x = 0; x < WORLD_WIDTH; x++) {
            for(int y = 0; y < WORLD_HEIGHT; y++) {
                if (x == 0 || y == 0 || x + 1 == WORLD_WIDTH || y + 1 == WORLD_HEIGHT) {
                    // Create permanent blocks.
                } else if (x % spacing == 0 && y % spacing == 0) {
                    // Create hard blocks.
                }
            }
        }
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(SpriteBatch spriteBatch) {

    }
}
