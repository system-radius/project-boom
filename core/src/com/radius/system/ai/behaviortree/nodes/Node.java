package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.pathfinding.Point;
import com.radius.system.enums.NodeState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Node implements Comparable<Node> {

    private static final String FAILURE = ": FAILURE", SUCCESS = ": SUCCESS", RUNNING = ": RUNNING";

    private final Map<String, Object> data = new HashMap<>();

    public Node parent = null;

    protected NodeState state = NodeState.FAILURE;

    protected String id, displayId;

    protected List<Node> children = new ArrayList<>();

    protected int weight ;

    public Node() {
    }

    public Node(String id, Node... children) {
        this.id = id;
        for (Node child : children) {
            AttachChild(child);
        }
    }

    public List<Node> GetChildren() {
        return children;
    }

    public NodeState GetState() {
        return state;
    }

    public int ComputeWeight() {
        int childrenWeight = 0;

        for (Node child : children) {
            // Only the success/running node states are to be considered.
            if (NodeState.FAILURE.equals(child.state)) continue;
            childrenWeight += child.ComputeWeight();
        }

        return weight + childrenWeight;
    }

    public void Restart() {

        for (Node child : children) {
            child.Restart();
        }

        data.clear();
    }

    public void AttachChild(Node child) {
        child.parent = this;
        child.RefreshId(id);
        children.add(child);
    }

    public void RefreshId(String parentId) {
        this.displayId = parentId + " -> " + this.id;
        for (Node child : children) {
            child.RefreshId(this.displayId);
        }
    }

    public String GetDisplayId() {
        return displayId;
    }

    public abstract NodeState Evaluate(Point srcPoint, int[][] boardCost);

    public abstract void Execute();

    public void SetData(String key, Object value) {
        data.put(key, value);
    }

    public Node GetRoot() {
        return GetParent(-1);
    }

    public Node GetParent(int order) {

        if (order == 0) {
            return parent;
        }

        if (order < 0 && parent == null) {
            return this;
        }

        return parent.GetParent(order - 1);
    }

    public final Object GetData(String key, boolean traverse) {
        if (data.containsKey(key)) {
            return data.get(key);
        }

        if (traverse) {
            Node node = parent;

            while (node != null) {
                Object value = node.GetData(key);
                if (value != null) {
                    return value;
                }

                node = node.parent;
            }
        }

        return null;
    }

    public final Object GetData(String key) {
        return GetData(key, true);
    }

    public final boolean ClearData(String key) {

        if (data.containsKey(key)) {
            data.remove(key);
            return true;
        }

        Node node = parent;
        while (node != null) {
            boolean cleared = node.ClearData(key);
            if (cleared) {
                return true;
            }

            node = node.parent;
        }

        return false;
    }

    public final boolean ClearFullData(String key) {
        Node root = GetRoot();
        return root.ClearDataFromSelf(key) || root.ClearDataFromChildren(key);
    }

    private boolean ClearDataFromSelf(String key) {
        if (data.containsKey(key)) {
            data.remove(key);
            return true;
        }

        return false;
    }

    private boolean ClearDataFromChildren(String key) {
        boolean clearedData = false;
        for (Node child : children) {
            clearedData = clearedData || child.ClearDataFromSelf(key) || child.ClearDataFromChildren(key);
        }

        return clearedData;
    }

    public NodeState Failure() {
        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + FAILURE);
        return state = NodeState.FAILURE;
    }

    public NodeState Success(int weight, Point targetPoint) {
        SetTargetPoint(targetPoint);
        return Success(weight);
    }

    public NodeState Success(int weight) {
        this.weight = weight;
        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + SUCCESS);
        return state = NodeState.SUCCESS;
    }

    public NodeState Running(int weight) {
        this.weight = weight;
        GetRoot().SetData(NodeKeys.ACTIVE_NODE, displayId + RUNNING);
        return state = NodeState.RUNNING;
    }

    public void SetTargetPoint(Point targetPoint) {
        Node root = GetRoot();

        root.SetData(NodeKeys.TARGET_POINT, targetPoint);
        root.SetData(NodeKeys.TARGET_SETTER, displayId);
    }

    @Override
    public int compareTo(Node that) {
        return Long.compare(this.ComputeWeight(), that.ComputeWeight());
    }
}
