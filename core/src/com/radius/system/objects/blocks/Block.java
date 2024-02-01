package com.radius.system.objects.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.objects.BoomGameObject;

public class Block extends BoomGameObject {

    public static final Texture BLOCKS_SPRITE_SHEET = new Texture(Gdx.files.internal("img/blocks.png"));

    protected static final float DESTROY_TIMER = 1f;

    private final float width;

    private final float height;

    protected float burnTimer;

    protected boolean burning;

    protected boolean activeCollision = true;

    protected boolean hasBonus;

    protected boolean destroyed = false;

    protected Animation<TextureRegion> animation;

    protected final Rectangle bounds;

    protected static final TextureRegion[][] REGIONS =
            TextureRegion.split(BLOCKS_SPRITE_SHEET, 32, 32);

    public Block(float x, float y, float width, float height) {
        super('#', x, y);

        this.width = width;
        this.height = height;

        this.bounds = new Rectangle(this.x, this.y, 1, 1);
        Initialize();
    }

    public float GetWidth() {
        return width;
    }

    public float GetHeight() {
        return height;
    }

    public Rectangle GetBounds() {
        return bounds;
    }

    protected void Initialize() {
        TextureRegion[] frames = new TextureRegion[1];
        frames[0] = REGIONS[0][6];

        animation = new Animation<>(0, frames);
    }

    @Override
    public void dispose() {
        // Nothing to do here as the sprite sheet is to be disposed outside the class.
    }

    @Override
    public void Burn() {
        // Do nothing.
    }

    @Override
    public void Update(float delta) {

        if (!burning || destroyed) {
            return;
        }

        burnTimer += delta;
        animationElapsedTime += delta;
        if (burnTimer >= DESTROY_TIMER) {
            // Mark this object for removal.
            this.destroyed = true;
        }
    }

    @Override
    public void Draw(Batch batch) {
        if (!burning) {
            batch.draw(animation.getKeyFrames()[0], x * width, y * height, width, height);
        }
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        renderer.setColor(Color.BLUE);
        renderer.rect(x * width, y * height, width, height);
    }
}
