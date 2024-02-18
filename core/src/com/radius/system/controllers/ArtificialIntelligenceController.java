package com.radius.system.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.trees.DefaultTree;
import com.radius.system.ai.behaviortree.trees.Tree;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.states.BoardState;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;

import java.util.List;

public class ArtificialIntelligenceController extends BoomPlayerController {

    private final Vector2 movementVector = new Vector2(0, 0);

    private final Point targetPoint, pastTarget, srcPoint;

    private final Tree tree;

    private List<Point> currentPath;

    public ArtificialIntelligenceController(int id, BoardState boardState, PlayerConfig config, float scale) {
        super(boardState, new Player(id, config.GetPlayerSpawnPoint(id), config.GetSpritePath(), scale));
        targetPoint = new Point(null, -1, -1);
        pastTarget = new Point(null, -1, -1);
        srcPoint = new Point(null, player.GetWorldX(), player.GetWorldY());
        tree = new DefaultTree(id, 2, boardState);
    }

    private void UpdateTree(float delta) {
        tree.SetData("srcPoint", srcPoint);
        tree.Update(delta);
        UpdateMovement();

        Boolean plantBomb = (Boolean) tree.GetData(NodeKeys.PLANT_BOMB);
        if (plantBomb != null && plantBomb) {
            PlantBomb();
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

        if (targetPoint.IsEqualPosition(player.GetDirection(), player.position.x, player.position.y)) {
            movementVector.x = movementVector.y = 0;
        }

        Object object = tree.GetData(NodeKeys.MOVEMENT_PATH);
        if (object == null) {
            currentPath = null;
            return;
        }
        currentPath = (List<Point>) tree.GetData(NodeKeys.MOVEMENT_PATH);

        Point point = currentPath.get(0);
        targetPoint.x = point.x;
        targetPoint.y = point.y;

        float sensitivity = 1f;
        movementVector.x = Math.round(UpdateMovementAxis(point.x, player.position.x) * sensitivity) / sensitivity;
        movementVector.y = Math.round(UpdateMovementAxis(point.y, player.position.y) * sensitivity) / sensitivity;
        if ((point.x != pastTarget.x || point.y != pastTarget.y)) {
            if (GlobalConstants.DEBUG) {
                System.out.println("(" + movementVector.x + ", " + movementVector.y + "): " + player.GetWorldX() + ", " + player.GetWorldY() + " ---> (" + point.x + ", " + point.y + ")");
            }
            pastTarget.x = point.x;
            pastTarget.y = point.y;
        }
    }

    @Override
    public void Update(float delta) {
        UpdateTree(delta);
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
