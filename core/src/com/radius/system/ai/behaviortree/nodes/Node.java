package com.radius.system.ai.behaviortree.nodes;

import com.radius.system.enums.NodeState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Node {

    private final Map<String, Object> data = new HashMap<>();

    public Node parent = null;

    protected NodeState state;

    protected List<Node> children = new ArrayList<>();

    public Node() {
        this(new ArrayList<>());
    }

    public Node(List<Node> children) {
        for (Node child : children) {
            AttachChild(child);
        }
    }

    public void Restart() {
        data.clear();
    }

    public void AttachChild(Node child) {
        child.parent = this;
        children.add(child);
    }

    public abstract NodeState Evaluate(int depth, float delta, int[][] boardCost);

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
}
