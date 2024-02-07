package com.radius.system.objects.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.enums.BoardRep;
import com.radius.system.objects.AnimatedGameObject;
import com.radius.system.objects.Entity;
import com.radius.system.objects.players.Player;

import java.util.Random;

public class Block extends Entity {

    public static final Texture BLOCKS_SPRITE_SHEET = new Texture(Gdx.files.internal("img/blocks.png"));

    protected static final Random randomizer = new Random(System.currentTimeMillis());

    protected static final float DESTROY_TIMER = 1f;

    protected float burnTimer;

    protected boolean burning;

    protected boolean activeCollision = true;

    protected boolean hasBonus = false;

    protected boolean destroyed = false;

    protected Rectangle bounds;

    protected static final TextureRegion[][] REGIONS =
            TextureRegion.split(BLOCKS_SPRITE_SHEET, 32, 32);

    public Block(int fieldIndex, float x, float y, float width, float height) {
        this(BoardRep.PERMANENT_BLOCK, fieldIndex, x, y, width, height);
    }

    public Block(BoardRep rep, int fieldIndex, float x, float y, float width, float height) {
        super(rep, x, y, width, height);

        this.bounds = new Rectangle(position.x, position.y, 1, 1);
        Initialize(fieldIndex);
    }

    public int GetLife() {
        return life;
    }

    public Rectangle GetBounds() {
        return bounds;
    }

    protected void Initialize(int fieldIndex) {
        if (fieldIndex < 0) {
            return;
        }

        TextureRegion[] frames = new TextureRegion[1];
        frames[0] = REGIONS[fieldIndex][6];

        activeAnimation = new Animation<>(0, frames);
    }

    public boolean HasActiveCollision(Player player) {
        return true;
    }

    public boolean IsDestroyed() {
        return destroyed;
    }

    public boolean HasBonus() {
        return hasBonus;
    }

    protected void Destroy() {
        // Mark this object for removal.
        this.destroyed = true;
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
            Destroy();
        }
    }

    @Override
    public void Draw(Batch batch) {
        if (!burning) {
            batch.draw(activeAnimation.getKeyFrames()[0], scaledPosition.x, scaledPosition.y, size.x, size.y);
            return;
        }

        super.Draw(batch);
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        renderer.setColor(Color.BLUE);
        renderer.rect(scaledPosition.x, scaledPosition.y, size.x, size.y);
    }
}
