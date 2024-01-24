package com.radius.system.objects.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.radius.system.objects.BoomGameObject;

public class Block extends BoomGameObject {

    private static final Texture BLOCKS_SPRITE_SHEET = new Texture();

    public Block(float x, float y, float width, float height) {
        super('#', x, y);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void Burn() {

    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(SpriteBatch batch) {

    }
}
