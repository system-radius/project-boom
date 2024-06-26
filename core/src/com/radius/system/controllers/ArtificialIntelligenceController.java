package com.radius.system.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.trees.AClassTree;
import com.radius.system.ai.behaviortree.trees.BClassTree;
import com.radius.system.ai.behaviortree.trees.CClassTree;
import com.radius.system.ai.behaviortree.trees.DClassTree;
import com.radius.system.ai.behaviortree.trees.EClassTree;
import com.radius.system.ai.behaviortree.trees.OmegaTree;
import com.radius.system.ai.behaviortree.trees.SClassTree;
import com.radius.system.ai.behaviortree.trees.Tree;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;
import com.radius.system.configs.PlayerConfig;

import java.util.List;

public class ArtificialIntelligenceController extends BoomPlayerController {

    private static final Color[] TRACKER_COLORS = new Color[]{Color.BLACK, Color.GOLD, Color.RED, Color.BLACK};

    private final Vector2 movementVector = new Vector2(0, 0);

    private final Point pastTarget;

    private final Tree tree;

    private List<Point> currentPath;

    private Point targetPoint;

    private boolean deathReset;

    private String activeNode = null;

    public ArtificialIntelligenceController(int id, PlayerConfig config, Vector2 spawnPoint, BoardState boardState, float scale) {
        super(boardState, new Player(id, spawnPoint, config.texturePath, scale));
        targetPoint = new Point();
        pastTarget = new Point();

        switch (config.botLevel) {
            case S_CLASS:
                tree = new SClassTree(id, boardState, player);
                break;
            case A_CLASS:
                tree = new AClassTree(id, boardState, player);
                break;
            case B_CLASS:
                tree = new BClassTree(id, boardState, player);
                break;
            case C_CLASS:
                tree = new CClassTree(id, boardState, player);
                break;
            case D_CLASS:
                tree = new DClassTree(id, boardState, player);
                break;
            case E_CLASS:
                tree = new EClassTree(id, boardState, player);
                break;
            case OMEGA:
            default:
                tree = new OmegaTree(id, boardState, player);
        }
    }

    public String GetActiveNode() {
        Object value = tree.GetData(NodeKeys.ACTIVE_NODE);
        if (value == null) {
            return "No Active node!";
        }

        String display = value.toString();

        if (!display.equals(activeNode)) {
            //System.out.println("[" + player.id + "]" + display);
            activeNode = display;
        }

        return value.toString();
    }

    @Override
    public void Play() {
        tree.Play();
    }

    @Override
    public void Pause() {
        tree.Pause();
    }

    @Override
    public void Restart() {
        tree.Restart();
        currentPath = null;
        targetPoint = null;
        movementVector.x = movementVector.y = 0;
    }

    private void UpdateTree(float delta) {
        tree.Update(delta);
        UpdateMovement();
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

    private void MoveToTargetPoint() {

        if (targetPoint == null) {
            return;
        }

        float sensitivity = 1f;
        movementVector.x = Math.round(UpdateMovementAxis(targetPoint.x, player.position.x) * sensitivity) / sensitivity;
        movementVector.y = Math.round(UpdateMovementAxis(targetPoint.y, player.position.y) * sensitivity) / sensitivity;

        if (targetPoint.IsEqualPosition(player.position.x, player.position.y)) {
            movementVector.x = movementVector.y = 0;
            Boolean plantBomb = (Boolean) tree.GetData(NodeKeys.PLANT_BOMB);
            if (plantBomb != null && plantBomb) {
                PlantBomb();
                tree.ClearData(NodeKeys.PLANT_BOMB);
                //System.out.println("Bomb has been planted!");
            }
            targetPoint = null;
            currentPath = null;
        }
    }

    @SuppressWarnings("unchecked")
    private void UpdateMovement() {
        MoveToTargetPoint();

        Object object = tree.GetData(NodeKeys.MOVEMENT_PATH);
        if (object == null) {
            currentPath = null;
            movementVector.x = movementVector.y = 0;
            return;
        }

        currentPath = (List<Point>) tree.GetData(NodeKeys.MOVEMENT_PATH);
        targetPoint = currentPath.get(0);

        if ((targetPoint.x != pastTarget.x || targetPoint.y != pastTarget.y)) {
            if (GlobalConstants.DEBUG) {
                //System.out.println("(" + movementVector.x + ", " + movementVector.y + "): " + player.GetWorldX() + ", " + player.GetWorldY() + " ---> (" + point.x + ", " + point.y + ")");
            }
            pastTarget.x = targetPoint.x;
            pastTarget.y = targetPoint.y;
        }
    }

    @Override
    public void Update(float delta) {

        if (player.GetRemainingLife() == 0) {
            if (deathReset) {
                Restart();
                deathReset = false;
            }
            return;
        }

        if (player.IsDead() && deathReset) {
            deathReset = false;
            Restart();
        } else if (player.IsAlive() && !deathReset) {
            deathReset = true;
        }

        if (tree.GetData(NodeKeys.CRASHED) != null) {
            //tree.ClearData(NodeKeys.CRASHED);
            player.Burn();
            return;
        }

        // Continuously update the player regardless of death status.
        player.MoveAlongX(movementVector.x);
        player.MoveAlongY(movementVector.y);
        player.Update(delta);
        player.Collide(boardState.GetSurroundingBlocks(player.GetWorldX(), player.GetWorldY()));

        if (!deathReset) {
            return;
        }
        UpdateTree(delta);
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

        float scale = GlobalConstants.WORLD_SCALE;
        float offset = (player.id + 1) * (scale / 5);

        renderer.setColor(TRACKER_COLORS[player.id]);
        for (Point point : currentPath) {
            renderer.rect((point.x * scale) + offset / 2, (point.y * scale) + offset / 2, scale - offset, scale - offset);
        }
    }

    @Override
    public void dispose() {
        tree.Stop();
        player.dispose();
    }

    @Override
    public String toString() {
        Point point = (Point) tree.GetData(NodeKeys.TARGET_POINT);
        StringBuilder sb = new StringBuilder();
        sb.append(GetActiveNode());
        sb.append(" / final target point: ");
        sb.append(point);
        if (currentPath != null) {
            sb.append(" / current target point: ");
            sb.append(currentPath.get(0));
        }
        sb.append("     ");
        sb.append(super.toString());
        sb.append(", movementVector -> [");
        sb.append(movementVector.x);
        sb.append(", ");
        sb.append(movementVector.y);
        sb.append("]");

        return sb.toString();
    }
}
