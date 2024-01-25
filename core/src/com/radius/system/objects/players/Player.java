package com.radius.system.objects.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.objects.BoomGameObject;

public class Player extends BoomGameObject {

    private static final float FRAME_DURATION_MOVING = 1f / 15f;

    private static final float FRAME_DURATION_DYING = 1f / 4f;

    /**
     * The max speed + two. This is mainly used for computations that rely on the remaining speed
     * levels that are not acquired yet. Leaving with 2 on full speed.
     */
    public static final float SPEED_COUNTER = 5f;

    /**
     * The timer until the player respawns after dying.
     */
    private static final float DEATH_TIMER = 3f;

    /**
     * The amount of time to watch the player die.
     */
    private static final float DYING_TIMER = 2f;

    /**
     * The top collision bound.
     */
    private Rectangle northRect;

    /**
     * The bottom collision bound.
     */
    private Rectangle southRect;

    /**
     * The left collision bound.
     */
    private Rectangle westRect;

    /**
     * The right collision bound.
     */
    private Rectangle eastRect;

    /**
     * The animation frames when going south or pressing "S" key.
     */
    private Animation<TextureRegion> sAnim;

    /**
     * The animation when going north or pressing "W" key.
     */
    private Animation<TextureRegion> wAnim;

    /**
     * The animation when going west or pressing "A" key.
     */
    private Animation<TextureRegion> aAnim;

    /**
     * The animation when going east or pressing "D" key.
     */
    private Animation<TextureRegion> dAnim;

    /**
     * The animation for when the player is dying.
     */
    private Animation<TextureRegion> deathAnim;

    /**
     * The loaded sprite sheet for this player.
     */
    private Texture spriteSheet;

    /**
     * The current scale value, provided on the creation of this object, relevant for the creation
     * of the collision bounds.
     */
    protected float scale;

    public Player(float x, float y, float scale) {
        super(' ', x, y);

        this.scale = scale;
        LoadAsset("img/tokoy_sprite_sheet.png");
    }

    private void LoadAsset(String spriteSheetPath) {
        spriteSheet = new Texture(Gdx.files.internal(spriteSheetPath));
        TextureRegion[][] allFrames = TextureRegion.split(spriteSheet, 32, 32);

        sAnim = CreateAnimation(allFrames, 0, FRAME_DURATION_MOVING, true);
        wAnim = CreateAnimation(allFrames, 1, FRAME_DURATION_MOVING, true);
        aAnim = CreateAnimation(allFrames, 2, FRAME_DURATION_MOVING, true);
        dAnim = CreateAnimation(allFrames, 3, FRAME_DURATION_MOVING, true);
        deathAnim = CreateAnimation(allFrames, 4, FRAME_DURATION_DYING, false);
    }

    private Animation<TextureRegion> CreateAnimation(TextureRegion[][] allFrames, int direction, float frameDuration, boolean enableLoop) {
        TextureRegion[] frames = LoadFrames(allFrames, direction);
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);

        if (enableLoop) {
            animation.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animation;
    }

    private TextureRegion[] LoadFrames(TextureRegion[][] allFrames, int direction) {
        TextureRegion[] temp = allFrames[direction];
        TextureRegion[] container = new TextureRegion[temp.length];

        System.arraycopy(temp, 0, container, 0, temp.length);

        return container;
    }

    @Override
    public void dispose() {
        spriteSheet.dispose();
    }

    @Override
    public void Burn() {

    }

    @Override
    public void Update(float delta) {
        animationElapsedTime += delta;
    }

    @Override
    public void Draw(SpriteBatch batch) {
        batch.draw(sAnim.getKeyFrame(animationElapsedTime), x * scale, y * scale, scale, scale);
    }
}
