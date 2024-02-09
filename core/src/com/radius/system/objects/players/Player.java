package com.radius.system.objects.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.radius.system.board.BoardState;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombType;
import com.radius.system.enums.BonusType;
import com.radius.system.enums.Direction;
import com.radius.system.enums.PlayerState;
import com.radius.system.events.BombTypeChangeListener;
import com.radius.system.events.MovementEventListener;
import com.radius.system.events.StatChangeListener;
import com.radius.system.objects.Entity;
import com.radius.system.objects.bombs.Bomb;
import com.radius.system.objects.AnimatedGameObject;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.blocks.Bonus;
import com.radius.system.objects.bombs.ImpactBomb;
import com.radius.system.objects.bombs.PierceBomb;
import com.radius.system.objects.bombs.RemoteMine;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Entity {

    private static final float FRAME_DURATION_MOVING = 1f / 10f;

    private static final float FRAME_DURATION_DYING = 1f / 4f;

    /**
     * The maximum amount of bombs that a player can have.
     */
    public static final int BOMB_STOCK_LIMIT = 20;

    /**
     * THe maximum range for the fire power of the player.
     */
    public static final int FIRE_POWER_LIMIT = 100;

    /**
     * The maximum speed that a player can have.
     */
    public static final float SPEED_LIMIT = 7.5f;

    /**
     * The timer until the player respawns after dying.
     */
    private static final float DEATH_TIMER = 3f;

    /**
     * The amount of time to watch the player die.
     */
    private static final float DYING_TIMER = 2f;

    private static final float INVULNERABLE_TIMER = 5f;

    private final List<Animation<TextureRegion>> animations = new ArrayList<>();

    private final List<MovementEventListener> coordEventListeners = new ArrayList<>();

    private final List<StatChangeListener> statChangeListeners = new ArrayList<>();

    private final List<BombTypeChangeListener> bombTypeChangeListeners = new ArrayList<>();

    private final List<Bomb> bombs;

    private final Vector2 respawnPoint;

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
    protected Rectangle burnRect;

    protected Rectangle collisionRect;

    /**
     * The loaded sprite sheet for this player.
     */
    private Texture spriteSheet;

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

    private final float fixedDividerCoordinator = 8f, fixedDividerOffset = 2f, verticalDivider = 0.75f;

    private float thinWidth, thinHeight, fixedThinWidth, fixedThinHeight;

    private float deathTime, respawnTime, invulnerableTime;

    private boolean invulnerable = false, useRespawnPoint = false;

    private int bombStock = 0, firePower = 0, speedLevel = 0;

    private final float baseSpeedIncrease = 0.5f, baseSpeed = 1.5f;

    private BombType bombType = BombType.NORMAL;

    private final int id;

    private final String name;

    private final BitmapFont playerNameFont;

    public Player(int id, float x, float y, float scale) {
        super(BoardRep.PLAYER, x, y, scale, scale);

        this.id = id;
        this.name = "Player" + id;
        playerNameFont = FontUtils.GetFont((int)scale / 4, Color.WHITE, 1, Color.BLACK);

        respawnPoint = new Vector2(x, y);
        this.scale = scale;
        LoadAsset("img/tokoy_sprite_sheet.png");

        bombs = new ArrayList<>();
    }

    public void Reset() {

        bombStock = 0;
        firePower = 0;
        movementSpeed = 1f;
        speedLevel = 0;
        bombType = BombType.NORMAL;
        FireBombChangeEvent();

        bombs.clear();

        IncreaseBombStock();
        IncreaseFirePower(1);
        IncreaseMovementSpeed();
        Respawn(GetWorldPosition(respawnPoint.x, size.x), GetWorldPosition(respawnPoint.y, size.y));
    }

    private void FixBounds() {
        float x = position.x;
        float y = position.y;

        float width = 1f;
        float height = 1f;

        float fixedDivider = 1.1f;
        //float divider = (1.1f + SPEED_LIMIT) - movementSpeed;

        thinWidth = (width / (fixedDivider * 2));
        thinHeight = (height / (fixedDivider * 2));

        fixedThinWidth = (width / (fixedDivider * fixedDividerOffset));
        fixedThinHeight = (height / (fixedDivider * fixedDividerOffset));

        burnRect = RefreshRectangle(burnRect, x, y, width - fixedThinWidth, height - fixedThinHeight);
        collisionRect = RefreshRectangle(collisionRect, x, y, width + fixedThinWidth / fixedDividerCoordinator, height + fixedThinHeight / fixedDividerCoordinator);

        northRect = RefreshRectangle(northRect, x, y, width - (thinWidth * fixedDividerOffset), thinHeight / fixedDividerOffset);
        southRect = RefreshRectangle(southRect, x, y, width - (thinWidth * fixedDividerOffset), thinHeight / fixedDividerOffset);
        westRect = RefreshRectangle(westRect, x, y, thinWidth / fixedDividerOffset, height - fixedThinHeight / verticalDivider);
        eastRect = RefreshRectangle(eastRect, x, y, thinWidth / fixedDividerOffset, height - fixedThinHeight / verticalDivider);

        UpdateBounds();
    }

    private void LoadAsset(String spriteSheetPath) {
        spriteSheet = new Texture(Gdx.files.internal(spriteSheetPath));
        TextureRegion[][] allFrames = TextureRegion.split(spriteSheet, 32, 32);

        animations.add(CreateAnimation(allFrames, Direction.SOUTH.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.NORTH.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.WEST.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.EAST.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.DEAD.GetIndex(), FRAME_DURATION_DYING, false));
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
        return animations.get(direction.GetIndex());
    }

    private Animation<TextureRegion> GetActiveAnimation() {
        switch(state) {
            case DYING:
                return animations.get(Direction.DEAD.GetIndex());
            case MOVING:
            default:
                return GetActiveMovingAnimation();
        }
    }

    private TextureRegion GetActiveKeyFrame() {
        switch(state) {
            case DEAD:
                return animations.get(Direction.DEAD.GetIndex()).getKeyFrames()[3];
            case IDLE:
            default:
                return GetActiveMovingAnimation().getKeyFrames()[0];
        }
    }

    public void Respawn() {
        if (useRespawnPoint) {
            Respawn(GetWorldPosition(respawnPoint.x, size.x), GetWorldPosition(respawnPoint.y, size.y));
        } else {
            Respawn(position.x, position.y);
        }
    }

    public void Respawn(float x, float y) {
        position.x = x;
        position.y = y;

        state = PlayerState.IDLE;
        direction = Direction.SOUTH;

        velocity.x = velocity.y = 0;
        invulnerableTime = 0f;
        invulnerable = true;
    }

    private void UpdateDirection() {

        Direction horizontalDirection = null;
        Direction verticalDirection = null;
        if (velocity.x > 0) {
            horizontalDirection = Direction.EAST;
        } else if (velocity.x < 0) {
            horizontalDirection = Direction.WEST;
        }

        if (velocity.y > 0) {
            verticalDirection = Direction.NORTH;
        } else if (velocity.y < 0) {
            verticalDirection = Direction.SOUTH;
        }

        if (horizontalDirection != null && verticalDirection != null) {
            direction = Math.abs(velocity.x) > Math.abs(velocity.y) ? horizontalDirection : verticalDirection;
        } else if (horizontalDirection != null || verticalDirection != null) {
            direction = horizontalDirection == null ? verticalDirection : horizontalDirection;
        }
    }

    private void UpdateBounds() {

        float x = position.x;
        float y = position.y;

        northRect.setPosition(x + (thinWidth), (y + 1) - (thinHeight/fixedDividerOffset));
        southRect.setPosition(x + (thinWidth), y);
        eastRect.setPosition(x + (1 - (thinWidth / fixedDividerOffset)), y + (fixedThinHeight / fixedDividerOffset) / verticalDivider);
        westRect.setPosition(x, y + (fixedThinHeight / fixedDividerOffset) / verticalDivider);

        burnRect.setPosition(x + fixedThinWidth / fixedDividerOffset, y + fixedThinHeight / fixedDividerOffset);
        collisionRect.setPosition(x - fixedThinWidth / (fixedDividerCoordinator * fixedDividerOffset), y - fixedThinHeight / (fixedDividerCoordinator * fixedDividerOffset));
    }

    public int GetFirePower() {
        return firePower;
    }

    public Rectangle GetBurnRect() {
        return burnRect;
    }

    public Rectangle GetCollisionRect() {
        return collisionRect;
    }

    public void Collide(List<Block> blocks) {
        for (Block block : blocks) {

            if (block instanceof Bonus && (block.GetWorldX() == GetWorldX() && block.GetWorldY() == GetWorldY())) {
                ((Bonus) block).ApplyBonus(this);
                continue;
            }

            if (!block.HasActiveCollision(this)) {
                continue;
            }

            CollideWithBlock(block);
        }

        FireCoordinateEvent();
    }

    private void CollideWithBlock(Block block) {

        boolean hasCollision = false;

        if (block instanceof Bomb && ((Bomb) block).IsMoving()) {
            return;
        }

        Rectangle blockBounds = block.GetBounds();

        float blockX = blockBounds.x;
        float blockY = blockBounds.y;
        float blockWidth = blockBounds.width;
        float blockHeight = blockBounds.height;

        boolean collideX = false, collideY = false;

        if (Intersector.overlaps(blockBounds, northRect)) {
            position.y = (blockY - blockHeight);
            collideY = hasCollision = true;
        } else if (Intersector.overlaps(blockBounds, southRect)) {
            position.y = (blockY + blockHeight);
            collideY = hasCollision = true;
        }

        if (Intersector.overlaps(blockBounds, eastRect)) {
            position.x = (blockX - blockWidth);
            collideX = hasCollision = true;
        } else if (Intersector.overlaps(blockBounds, westRect)) {
            position.x = (blockX + blockWidth);
            collideX = hasCollision = true;
        }

        if (hasCollision && block instanceof Bomb) {
            ResolveCollision((Bomb) block, collideX, collideY);
        }

        RefreshScaledPosition();
    }

    private void ResolveCollision(Bomb bomb, boolean collideX, boolean collideY) {
        if (bombType != BombType.IMPACT) {
            return;
        }

        if (collideX && collideY) {
            if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
                bomb.Move(velocity.x, 0);
            } else {
                bomb.Move(0, velocity.y);
            }
        } else if (collideX) {
            bomb.Move(velocity.x, 0);
        } else if (collideY) {
            bomb.Move(0, velocity.y);
        }
    }

    public void IncreaseBombStock() {
        if (bombStock + 1 > BOMB_STOCK_LIMIT) {
            bombStock = BOMB_STOCK_LIMIT;
            return;
        }

        bombStock++;
        FireStatChange(BonusType.BOMB_STOCK, bombStock - bombs.size());
    }

    public void IncreaseFirePower(int increase) {
        if (firePower + increase >= FIRE_POWER_LIMIT) {
            firePower = FIRE_POWER_LIMIT;
            return;
        }

        firePower += increase;
        FireStatChange(BonusType.FIRE_POWER, firePower);
    }

    public void IncreaseMovementSpeed() {
        if (baseSpeed + (baseSpeedIncrease * (speedLevel + 1)) > SPEED_LIMIT) {
            movementSpeed = SPEED_LIMIT;
            return;
        }

        speedLevel += 1f;
        movementSpeed = baseSpeed + (baseSpeedIncrease * speedLevel);
        FixBounds();
        FireStatChange(BonusType.MOVEMENT_SPEED, speedLevel);
    }

    public void ChangeBombType(BombType bombType) {

        if (this.bombType == bombType) {
            IncreaseBombStock();
        }
        this.bombType = bombType;
        FireBombChangeEvent();
    }

    public void AddCoordinateEventListener(MovementEventListener listener) {
        if (coordEventListeners.contains(listener)) return;
        coordEventListeners.add(listener);
    }

    public void AddStatChangeListener(StatChangeListener listener) {
        if (statChangeListeners.contains(listener)) return;
        statChangeListeners.add(listener);
        FireStatChange(BonusType.BOMB_STOCK, bombStock);
        FireStatChange(BonusType.FIRE_POWER, firePower);
        FireStatChange(BonusType.MOVEMENT_SPEED, speedLevel);
    }

    public void AddBombTypeChangeListener(BombTypeChangeListener listener) {
        if (bombTypeChangeListeners.contains(listener)) return;
        bombTypeChangeListeners.add(listener);
    }

    public Bomb PlantBomb(BoardState boardState) {

        if (state == PlayerState.DEAD || state == PlayerState.DYING) {
            return null;
        }

        int worldX = GetWorldX();
        int worldY = GetWorldY();

        if (boardState.GetBoardEntry(worldX, worldY) != BoardRep.EMPTY || bombs.size() >= bombStock) {
            return null;
        }

        Bomb bomb;

        switch (bombType) {
            case REMOTE:
                bomb = new RemoteMine(this, worldX, worldY, scale, scale, scale);
                break;
            case PIERCE:
                bomb = new PierceBomb(this, worldX, worldY, scale, scale, scale);
                break;
            case IMPACT:
                bomb = new ImpactBomb(this, worldX, worldY, scale, scale, scale);
                break;
            case NORMAL:
            default:
                bomb = new Bomb(this, worldX, worldY, scale, scale, scale);
        }

        bombs.add(bomb);
        FireStatChange(BonusType.BOMB_STOCK, bombStock - bombs.size());
        return bomb;
    }

    public void DetonateBomb() {

        if (bombs.size() == 0 || state == PlayerState.DEAD || state == PlayerState.DYING) {
            return;
        }

        for (int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            if (bomb.IsWaiting() && BombType.REMOTE.equals(bomb.GetType())) {
                bombs.get(i).Explode();
                break;
            }
        }

    }

    public void RemoveBomb(Bomb bomb) {
        bombs.remove(bomb);
        FireStatChange(BonusType.BOMB_STOCK, bombStock - bombs.size());
    }

    @Override
    public void dispose() {
        spriteSheet.dispose();
    }

    @Override
    public void Burn() {
        if (PlayerState.DYING.equals(state) || PlayerState.DEAD.equals(state) || invulnerable) {
            return;
        }

        state = PlayerState.DYING;
        animationElapsedTime = deathTime = respawnTime = 0f;
    }

    @Override
    public void Update(float delta) {
        animationElapsedTime += delta;

        switch(state) {
            case MOVING:
                UpdateMovement(delta);
            case IDLE:
                UpdateInvulnerable(delta);
                UpdateState();
                break;
            case DYING:
                UpdateDying(delta);
                break;
            case DEAD:
                UpdateRespawn(delta);
        }
    }

    private void UpdateInvulnerable(float delta) {
        if (invulnerable) {
            invulnerableTime += delta;
            if (invulnerableTime >= INVULNERABLE_TIMER) {
                invulnerable = false;
            }
        }
    }

    private void UpdateMovement(float delta) {
        UpdateDirection();
        if (UpdatePosition(delta)) {
            FireCoordinateEvent();
        } else {
            state = PlayerState.IDLE;
        }
        UpdateBounds();
    }

    private void UpdateState() {
        if (velocity.x != 0 || velocity.y != 0) {
            state = PlayerState.MOVING;
        } else {
            state = PlayerState.IDLE;
        }
    }

    private void UpdateDying(float delta) {
        deathTime += delta;
        if (deathTime >= DYING_TIMER) {
            state = PlayerState.DEAD;
        }
    }

    private void UpdateRespawn(float delta) {
        respawnTime += delta;
        if (respawnTime >= DEATH_TIMER) {
            Respawn();
        }
    }

    @Override
    public void Draw(Batch batch) {

        if (state == PlayerState.MOVING || state == PlayerState.DYING) {
            activeAnimation = GetActiveAnimation();
            batch.draw(activeAnimation.getKeyFrame(animationElapsedTime), position.x * size.x, position.y * size.y, size.x, size.y);
        } else {
            batch.draw(GetActiveKeyFrame(), scaledPosition.x, scaledPosition.y, size.x, size.y);
        }

        playerNameFont.draw(batch, name, position.x * size.x, position.y * size.y, scale, Align.center, false);
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        renderer.rect(burnRect.x * scale, burnRect.y * scale, burnRect.width * scale, burnRect.height * scale);

        renderer.setColor(invulnerable ? Color.BLUE : Color.CYAN);
        renderer.rect(collisionRect.x * scale, collisionRect.y * scale, collisionRect.width * scale, collisionRect.height * scale);

        renderer.setColor(Color.GREEN);
        renderer.rect(northRect.x * scale, northRect.y * scale, northRect.width * scale, northRect.height * scale);
        renderer.rect(southRect.x * scale, southRect.y * scale, southRect.width * scale, southRect.height * scale);
        renderer.rect(eastRect.x * scale, eastRect.y * scale, eastRect.width * scale, eastRect.height * scale);
        renderer.rect(westRect.x * scale, westRect.y * scale, westRect.width * scale, westRect.height * scale);
    }

    private void FireCoordinateEvent() {
        for (MovementEventListener listener : coordEventListeners) {
            listener.OnMove(id, position.x, position.y);
        }
    }

    private void FireStatChange(BonusType type, int value) {
        for (StatChangeListener listener : statChangeListeners) {
            listener.OnStatChange(type, value);
        }
    }

    private void FireBombChangeEvent() {
        for (BombTypeChangeListener listener : bombTypeChangeListeners) {
            listener.OnBombTypeChange(bombType);
        }
    }
}
