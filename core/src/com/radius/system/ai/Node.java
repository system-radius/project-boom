package com.radius.system.ai;

import com.radius.system.assets.GlobalConstants;

public class Node implements Comparable<Node> {

    public int x, y;

    public float h, g;

    private Node parent;

    public Node(Node parent, int x, int y, float g, float h) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
    }

    public Node GetParent() {
        return parent;
    }

    public float GetCost() {
        return h + g;
    }

    public boolean IsInvalid() {
        return x < 0 || y < 0 || x >= GlobalConstants.WORLD_WIDTH || y >= GlobalConstants.WORLD_HEIGHT;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public int compareTo(Node that) {
        return Float.compare(this.GetCost(), that.GetCost());
    }
}
