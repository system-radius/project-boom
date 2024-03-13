package com.radius.system.objects.bombs;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombState;
import com.radius.system.enums.BombType;
import com.radius.system.enums.Direction;
import com.radius.system.objects.AnimatedGameObject;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bomb extends Block {

    protected static final int MAX_COST = GlobalConstants.WORLD_AREA;

    protected static final float FRAME_DURATION_BREATHING = 1f / 5f;

    protected static final float FRAME_DURATION_FIRE = 1f / 7.5f;

    protected static final float WAIT_TIMER = GlobalConstants.BOMB_WAIT_TIMER;

    private static final float EXPLOSION_TIMER = 1f;

    protected final Map<Player, Boolean> playerCollisions = new HashMap<>();

    protected final Player owner;

    protected final float scale;

    protected Animation<TextureRegion> breathingAnimation;

    protected Animation<TextureRegion> fireStreamNorth;

    protected Animation<TextureRegion> fireStreamSouth;

    protected Animation<TextureRegion> fireStreamWest;

    protected Animation<TextureRegion> fireStreamEast;

    protected Animation<TextureRegion> fireStreamV;

    protected Animation<TextureRegion> fireStreamH;

    protected Animation<TextureRegion> fireStreamCenter;

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

    protected Rectangle fireStreamNorthBound;

    protected Rectangle fireStreamSouthBound;

    protected Rectangle fireStreamWestBound;

    protected Rectangle fireStreamEastBound;

    protected Rectangle fireStreamCenterBound;

    protected Map<Direction, Integer> fireRanges = new HashMap<>();

    protected float range;

    protected boolean burnt = false;

    protected BombState state = BombState.BREATHING;

    protected BombType bombType;

    protected float preExplosionTime = 0f;

    private float explosionTime = 0f;

    private final float fixedDividerOffset = 2f;

    private float thinWidth, thinHeight;

    private final float boundsOffset = 0.15f;

    public Bomb(Player player, float x, float y, float width, float height, float scale) {
        this(BombType.NORMAL, player, x, y, width, height, scale);
    }

    public Bomb(BombType bombType, Player player, float x, float y, float width, float height, float scale) {
        super(BoardRep.BOMB, -1, x, y, width, height);

        this.owner = player;
        if (player != null) {
            this.range = player.GetFirePower();
        }
        this.scale = scale;

        this.bombType = bombType;
        LoadAssets();
        FixBounds();
    }

    protected void LoadAssets() {

        TextureRegion[][] bombFrameRegions = GlobalAssets.LoadTextureRegion(GlobalAssets.BOMB_TEXTURE_PATH, GlobalAssets.BOMB_TEXTURE_REGION_SIZE, GlobalAssets.BOMB_TEXTURE_REGION_SIZE);
        breathingAnimation = LoadAnimation(bombFrameRegions[bombType.GetType()], FRAME_DURATION_BREATHING, bombFrameRegions[bombType.GetType()].length, true);

        TextureRegion[][] fireFrameRegions = GlobalAssets.LoadTextureRegion(GlobalAssets.FIRE_TEXTURE_PATH, GlobalAssets.FIRE_TEXTURE_REGION_SIZE, GlobalAssets.FIRE_TEXTURE_REGION_SIZE);
        boolean fireLoop = false;
        fireStreamNorth = LoadAnimation(fireFrameRegions[0], FRAME_DURATION_FIRE, fireFrameRegions[0].length, fireLoop);
        fireStreamSouth = LoadAnimation(fireFrameRegions[1], FRAME_DURATION_FIRE, fireFrameRegions[1].length, fireLoop);
        fireStreamCenter = LoadAnimation(fireFrameRegions[2], FRAME_DURATION_FIRE, fireFrameRegions[2].length, fireLoop);
        fireStreamWest = LoadAnimation(fireFrameRegions[3], FRAME_DURATION_FIRE, fireFrameRegions[3].length, fireLoop);
        fireStreamEast = LoadAnimation(fireFrameRegions[4], FRAME_DURATION_FIRE, fireFrameRegions[4].length, fireLoop);
        fireStreamV = LoadAnimation(fireFrameRegions[5], FRAME_DURATION_FIRE, fireFrameRegions[5].length, fireLoop);
        fireStreamH = LoadAnimation(fireFrameRegions[6], FRAME_DURATION_FIRE, fireFrameRegions[6].length, fireLoop);
    }

    protected Animation<TextureRegion> LoadAnimation(TextureRegion[] frames, float frameDuration, int length, boolean isLooping) {
        TextureRegion[] container = new TextureRegion[length];
        System.arraycopy(frames, 0, container, 0, length);

        Animation<TextureRegion> animation = new Animation<>(frameDuration, container);
        if (isLooping) {
            animation.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animation;
    }

    private void FixBounds() {
        float x = position.x;
        float y = position.y;

        float width = 1f;
        float height = 1f;

        float divider = 1.1f;
        thinWidth = (width / (divider * 2));
        thinHeight = (height / (divider * 2));

        northRect = RefreshRectangle(northRect, x, y, width - (thinWidth * fixedDividerOffset), thinHeight / fixedDividerOffset);
        southRect = RefreshRectangle(southRect, x, y, width - (thinWidth * fixedDividerOffset), thinHeight / fixedDividerOffset);
        westRect = RefreshRectangle(westRect, x, y, thinWidth / fixedDividerOffset, height - (thinHeight * fixedDividerOffset));
        eastRect = RefreshRectangle(eastRect, x, y, thinWidth / fixedDividerOffset, height - (thinHeight * fixedDividerOffset));

        bounds = RefreshRectangle(bounds, x + boundsOffset / 2, y + boundsOffset / 2, 1 - boundsOffset, 1 - boundsOffset);

        UpdateCollisionBounds();

    }

    public void UpdateCollisionBounds() {
        float x = position.x;
        float y = position.y;

        float offset = 0;
        northRect.setPosition(x + (thinWidth), (y + 1) - (thinHeight / fixedDividerOffset) - (offset / scale));
        southRect.setPosition(x + (thinWidth), y + (offset / scale));
        eastRect.setPosition(x + (1 - (thinWidth / fixedDividerOffset)) - (offset / scale), y + (thinHeight));
        westRect.setPosition(x + (offset / scale), y + (thinHeight));

        bounds.setPosition(x + boundsOffset / 2, y + boundsOffset / 2);
    }

    public int GetCost() {
        // Get percentage of the explosion limit against the current time.
        float percent = 1 - ((WAIT_TIMER - preExplosionTime) / WAIT_TIMER);
        return IsExploding() || IsSetToExplode() ? -1 : (int)(MAX_COST * percent);
    }

    public Map<Direction, Integer> GetRangeValues() {
        return fireRanges;
    }

    public void UpdateBounds(BoardState boardState) {

        int intX = GetWorldX();
        int intXPlus1 = intX + 1;
        int intXLess1 = intX - 1;

        int intY = GetWorldY();
        int intYPlus1 = intY + 1;
        int intYLess1 = intY - 1;

        int rangeNorth = CheckObstacle(boardState, intX, intYPlus1, Direction.NORTH, 1);
        int rangeSouth = CheckObstacle(boardState, intX, intYLess1, Direction.SOUTH, 1);
        int rangeWest = CheckObstacle(boardState, intXLess1, intY, Direction.WEST, 1);
        int rangeEast = CheckObstacle(boardState, intXPlus1, intY, Direction.EAST, 1);

        fireRanges.put(Direction.NORTH, rangeNorth);
        fireRanges.put(Direction.SOUTH, rangeSouth);
        fireRanges.put(Direction.WEST, rangeWest);
        fireRanges.put(Direction.EAST, rangeEast);

        float widthOffset = 0.5f, heightOffset = 0.5f, reduction = 1.25f;

        fireStreamNorthBound = RefreshRectangle(fireStreamNorthBound, intX + widthOffset / 2, intYPlus1, widthOffset, (rangeNorth - reduction));
        fireStreamSouthBound = RefreshRectangle(fireStreamSouthBound, intX + widthOffset / 2, intYPlus1 - rangeSouth + heightOffset / 2, widthOffset, (rangeSouth - reduction));
        fireStreamWestBound = RefreshRectangle(fireStreamWestBound, intXPlus1 - rangeWest + widthOffset / 2, intY + heightOffset / 2, (rangeWest - reduction), heightOffset);
        fireStreamEastBound = RefreshRectangle(fireStreamEastBound, intXPlus1, intY + heightOffset / 2, (rangeEast - reduction), heightOffset);
        fireStreamCenterBound = RefreshRectangle(fireStreamCenterBound, intX, intY, 1, 1);
    }

    protected int CheckObstacle(BoardState boardState, int x, int y, Direction direction, int counter) {
        if (counter > range) return 1;

        BoardRep rep = boardState.GetBoardEntry(x, y);

        if (rep == null) {
            return 1;
        }

        switch (rep) {
            case PERMANENT_BLOCK:
            case HARD_BLOCK:
            case SOFT_BLOCK:
                return 2;
        }

        counter++;

        switch (direction) {
            case NORTH:
                return 1 + CheckObstacle(boardState, x, y + 1, direction, counter);
            case SOUTH:
                return 1 + CheckObstacle(boardState, x, y - 1, direction, counter);
            case WEST:
                return 1 + CheckObstacle(boardState, x - 1, y, direction, counter);
            case EAST:
                return 1 + CheckObstacle(boardState, x + 1, y, direction, counter);
        }

        return 1;
    }

    public void BurnObjects(BoardState boardState) {

        if (burnt) {
            return;
        }

        int x = (int) GetWorldX();
        int y = (int) GetWorldY();

        for (int i = 1; i <= range; i++) {
            BurnObject(boardState, i, fireRanges.get(Direction.NORTH), x, y + i);
            BurnObject(boardState, i, fireRanges.get(Direction.SOUTH), x, y - i);
            BurnObject(boardState, i, fireRanges.get(Direction.WEST), x - i, y);
            BurnObject(boardState, i, fireRanges.get(Direction.EAST), x + i, y);
        }

        burnt = true;
    }

    protected void BurnObject(BoardState boardState, int counter, int range, int x, int y) {
        if (counter >= range) {
            return;
        }

        AnimatedGameObject object = boardState.GetBoardObject(x, y);
        if (object != null && (!burnt || object instanceof Bomb)) {
            object.Burn();
        }
    }

    public boolean Collide(List<Block> blocks) {
        for (Block block : blocks) {

            if (block.equals(this)) {
                continue;
            }

             if (CollideWithBlock(block)) {
                 return true;
             }
        }

        return false;
    }

    private boolean CollideWithBlock(Block block) {

        boolean hasCollision = false;
        Rectangle blockBounds = block.GetBounds();

        float blockX = blockBounds.x;
        float blockY = blockBounds.y;
        float blockWidth = blockBounds.width;
        float blockHeight = blockBounds.height;

        if (Intersector.overlaps(blockBounds, northRect)) {
            position.y = (blockY - blockHeight);
            hasCollision = true;
        } else if (Intersector.overlaps(blockBounds, southRect)) {
            position.y = (blockY + blockHeight);
            hasCollision = true;
        }

        if (Intersector.overlaps(blockBounds, eastRect)) {
            position.x = (blockX - blockWidth);
            hasCollision = true;
        } else if (Intersector.overlaps(blockBounds, westRect)) {
            position.x = (blockX + blockWidth);
            hasCollision = true;
        }

        RefreshScaledPosition();
        return hasCollision;
    }

    public Player GetOwner() {
        return owner;
    }

    public final void Explode() {

        if (IsExploding()) {
            return;
        }

        GlobalAssets.PlaySound(GlobalAssets.EXPLOSION_SFX_PATH);

        state = BombState.EXPLODING;
        animationElapsedTime = 0;
        velocity.x = velocity.y = 0;
        position.x = GetWorldX();
        position.y = GetWorldY();
    }

    public boolean IsWaiting() {
        return state == BombState.BREATHING || state == BombState.SET_TO_EXPLODE ;
    }

    public BombState GetState() {
        return state;
    }

    public boolean IsMoving() {
        return state == BombState.MOVING;
    }

    public boolean IsExploding() {
        return state == BombState.EXPLODING;
    }

    public boolean IsSetToExplode() {
        return state == BombState.SET_TO_EXPLODE;
    }

    public boolean IsExploded() {
        return state == BombState.EXPLODED;
    }

    public boolean HasContact(Rectangle rect) {
        return Intersector.overlaps(fireStreamEastBound, rect) ||
                Intersector.overlaps(fireStreamWestBound, rect) ||
                Intersector.overlaps(fireStreamNorthBound, rect) ||
                Intersector.overlaps(fireStreamSouthBound, rect) ||
                Intersector.overlaps(fireStreamCenterBound, rect);
    }

    public BombType GetType() {
        return bombType;
    }

    public void AddPlayerCollision(List<Player> players) {
        for (Player player : players) {
            playerCollisions.put(player, !Intersector.overlaps(player.GetCollisionRect(), bounds));
        }
    }

    public void UpdatePlayerCollision() {
        for (Player player : playerCollisions.keySet()) {
            if (playerCollisions.get(player)) {
                continue;
            }

            playerCollisions.put(player, !Intersector.overlaps(player.GetCollisionRect(), bounds));
        }
    }


    @Override
    public void Move(float x, float y) {

        if (state == BombState.MOVING) {
            return;
        }

        super.Move(Math.round(x), Math.round(y));
        state = BombState.MOVING;
    }

    @Override
    public boolean HasActiveCollision(Player player) {
        if (IsMoving() || IsExploding() || IsExploded() || owner == null) {
            return false;
        }

        return playerCollisions.get(player);
    }

    @Override
    public boolean Burn() {

        if (IsExploding() || IsSetToExplode() || IsExploded()) {
            return false;
        }

        state = BombState.SET_TO_EXPLODE;
        preExplosionTime = WAIT_TIMER - 0.1f;
        return true;
    }

    @Override
    public void Update(float delta) {
        animationElapsedTime += delta;

        switch(state) {
            case MOVING:
            case BREATHING:
            case SET_TO_EXPLODE:
                UpdateBreathing(delta);
                UpdatePlayerCollision();
                break;
            case EXPLODING:
                UpdateExploding(delta);
                break;
            case DUD:
            default:
        }
    }

    protected void UpdateBreathing(float delta) {

        preExplosionTime += delta;
        if (preExplosionTime >= WAIT_TIMER) {
            Explode();
        }
    }

    protected void UpdateExploding(float delta) {

        explosionTime += delta;
        if (explosionTime >= EXPLOSION_TIMER) {
            state = BombState.EXPLODED;
            if (owner != null) {
                owner.RemoveBomb(this);
            }
        }
    }

    @Override
    public void Draw(Batch batch) {

        switch (state) {
            case MOVING:
            case BREATHING:
            case SET_TO_EXPLODE:
                DrawAnimation(batch, breathingAnimation, position.x, position.y);
                break;
            case EXPLODING:
                DrawFire(batch);
                break;
        }
    }

    private void DrawFire(Batch batch) {
        int x = GetWorldX();
        int y = GetWorldY();

        for (int i = 1; i <= range; i++) {
            DrawFireDirection(batch, fireStreamV, fireStreamNorth, i, fireRanges.get(Direction.NORTH), x, (y + i));
            DrawFireDirection(batch, fireStreamV, fireStreamSouth, i, fireRanges.get(Direction.SOUTH), x, (y - i));
            DrawFireDirection(batch, fireStreamH, fireStreamWest, i, fireRanges.get(Direction.WEST), (x - i), y);
            DrawFireDirection(batch, fireStreamH, fireStreamEast, i, fireRanges.get(Direction.EAST), (x + i), y);
        }

        DrawAnimation(batch, fireStreamCenter, x, y);

    }

    private void DrawFireDirection(Batch batch, Animation<TextureRegion> animation, Animation<TextureRegion> maxAnimation, int counter, int range, float x, float y) {

        if (counter + 1 < range) {
            DrawAnimation(batch, animation, x, y);
        } else if (counter < range) {
            DrawAnimation(batch, maxAnimation, x, y);
        }

    }

    private void DrawAnimation(Batch batch, Animation<TextureRegion> animation, float x, float y) {
        batch.draw(animation.getKeyFrame(animationElapsedTime), x * size.x, y * size.y, size.x, size.y);
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {

        renderer.setColor(Color.GREEN);
        DrawRect(renderer, northRect);
        DrawRect(renderer, southRect);
        DrawRect(renderer, westRect);
        DrawRect(renderer, eastRect);
        DrawRect(renderer, bounds);

        if (IsExploding()) {
            renderer.setColor(Color.RED);

            DrawRect(renderer, fireStreamNorthBound);
            DrawRect(renderer, fireStreamEastBound);

            renderer.setColor(Color.CYAN);
            DrawRect(renderer, fireStreamSouthBound);
            DrawRect(renderer, fireStreamWestBound);

            renderer.setColor(Color.GREEN);
            DrawRect(renderer, fireStreamCenterBound);
        }

    }

    private void DrawRect(ShapeRenderer renderer, Rectangle rect) {
        if (rect == null) return;
        renderer.rect(rect.x * size.x, rect.y * size.y, rect.width * size.x, rect.height * size.y);
    }

}
