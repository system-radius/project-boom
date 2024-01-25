package com.radius.system.objects.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.enums.Direction;
import com.radius.system.enums.PlayerState;
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
     * The base speed that will be multiplied with the current speed level to gat the actual speed.
     * This value is separate from the velocity values because the velocities are reset every
     * update. When moving the character, the speed is set as either the velocity X or velocity Y.
     */
    protected float baseSpeed = 10;

    /**
     * The player's current speed level. To avoid having the player jump over walls dues to too
     * much computation using the velocity values, the maximum speed level is up to 5.
     */
    protected float speedLevel = 1f;

    /**
     * The current scale value, provided on the creation of this object, relevant for the creation
     * of the collision bounds.
     */
    protected float scale;

    /**
     * The player's current direction.
     */
    protected Direction direction = Direction.SOUTH;

    /**
     * The player's current state.
     */
    protected PlayerState state = PlayerState.IDLE;

    private float thinWidth;

    private float thinHeight;

    public Player(float x, float y, float scale) {
        super(' ', x, y);

        this.scale = scale;
        LoadAsset("img/tokoy_sprite_sheet.png");
        FixBounds();
    }

    private void FixBounds() {
        float width = scale;
        float height = scale;

        float divider = SPEED_COUNTER - speedLevel;
        thinWidth = (width / (divider * 2));
        thinHeight = (height / (divider * 2));

        northRect = new Rectangle(x + thinWidth, (y + height) - thinHeight,
                width - (thinWidth * 2), thinHeight);
        southRect = new Rectangle(x + thinWidth, y, width - (thinWidth * 2), thinHeight);
        westRect = new Rectangle(x, y + thinHeight, thinWidth, height - (thinHeight * 2));
        eastRect = new Rectangle(x + width - thinWidth, y + thinHeight, thinWidth,
                height - (thinHeight * 2));
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

    private Animation<TextureRegion> GetActiveMovingAnimation() {
        switch (direction) {
            case NORTH:
                return wAnim;
            case WEST:
                return aAnim;
            case EAST:
                return dAnim;
            case SOUTH:
            default:
                return sAnim;
        }
    }

    private Animation<TextureRegion> GetActiveAnimation() {
        switch(state) {
            case DYING:
                return deathAnim;
            case MOVING:
            default:
                return GetActiveMovingAnimation();
        }
    }

    private TextureRegion GetActiveKeyFrame() {
        switch(state) {
            case DEAD:
                return deathAnim.getKeyFrames()[3];
            case IDLE:
            default:
                return GetActiveMovingAnimation().getKeyFrames()[0];
        }
    }

    public void SetVelX(float multiplier) {
        this.velX = (baseSpeed * speedLevel) * multiplier;
    }

    public void SetVelY(float multiplier) {
        this.velY = (baseSpeed * speedLevel) * multiplier;
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

        this.x += velX * delta;
        this.y += velY * delta;
    }

    @Override
    public void Draw(SpriteBatch batch) {

        if (state == PlayerState.MOVING || state == PlayerState.DYING) {
            batch.draw(GetActiveAnimation().getKeyFrame(animationElapsedTime), x * scale, y * scale, scale, scale);
        } else {
            batch.draw(GetActiveKeyFrame(), x * scale, y * scale, scale, scale);
        }
    }
}
