package com.radius.system.objects.bombs;

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

    public static final Texture BOMB_TEXTURE = new Texture(Gdx.files.internal("img/bomb.png"));

    public static final Texture FIRE_TEXTURE = new Texture(Gdx.files.internal("img/spiralingFire.png"));

    protected static final TextureRegion[][] BOMB_REGIONS =
            TextureRegion.split(BOMB_TEXTURE, 32, 32);

    protected static final TextureRegion[][] FIRE_REGIONS =
            TextureRegion.split(FIRE_TEXTURE, 64, 64);

    protected static final float FRAME_DURATION_BREATHING = 1f / 5f;

    protected static final float FRAME_DURATION_FIRE = 1f / 7.5f;

    private static final float WAIT_TIMER = 3f;

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

    protected Rectangle fireStreamNorthBound;

    protected Rectangle fireStreamSouthBound;

    protected Rectangle fireStreamWestBound;

    protected Rectangle fireStreamEastBound;

    protected float rangeNorth;

    protected float rangeSouth;

    protected float rangeEast;

    protected float rangeWest;

    protected float range;

    protected boolean burnt = false;

    protected BombState state = BombState.BREATHING;

    protected BombType bombType;

    private float preExplosionTime = 0f;

    private float explosionTime = 0f;

    public Bomb(Player player, float x, float y, float width, float height, float scale) {
        this(BombType.NORMAL, player, x, y, width, height, scale);
    }

    public Bomb(BombType bombType, Player player, float x, float y, float width, float height, float scale) {
        super(BoardRep.BOMB, -1, x, y, width, height);

        this.owner = player;
        this.range = player.GetFirePower();
        this.scale = scale;

        this.bombType = bombType;
        LoadAssets();
    }

    protected void LoadAssets() {
        breathingAnimation = LoadAnimation(BOMB_REGIONS[bombType.GetType()], FRAME_DURATION_BREATHING, BOMB_REGIONS[bombType.GetType()].length, true);

        boolean fireLoop = false;
        fireStreamNorth = LoadAnimation(FIRE_REGIONS[0], FRAME_DURATION_FIRE, FIRE_REGIONS[0].length, fireLoop);
        fireStreamSouth = LoadAnimation(FIRE_REGIONS[1], FRAME_DURATION_FIRE, FIRE_REGIONS[1].length, fireLoop);
        fireStreamCenter = LoadAnimation(FIRE_REGIONS[2], FRAME_DURATION_FIRE, FIRE_REGIONS[2].length, fireLoop);
        fireStreamWest = LoadAnimation(FIRE_REGIONS[3], FRAME_DURATION_FIRE, FIRE_REGIONS[3].length, fireLoop);
        fireStreamEast = LoadAnimation(FIRE_REGIONS[4], FRAME_DURATION_FIRE, FIRE_REGIONS[4].length, fireLoop);
        fireStreamV = LoadAnimation(FIRE_REGIONS[5], FRAME_DURATION_FIRE, FIRE_REGIONS[5].length, fireLoop);
        fireStreamH = LoadAnimation(FIRE_REGIONS[6], FRAME_DURATION_FIRE, FIRE_REGIONS[6].length, fireLoop);
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

    public void UpdateBounds(BoardState boardState) {

        int intX = GetWorldX();
        int intXPlus1 = intX + 1;
        int intXLess1 = intX - 1;

        int intY = GetWorldY();
        int intYPlus1 = intY + 1;
        int intYLess1 = intY - 1;

        rangeNorth = CheckObstacle(boardState, intX, intYPlus1, Direction.NORTH, 1);
        rangeSouth = CheckObstacle(boardState, intX, intYLess1, Direction.SOUTH, 1);
        rangeWest = CheckObstacle(boardState, intXLess1, intY, Direction.WEST, 1);
        rangeEast = CheckObstacle(boardState, intXPlus1, intY, Direction.EAST, 1);

        fireStreamNorthBound = RefreshRectangle(fireStreamNorthBound, intX, intYPlus1, 1, (rangeNorth - 1));
        fireStreamSouthBound = RefreshRectangle(fireStreamSouthBound, intX, intYLess1 + 1, 1, -(rangeSouth - 1));
        fireStreamWestBound = RefreshRectangle(fireStreamWestBound, intXLess1 + 1, intY, -(rangeWest - 1), 1);
        fireStreamEastBound = RefreshRectangle(fireStreamEastBound, intXPlus1, intY, (rangeEast - 1), 1);
    }

    protected int CheckObstacle(BoardState boardState, int x, int y, Direction direction, int counter) {
        if (counter > range) return 1;

        BoardRep rep = boardState.GetBoardEntry(x, y);

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
            BurnObject(boardState, i, (int) rangeNorth, x, y + i);
            BurnObject(boardState, i, (int) rangeSouth, x, y - i);
            BurnObject(boardState, i, (int) rangeWest, x - i, y);
            BurnObject(boardState, i, (int) rangeEast, x + i, y);
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

    public void Explode() {
        state = BombState.EXPLODING;
        animationElapsedTime = 0;
    }

    public boolean IsWaiting() {
        return state == BombState.BREATHING || state == BombState.SET_TO_EXPLODE ;
    }

    public boolean IsExploding() {
        return state == BombState.EXPLODING;
    }

    public boolean IsExploded() {
        return state == BombState.EXPLODED;
    }

    public boolean HasContact(Rectangle rect) {
        return Intersector.overlaps(fireStreamEastBound, rect) ||
                Intersector.overlaps(fireStreamWestBound, rect) ||
                Intersector.overlaps(fireStreamNorthBound, rect) ||
                Intersector.overlaps(fireStreamSouthBound, rect);
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
    public boolean HasActiveCollision(Player player) {
        return playerCollisions.get(player);
    }

    @Override
    public void Burn() {

        if (state == BombState.SET_TO_EXPLODE || state == BombState.EXPLODING) {
            return;
        }

        state = BombState.SET_TO_EXPLODE;
        preExplosionTime = WAIT_TIMER - 0.1f;
    }

    @Override
    public void Update(float delta) {
        animationElapsedTime += delta;

        switch(state) {
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
            owner.RemoveBomb(this);
        }
    }

    @Override
    public void Draw(Batch batch) {

        switch (state) {
            case BREATHING:
            case SET_TO_EXPLODE:
                DrawAnimation(batch, breathingAnimation, GetWorldX(), GetWorldY());
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
            DrawFireDirection(batch, fireStreamV, fireStreamNorth, i, (int) rangeNorth, x, (y + i));
            DrawFireDirection(batch, fireStreamV, fireStreamSouth, i, (int) rangeSouth, x, (y - i));
            DrawFireDirection(batch, fireStreamH, fireStreamWest, i, (int) rangeWest, (x - i), y);
            DrawFireDirection(batch, fireStreamH, fireStreamEast, i, (int) rangeEast, (x + i), y);
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

        if (IsExploding()) {
            renderer.setColor(Color.RED);

            DrawRect(renderer, fireStreamNorthBound);
            DrawRect(renderer, fireStreamSouthBound);
            DrawRect(renderer, fireStreamWestBound);
            DrawRect(renderer, fireStreamEastBound);
        }

    }

    private void DrawRect(ShapeRenderer renderer, Rectangle rect) {
        renderer.rect(rect.x * size.x, rect.y * size.y, rect.width * size.x, rect.height * size.y);
    }

}
