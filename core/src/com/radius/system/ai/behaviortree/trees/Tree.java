package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.checks.OnFirePath;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.BasicFindBombArea;
import com.radius.system.ai.behaviortree.tasks.BasicFindPlayer;
import com.radius.system.ai.behaviortree.tasks.FindBonus;
import com.radius.system.ai.behaviortree.tasks.BasicFindSafeSpace;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.ai.behaviortree.tasks.PlantBomb;
import com.radius.system.ai.pathfinding.PathFinder;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.NodeState;
import com.radius.system.board.BoardState;
import com.radius.system.objects.players.Player;
import com.radius.system.utils.ThreadUtils;

import java.util.List;

public abstract class Tree implements Runnable {

    protected final int id, fireThreshold;

    protected final BoardState boardState;

    protected final int[][] boardCost;

    protected final Node root;

    protected final Point srcPoint;

    protected final PathFinder pathFinder;

    protected final Player owner;

    private boolean running = false, playing = false;

    public Tree(int id, int fireThreshold, BoardState boardState, Player player) {
        this.owner = player;
        this.id =  id;
        this.fireThreshold = fireThreshold;
        this.boardState = boardState;
        boardCost = new int[boardState.BOARD_WIDTH][boardState.BOARD_HEIGHT];

        srcPoint = new Point();
        this.pathFinder = new PathFinder(boardState);

        root = SetupTree();
    }

    public void Start() {
        if (running) {
            return;
        }

        playing = running = true;
        ThreadUtils.EXECUTOR.execute(this);
    }

    public void Stop() {
        if (!running) {
            return;
        }

        running = false;
    }

    public void Play() {
        playing = true;
    }

    public void Pause() {
        playing = false;
    }

    public void Restart() {
        root.Restart();
    }

    public boolean ClearData(String key) {
        return root.ClearData(key);
    }

    public Object GetData(String key) {
        return root.GetData(key);
    }

    public final void Update(float delta) {
        if (owner.IsAlive() && !running) {
            Start();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double ticksAmount = GlobalConstants.TICKS_PER_SECOND;
        double ticksPerSecond = 1_000_000_000 / ticksAmount;
        double delta = 0;
        playing = true;
        long timer = System.currentTimeMillis(), timeDelta = 0;
        int ticks = 0;
        while (running) {
            if (!owner.IsAlive()) {
                break;
            }
            long now = System.nanoTime();
            if (playing) {
                delta += (now - lastTime) / ticksPerSecond;
            }
            lastTime = now;
            if (root.GetData(NodeKeys.CRASHED) != null) {
                continue;
            }
            while (delta >= 1) {
                delta--;

                srcPoint.x = owner.GetWorldX();
                srcPoint.y = owner.GetWorldY();

                try {
                    boardState.CompileBoardCost(boardCost, fireThreshold, id);
                    Evaluate(boardCost);
                } catch (ArrayIndexOutOfBoundsException arrayException) {
                    root.SetData(NodeKeys.CRASHED, true);
                    System.out.println("[" + (id + 1) + "] Crashed due to out of bounds! Current position: " + srcPoint);
                    break;
                } catch (Exception e) {
                    root.SetData(NodeKeys.CRASHED, true);
                    System.out.println("[" + (id + 1) + "] Crashed due everything else!");
                    e.printStackTrace();
                    break;
                }

                /*
                GetData(NodeKeys.ACTIVE_NODE);
                (Boolean) GetData(NodeKeys.PLANT_BOMB);
                (List<Point>) GetData(NodeKeys.MOVEMENT_PATH);
                (Point) GetData(NodeKeys.TARGET_POINT);

                 */
                ticks++;
            }

            long currentTime = System.currentTimeMillis();
            if (playing) timeDelta += currentTime - timer;
            timer = currentTime;
            if (timeDelta > 1000) {
                timeDelta = 0;
                //TimerDisplay.LogTimeStamped("[" + (id + 1) + "]" + ": " + ticks + " ticks!");
                ticks = 0;
            }
        }

        Stop();
    }

    protected void Evaluate(int[][] boardCost) {
        NodeState state = root.Start(srcPoint, pathFinder, boardCost);
        List<Node> children = root.GetChildren();
        for (Node child : children) {
            if (state.equals(child.GetState())) {
                child.Execute();
                break;
            }
        }
    }

    protected abstract Node SetupTree();

    protected Node ConstructDefenseTree(int fireThreshold, boolean backup) {
        Node findSafeSpaceTarget = new Selector("[+] FindSpace");
        //findSafeSpaceTarget.AttachChild(new HasTargetPoint());
        findSafeSpaceTarget.AttachChild(new BasicFindSafeSpace(fireThreshold, boardState));

        Node root = new Sequencer("[>] Defense" + fireThreshold);
        root.AttachChild(new OnFirePath(fireThreshold, backup));
        root.AttachChild(findSafeSpaceTarget);
        root.AttachChild(new MoveToTarget());

        return root;
    }

    protected Node ConstructFindBonusTree() {
        Node root = new Sequencer("[>] Bonus");
        root.AttachChild(new FindBonus(fireThreshold, boardState));
        root.AttachChild(new MoveToTarget());

        return root;
    }

    protected Node ConstructAttackPlayerTree() {
        Node root = new Sequencer("[>] AttackP");
        root.AttachChild(new BasicFindPlayer(id, fireThreshold, boardState));
        root.AttachChild(new MoveToTarget(new PlantBomb()));

        return root;
    }

    protected Node ConstructBombAreaTree() {
        Node findBombAreaTarget = new Selector("[+] FindArea");
        //findBombAreaTarget.AttachChild(new HasTargetPoint());
        findBombAreaTarget.AttachChild(new BasicFindBombArea(fireThreshold, boardState, boardState.GetPlayers().get(id)));

        Node root = new Sequencer("[>] AttackA");
        root.AttachChild(findBombAreaTarget);
        root.AttachChild(new MoveToTarget(new PlantBomb()));
        return root;
    }

}
