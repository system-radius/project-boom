package com.radius.system.objects.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.board.BoardState;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombType;
import com.radius.system.enums.Direction;
import com.radius.system.enums.PlayerState;
import com.radius.system.events.MovementEventListener;
import com.radius.system.objects.bombs.Bomb;
import com.radius.system.objects.BoomGameObject;
import com.radius.system.objects.blocks.Block;
import com.radius.system.utils.SegmentIntersector;

import java.util.ArrayList;
import java.util.List;

public class Player extends BoomGameObject {

    private static final float FRAME_DURATION_MOVING = 1f / 10f;

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

    private final List<MovementEventListener> coordEventListeners;

    private final List<Bomb> bombs;

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
     * The rectangle representing the collision bounds of the player.
     */
    protected Rectangle collisionRect;

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
    protected float baseSpeed = 5f;

    /**
     * The player's current speed level. To avoid having the player jump over walls dues to too
     * much computation using the velocity values, the maximum speed level is up to 5.
     */
    protected float speedLevel = 4f;

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

    private float pastX;

    private float pastY;

    private int bombStock = 1;

    private int firePower = 3;

    private final int id;

    public Player(int id, float x, float y, float scale) {
        super(BoardRep.PLAYER, x, y);

        this.id = id;

        this.scale = scale;
        LoadAsset("img/tokoy_sprite_sheet.png");
        FixBounds();

        coordEventListeners = new ArrayList<>();
        bombs = new ArrayList<>();
    }

    private void FixBounds() {
        float width = 1f;
        float height = 1f;

        float divider = 1.1f;
        thinWidth = (width / (divider * 2));
        thinHeight = (height / (divider * 2));

        collisionRect = RefreshRectangle(collisionRect, x, y, width - thinWidth, height - thinHeight);

        northRect = RefreshRectangle(northRect, x, y, width - (thinWidth * 2), thinHeight);
        southRect = RefreshRectangle(southRect, x, y, width - (thinWidth * 2), thinHeight);
        westRect = RefreshRectangle(westRect, x, y, thinWidth, height - (thinHeight * 2));
        eastRect = RefreshRectangle(eastRect, x, y, thinWidth, height - (thinHeight * 2));
    }

    private Rectangle RefreshRectangle (Rectangle rectangle, float x, float y, float width, float height) {
        if (rectangle == null) {
            return new Rectangle(x, y, width, height);
        }

        return rectangle.set(x, y, width, height);
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

    private void UpdateDirection() {

        Direction horizontalDirection = null;
        Direction verticalDirection = null;
        if (velX > 0) {
            horizontalDirection = Direction.EAST;
        } else if (velX < 0) {
            horizontalDirection = Direction.WEST;
        }

        if (velY > 0) {
            verticalDirection = Direction.NORTH;
        } else if (velY < 0) {
            verticalDirection = Direction.SOUTH;
        }

        if (horizontalDirection != null && verticalDirection != null) {
            direction = Math.abs(velX) > Math.abs(velY) ? horizontalDirection : verticalDirection;
        } else if (horizontalDirection != null || verticalDirection != null) {
            direction = horizontalDirection == null ? verticalDirection : horizontalDirection;
        }
    }

    private void UpdateState() {
        if (velX != 0 || velY != 0) {
            state = PlayerState.MOVING;
        } else {
            state = PlayerState.IDLE;
        }
    }

    private void UpdateBounds() {

        float offset = -speedLevel;
        northRect.setPosition(x + (thinWidth), (y + 1) - (thinHeight) - (offset / scale));
        southRect.setPosition(x + (thinWidth), y + (offset / scale));
        eastRect.setPosition(x + (1 - (thinWidth)) - (offset / scale), y + (thinHeight));
        westRect.setPosition(x + (offset / scale), y + (thinHeight));

        collisionRect.setPosition(x + thinWidth / 2, y + thinHeight / 2);
    }

    public void SetVelX(float multiplier) {
        this.velX = (baseSpeed * speedLevel) * multiplier;
    }

    public void SetVelY(float multiplier) {
        this.velY = (baseSpeed * speedLevel) * multiplier;
    }

    private int GetWorldPosition(float c, float scale) {
        float excess = (c * scale) % scale >= scale / 2 ? 1 : 0;

        return (int)(c + excess);
    }

    public int GetWorldX() {
        return GetWorldPosition(x, scale);
    }

    public int GetWorldY() {
        return GetWorldPosition(y, scale);
    }

    public int GetFirePower() {
        return firePower;
    }

    public int GetBombStock() {
        return bombStock - bombs.size();
    }

    public Rectangle GetCollisionRect() {
        return collisionRect;
    }

    public void Collide(List<Block> blocks) {
        for (Block block : blocks) {

            if (!block.HasActiveCollision()) {
                continue;
            }

            Rectangle blockBounds = block.GetBounds();
            float blockX = block.GetX();
            float blockY = block.GetY();
            float blockWidth = block.GetWidth();
            float blockHeight = block.GetHeight();

            if (Intersector.overlaps(blockBounds, northRect)) {
                this.y = (blockY - (blockHeight / scale));
            } else if (Intersector.overlaps(blockBounds, southRect)) {
                this.y = (blockY + (blockHeight / scale));
            }

            if (Intersector.overlaps(blockBounds, eastRect)) {
                this.x = (blockX - (blockWidth / scale ));
            } else if (Intersector.overlaps(blockBounds, westRect)) {
                this.x = (blockX + (blockWidth / scale));
            }
        }

        FireCoordinateEvent();
    }

    public void CollideExperimental(List<Block> blocks) {
        for (Block block : blocks) {
            Rectangle blockBounds = block.GetBounds();
            float blockX = block.GetX();
            float blockY = block.GetY();
            float blockWidth = block.GetWidth();
            float blockHeight = block.GetHeight();

            int collision = SegmentIntersector.HasIntersection(collisionRect, blockBounds);

            if ((collision & SegmentIntersector.SOUTH) != 0) {
                this.y = (blockY - (blockHeight / scale));
            } else if ((collision & SegmentIntersector.NORTH) != 0) {
                this.y = (blockY + (blockHeight / scale));
            }

            if ((collision & SegmentIntersector.WEST) != 0) {
                this.x = (blockX - (blockWidth / scale ));
            } else if ((collision & SegmentIntersector.EAST) != 0) {
                this.x = (blockX + (blockWidth / scale));
            }
        }
    }

    public void AddCoordinateEventListener(MovementEventListener listener) {
        if (coordEventListeners.contains(listener)) return;
        coordEventListeners.add(listener);
    }

    public Bomb PlantBomb(BoardState boardState) {
        int worldX = GetWorldX();
        int worldY = GetWorldY();

        if (boardState.GetBoardEntry(worldX, worldY) != BoardRep.EMPTY) {
            return null;
        }

        Bomb bomb = new Bomb(this, worldX, worldY, scale, scale, scale);
        bombs.add(bomb);

        return bomb;
    }

    public void RemoveBomb(Bomb bomb) {
        bombs.remove(bomb);
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

        UpdateDirection();
        UpdateState();

        this.x += velX * delta;
        this.y += velY * delta;

        if (pastX != x || pastY != y) {
            FireCoordinateEvent();
            pastX = x;
            pastY = y;
        }

        UpdateBounds();
    }

    @Override
    public void Draw(Batch batch) {

        if (state == PlayerState.MOVING || state == PlayerState.DYING) {
            batch.draw(GetActiveAnimation().getKeyFrame(animationElapsedTime), x * scale, y * scale, scale, scale);
        } else {
            batch.draw(GetActiveKeyFrame(), x * scale, y * scale, scale, scale);
        }
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        renderer.rect(collisionRect.x * scale, collisionRect.y * scale, collisionRect.width * scale, collisionRect.height * scale);

        renderer.setColor(Color.GREEN);
        renderer.rect(northRect.x * scale, northRect.y * scale, northRect.width * scale, northRect.height * scale);
        renderer.rect(southRect.x * scale, southRect.y * scale, southRect.width * scale, southRect.height * scale);
        renderer.rect(eastRect.x * scale, eastRect.y * scale, eastRect.width * scale, eastRect.height * scale);
        renderer.rect(westRect.x * scale, westRect.y * scale, westRect.width * scale, westRect.height * scale);
    }

    private void FireCoordinateEvent() {
        for (MovementEventListener listener : coordEventListeners) {
            listener.OnMove(id, x, y);
        }
    }
}
