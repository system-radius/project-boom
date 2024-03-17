package com.radius.system.objects.players;

import com.badlogic.gdx.audio.Sound;
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
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.events.listeners.FirePathListener;
import com.radius.system.events.parameters.FirePathEvent;
import com.radius.system.board.BoardState;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombType;
import com.radius.system.enums.BonusType;
import com.radius.system.enums.Direction;
import com.radius.system.enums.PlayerState;
import com.radius.system.events.BombTypeChangeListener;
import com.radius.system.events.listeners.MovementEventListener;
import com.radius.system.events.listeners.StatChangeListener;
import com.radius.system.events.parameters.MovementEvent;
import com.radius.system.events.parameters.StatChangeEvent;
import com.radius.system.objects.Entity;
import com.radius.system.objects.bombs.Bomb;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.blocks.Bonus;
import com.radius.system.objects.bombs.GodBomb;
import com.radius.system.objects.bombs.ImpactBomb;
import com.radius.system.objects.bombs.PierceBomb;
import com.radius.system.objects.bombs.RemoteMine;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity implements FirePathListener {

    private static final float FRAME_DURATION_MOVING = 1f / 10f;

    private static final float FRAME_DURATION_DYING = 1f / 4f;

    /**
     * The maximum amount of bombs that a player can have.
     */
    public static final int BOMB_STOCK_LIMIT = 99;

    /**
     * THe maximum range for the fire power of the player.
     */
    public static final int FIRE_POWER_LIMIT = 99;

    /**
     * The maximum speed that a player can have.
     */
    public static final int SPEED_LIMIT = 10;

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

    private final List<MovementEventListener> movementEventListeners = new ArrayList<>();

    private final MovementEvent movementEvent;

    private final List<StatChangeListener> statChangeListeners = new ArrayList<>();

    private final StatChangeEvent statChangeEvent;

    private final List<BombTypeChangeListener> bombTypeChangeListeners = new ArrayList<>();

    private final List<Bomb> bombs;

    private final Vector2 respawnPoint;

    private FirePathEvent firePathEvent;

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
    private Texture warningSign;

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

    private boolean invulnerable = false, useRespawnPoint = true;

    private int bombStock = 0, firePower = 0, speedLevel = 0;

    private final float baseSpeedIncrease = 0.5f, baseSpeed = 2f;

    private BombType bombType = null;

    public final int id;

    public final String name;

    private String spritePath, displayedName;

    private final BitmapFont playerNameFont;

    private boolean godmode;

    private int kills, deaths, selfBurn;

    public Player(int id, Vector2 respawnPoint, String spritePath, float scale) {
        this(id, respawnPoint, spritePath, scale, false);
    }

    public Player(int id, Vector2 respawnPoint, String spritePath, float scale, boolean godmode) {
        super(BoardRep.PLAYER, respawnPoint.x, respawnPoint.y, scale, scale);

        this.id = id;
        this.name = godmode ? "Zero" : "Player" + (id + 1);
        this.godmode = godmode;
        playerNameFont = FontUtils.GetFont((int)scale / 4, Color.WHITE, 1, Color.BLACK);

        this.respawnPoint = respawnPoint;
        this.scale = scale;
        this.spritePath = spritePath;
        if (godmode) {
            this.spritePath = "img/player_5.png";
        }
        LoadAsset(this.spritePath);

        bombs = new ArrayList<>();
        movementEvent = new MovementEvent(id, respawnPoint.x, respawnPoint.y);
        statChangeEvent = new StatChangeEvent(id);
        firePathEvent = new FirePathEvent(id);
    }

    public void ActivateGodMode() {
        this.godmode = true;
    }

    public void Reset() {
        life = 3;
        bombStock = 1;
        firePower = 1;
        speedLevel = 1;
        kills = deaths = selfBurn = 0;
        bombType = BombType.NORMAL;

        Respawn(GetWorldPosition(respawnPoint.x, size.x), GetWorldPosition(respawnPoint.y, size.y));
        if (godmode) {
            this.spritePath = "img/player_5.png";
        }
        LoadAsset(this.spritePath);
        if (godmode) {
            bombStock = BOMB_STOCK_LIMIT;
            firePower = FIRE_POWER_LIMIT;
            speedLevel = SPEED_LIMIT;
            bombType = BombType.GODMODE;
            FixBounds();
        }
        movementSpeed = baseSpeed + speedLevel * baseSpeedIncrease;
        bombs.clear();

        FireStatChange(BonusType.BOMB_STOCK, bombStock);
        FireStatChange(BonusType.FIRE_POWER, firePower);
        FireStatChange(BonusType.MOVEMENT_SPEED, speedLevel);
    }

    private void FixBounds() {
        float x = position.x;
        float y = position.y;

        float width = 1f;
        float height = 1f;

        float fixedDivider = 1.1f;
        float balancer = 2f;
        float divider = SPEED_LIMIT - speedLevel;

        divider = divider == 0 ? balancer + 0.1f : balancer + divider;

        thinWidth = (width / divider) * fixedDivider;
        thinHeight = (height / divider) * fixedDivider;

        fixedThinWidth = (width / (fixedDivider * fixedDividerOffset));
        fixedThinHeight = (height / (fixedDivider * fixedDividerOffset));

        burnRect = RefreshRectangle(burnRect, x, y, width - fixedThinWidth, height - fixedThinHeight);
        collisionRect = RefreshRectangle(collisionRect, x, y, width - fixedThinWidth / fixedDividerCoordinator, height - fixedThinHeight / fixedDividerCoordinator);

        northRect = RefreshRectangle(northRect, x, y, width - thinWidth * balancer, thinHeight / fixedDividerOffset);
        southRect = RefreshRectangle(southRect, x, y, width - thinWidth * balancer, thinHeight / fixedDividerOffset);
        westRect = RefreshRectangle(westRect, x, y, thinWidth / fixedDividerOffset, height - thinHeight / verticalDivider);
        eastRect = RefreshRectangle(eastRect, x, y, thinWidth / fixedDividerOffset, height - thinHeight / verticalDivider);

        UpdateBounds();
    }

    private void LoadAsset(String spriteSheetPath) {
        //spriteSheet = new Texture(Gdx.files.internal(spriteSheetPath));
        TextureRegion[][] allFrames = GlobalAssets.LoadTextureRegion(spriteSheetPath, 32, 32);

        animations.add(CreateAnimation(allFrames, Direction.SOUTH.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.NORTH.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.WEST.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.EAST.GetIndex(), FRAME_DURATION_MOVING, true));
        animations.add(CreateAnimation(allFrames, Direction.DEAD.GetIndex(), FRAME_DURATION_DYING, false));

        warningSign = GlobalAssets.LoadTexture(GlobalAssets.WARNING_SIGN_PATH);
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

        FixBounds();

        state = PlayerState.IDLE;
        direction = Direction.SOUTH;

        velocity.x = velocity.y = 0;
        invulnerableTime = 0f;
        invulnerable = true;
        life--;

        ChangeBombType(BombType.NORMAL);
        FireStatChange(BonusType.LIFE, life);
        displayedName = name + "\n[" + GetRemainingLife() + "] ";
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

        northRect.setPosition(x + thinWidth, y + 1 - thinHeight / fixedDividerOffset);
        southRect.setPosition(x + thinWidth, y);
        eastRect.setPosition(x + 1 - thinWidth / fixedDividerOffset, y + thinHeight * verticalDivider);
        westRect.setPosition(x, y + thinHeight * verticalDivider);

        burnRect.setPosition(x + fixedThinWidth / fixedDividerOffset, y + fixedThinHeight / fixedDividerOffset);
        collisionRect.setPosition(x + fixedThinWidth / (fixedDividerCoordinator * fixedDividerOffset), y + fixedThinHeight / (fixedDividerCoordinator * fixedDividerOffset));
    }

    public int GetRemainingLife() {
        return life + (!IsDead() ? 1 : 0);
    }

    public int GetKills() {
        return kills;
    }

    public int GetSelfBurn() {
        return selfBurn;
    }

    public int GetDeaths() {
        return deaths;
    }

    public int GetFirePower() {
        return firePower;
    }

    public int GetAvailableBombs() {
        return bombStock - bombs.size();
    }

    public float GetMovementSpeed() {
        return movementSpeed;
    }

    public Rectangle GetBurnRect() {
        return burnRect;
    }

    public Rectangle GetCollisionRect() {
        return collisionRect;
    }

    public void CreditKill() {
        kills++;
    }

    public void CreditSelfBurn() {
        selfBurn++;
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

        float blockX = block.GetWorldX();
        float blockY = block.GetWorldY();
        float blockWidth = block.size.x/scale;
        float blockHeight = block.size.y/scale;

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
        if (bombType != BombType.IMPACT && bombType != BombType.GODMODE) {
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
        if (speedLevel + 1 > SPEED_LIMIT) {
            return;
        }

        speedLevel += 1f;
        movementSpeed = baseSpeed + (baseSpeedIncrease * speedLevel);
        FixBounds();
        FireStatChange(BonusType.MOVEMENT_SPEED, speedLevel);
    }

    public void ChangeBombType(BombType bombType) {

        if (this.bombType == BombType.GODMODE) {
            return;
        }

        if (this.bombType == bombType && !BombType.NORMAL.equals(bombType)) {
            IncreaseBombStock();
            return;
        }

        this.bombType = bombType;
        BonusType bonusType = BonusType.EMPTY;
        switch (bombType) {
            case PIERCE:
                bonusType = BonusType.PIERCE_BOMB;
                break;
            case IMPACT:
                bonusType = BonusType.IMPACT_BOMB;
                break;
            case REMOTE:
                bonusType = BonusType.REMOTE_MINE;
                break;
        }
        FireStatChange(bonusType, bombType.GetType());
    }

    public BombType GetBombType() {
        return bombType;
    }

    public void AddCoordinateEventListener(MovementEventListener listener) {
        if (movementEventListeners.contains(listener)) return;
        movementEventListeners.add(listener);
    }

    public void AddStatChangeListeners(List<StatChangeListener> listeners) {
        for (StatChangeListener listener : listeners) {
            AddStatChangeListener(listener);
        }
    }

    public void AddStatChangeListener(StatChangeListener listener) {
        if (statChangeListeners.contains(listener)) return;
        statChangeListeners.add(listener);
    }

    public Direction GetDirection() {
        return direction;
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
            case GODMODE:
                bomb = new GodBomb(this, worldX, worldY, scale, scale, scale);
                break;
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
            if (bomb.IsWaiting() && (BombType.REMOTE.equals(bomb.GetType()) || BombType.GODMODE.equals(bomb.GetType()))) {
                bombs.get(i).Explode();
                break;
            }
        }

    }

    public void RemoveBomb(Bomb bomb) {
        bombs.remove(bomb);
        FireStatChange(BonusType.BOMB_STOCK, bombStock - bombs.size());
    }

    public float GetDistance(Player that) {
        return Math.abs(that.position.x - this.position.x) + Math.abs(that.position.y - this.position.y);
    }

    public float GetDistance(Point point) {
        return Math.abs(this.position.x - point.x) + Math.abs(this.position.y - point.y);
    }

    public boolean IsAlive() {
        return state != PlayerState.DEAD && state != PlayerState.DYING;
    }

    public boolean IsDead() {
        return state == PlayerState.DEAD;
    }

    @Override
    public void dispose() {
        movementEventListeners.clear();
    }

    @Override
    public boolean Burn() {
        if (PlayerState.DYING.equals(state) || PlayerState.DEAD.equals(state) || invulnerable || life < 0) {
            return false;
        }

        deaths++;

        if (life == 0) {
            displayedName = name + "\n[x]";
        }

        state = PlayerState.DYING;
        animationElapsedTime = deathTime = respawnTime = 0f;
        GlobalAssets.PlaySound(GlobalAssets.PLAYER_BURN_SFX_PATH);
        return true;
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
            if (invulnerableTime >= INVULNERABLE_TIMER && !godmode) {
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
        if (respawnTime >= DEATH_TIMER && life > 0) {
            Respawn();
        }
    }

    private void DrawFireNotification(Batch batch) {

        float x = position.x  * scale, y = position.y  * scale;

        batch.setColor(1, 1, 1, 0.5f);
        DrawFireNotification(batch, warningSign, x, y + scale, scale, scale, firePathEvent.hasNorth);
        DrawFireNotification(batch, warningSign, x, y - scale, scale, scale, firePathEvent.hasSouth);
        DrawFireNotification(batch, warningSign, x - scale, y, scale, scale, firePathEvent.hasWest);
        DrawFireNotification(batch, warningSign, x + scale, y, scale, scale, firePathEvent.hasEast);
        batch.setColor(1, 1, 1, 1f);

    }

    private void DrawFireNotification(Batch batch, Texture texture, float x, float y, float width, float height, boolean draw) {
        if (!draw) {
            return;
        }

        batch.draw(texture, x, y, width, height);
    }

    @Override
    public void Draw(Batch batch) {

        if (state == PlayerState.MOVING || state == PlayerState.DYING) {
            activeAnimation = GetActiveAnimation();
            batch.draw(activeAnimation.getKeyFrame(animationElapsedTime), position.x * size.x, position.y * size.y, size.x, size.y);
        } else {
            batch.draw(GetActiveKeyFrame(), scaledPosition.x, scaledPosition.y, size.x, size.y);
        }

        if (firePathEvent.onFirePath) {
            DrawFireNotification(batch);
        }

        playerNameFont.draw(batch, displayedName, position.x * size.x, position.y * size.y, scale, Align.center, false);
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
        movementEvent.x = position.x;
        movementEvent.y = position.y;
        for (MovementEventListener listener : movementEventListeners) {
            listener.OnMove(movementEvent);
        }
    }

    private void FireStatChange(BonusType type, int value) {
        statChangeEvent.bonusType = type;
        statChangeEvent.value = value;
        for (StatChangeListener listener : statChangeListeners) {
            listener.OnStatChange(statChangeEvent);
        }
    }

    @Override
    public void OnFirePathTrigger(FirePathEvent event) {
        firePathEvent = event;
    }
}
