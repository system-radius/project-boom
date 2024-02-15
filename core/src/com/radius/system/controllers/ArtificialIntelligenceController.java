package com.radius.system.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.ai.AStar;
import com.radius.system.ai.Node;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;
import com.radius.system.objects.players.PlayerConfig;

import java.util.List;

public class ArtificialIntelligenceController extends BoomPlayerController {

    private int[][] boardCost = new int[(int)GlobalConstants.WORLD_WIDTH][(int)GlobalConstants.WORLD_HEIGHT];

    private final Vector2 movementVector = new Vector2(0, 0);

    private final List<Player> players;

    private List<Node> currentPath;

    private final Node targetNode, pastTarget;

    public ArtificialIntelligenceController(int id, BoardState boardState, PlayerConfig config, float scale) {
        super(boardState, new Player(id, config.GetPlayerSpawnPoint(id), config.GetSpritePath(), scale));
        targetNode = new Node(null, -1, -1, 0, 0);
        pastTarget = new Node(null, -1, -1, 0, 0);
        players =  boardState.GetPlayers();
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
                targetNode.x = player.GetWorldX();
                targetNode.y = player.GetWorldY();
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

    private void UpdateMovement() {

        SelectTarget();
        if (targetNode.IsInvalid()) {
            movementVector.x = movementVector.y = 0;
            return;
        }

        boardState.CompileBoardCost(boardCost);
        currentPath = AStar.FindShortestPath(boardCost, player.GetWorldX(), player.GetWorldY(), targetNode.x, targetNode.y);
        if (currentPath == null) {
            movementVector.x = movementVector.y = 0;
            return;
        }

        Node node = currentPath.get(0);
        if ((targetNode.x != pastTarget.x || targetNode.y != pastTarget.y) && GlobalConstants.DEBUG) {
           System.out.println("(" + movementVector.x + ", " + movementVector.y + "): " + player.GetWorldX() + ", " + player.GetWorldY() + " ---> (" + node.x + ", " + node.y + ")");
           pastTarget.x = targetNode.x;
           pastTarget.y = targetNode.y;
        }

        float sensitivity = 1f;
        movementVector.x = Math.round(UpdateMovementAxis(node.x, player.position.x) * sensitivity) / sensitivity;
        movementVector.y = Math.round(UpdateMovementAxis(node.y, player.position.y) * sensitivity) / sensitivity;

    }

    @Override
    public void Update(float delta) {
        UpdateMovement();
        player.MoveAlongX(movementVector.x);
        player.MoveAlongY(movementVector.y);
        player.Update(delta);
        player.Collide(boardState.GetSurroundingBlocks(player.GetWorldX(), player.GetWorldY()));
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
        for (Node node : currentPath) {
            renderer.rect(node.x * scale, node.y * scale, scale, scale);
        }
    }

    @Override
    public void dispose() {
        player.dispose();
    }
}
