package com.radius.system.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.ai.behaviortree.trees.DefaultTree;
import com.radius.system.ai.behaviortree.trees.Tree;
import com.radius.system.ai.pathfinding.AStar;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ArtificialIntelligenceController extends BoomPlayerController {

    private int[][] boardCost = new int[(int)GlobalConstants.WORLD_WIDTH][(int)GlobalConstants.WORLD_HEIGHT];

    private final Vector2 movementVector = new Vector2(0, 0);

    private final List<Player> players;

    private List<Point> currentPath;

    private final Point targetPoint, pastTarget, srcPoint;

    private Tree tree;

    public ArtificialIntelligenceController(int id, BoardState boardState, PlayerConfig config, float scale) {
        super(boardState, new Player(id, config.GetPlayerSpawnPoint(id), config.GetSpritePath(), scale));
        targetPoint = new Point(null, -1, -1, 0, 0);
        pastTarget = new Point(null, -1, -1, 0, 0);
        srcPoint = new Point(null, -1, -1, 0, 0);
        players =  boardState.GetPlayers();
        tree = new DefaultTree(id, boardState);
    }

    private void SelectTarget() {
        float x = Float.MAX_VALUE;
        float y = Float.MAX_VALUE;

        for (Player player : players) {
            if (player.equals(this.player)) {
                continue;
            }

            float diffX = Math.abs(player.GetWorldX() - this.player.GetWorldX());
            float diffY = Math.abs(player.GetWorldY() - this.player.GetWorldY());

            if (diffX < x && diffY < y) {
                x = diffX;
                y = diffY;
                targetPoint.x = player.GetWorldX();
                targetPoint.y = player.GetWorldY();
            }
        }
    }

    private float UpdateMovementAxis(float node, float position) {
        float value = node - position;
        if (value < 0) {
            if (value < -1) value += 1;
            value = (float) Math.floor(value);
        }
        else if (value > 0) {
            if (value > 1) value -= 1;
            value = (float) Math.ceil(value);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private void UpdateMovement() {

        SelectTarget();
        if (targetPoint.IsInvalid()) {
            movementVector.x = movementVector.y = 0;
            return;
        }

        Object object = tree.GetData("movementPath");
        if (object == null) {
            movementVector.x = movementVector.y = 0;
            currentPath = null;
            return;
        }
        currentPath = (List<Point>) tree.GetData("movementPath");

        Point point = currentPath.get(0);
        if ((targetPoint.x != pastTarget.x || targetPoint.y != pastTarget.y) && GlobalConstants.DEBUG) {
            System.out.println("(" + movementVector.x + ", " + movementVector.y + "): " + player.GetWorldX() + ", " + player.GetWorldY() + " ---> (" + point.x + ", " + point.y + ")");
            pastTarget.x = targetPoint.x;
            pastTarget.y = targetPoint.y;
        }

        float sensitivity = 1f;
        movementVector.x = Math.round(UpdateMovementAxis(point.x, player.position.x) * sensitivity) / sensitivity;
        movementVector.y = Math.round(UpdateMovementAxis(point.y, player.position.y) * sensitivity) / sensitivity;
    }

    @Override
    public void Update(float delta) {
        tree.SetData("srcPoint", srcPoint);
        tree.Update(delta);

        UpdateMovement();
        player.MoveAlongX(movementVector.x);
        player.MoveAlongY(movementVector.y);
        player.Update(delta);
        player.Collide(boardState.GetSurroundingBlocks(player.GetWorldX(), player.GetWorldY()));

        srcPoint.x = player.GetWorldX();
        srcPoint.y = player.GetWorldY();
    }

    @Override
    public void Draw(Batch batch) {
        player.Draw(batch);
    }

    @Override
    public void DrawDebug(ShapeRenderer renderer) {
        if (currentPath == null) {
            return;
        }

        renderer.setColor(Color.RED);
        float scale = GlobalConstants.WORLD_SCALE;
        for (Point point : currentPath) {
            renderer.rect(point.x * scale, point.y * scale, scale, scale);
        }
    }

    @Override
    public void dispose() {
        player.dispose();
    }
}
