package com.radius.system.ai.pathfinding;

import com.radius.system.assets.GlobalConstants;

public class Point implements Comparable<Point> {

    private final static float diffThreshold = 0.015f;

    public int x, y;

    public float h, parentCost, selfCost;

    private final Point parent;

    public Point(Point parent, int x, int y) {
        this(parent, x, y, 0, 0, 0);
    }

    public Point(Point parent, int x, int y, float selfCost, float g, float h) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.selfCost = selfCost;
        this.parentCost = g;
        this.h = h;
    }

    public Point GetParent() {
        return parent;
    }

    public float GetCost() {
        return h + parentCost + selfCost;
    }

    public boolean IsInvalid() {
        return x < 0 || y < 0 || x >= GlobalConstants.WORLD_WIDTH || y >= GlobalConstants.WORLD_HEIGHT;
    }

    public boolean IsEqualPosition(Point that) {
        return this.x == that.x && this.y == that.y;
    }

    public boolean IsEqualPosition(float x, float y) {
        float diff = 0.05f;
        boolean xEqual = Math.abs(this.x - x) < diff;
        boolean yEqual = Math.abs(this.y - y) < diff;
        return xEqual && yEqual;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public int compareTo(Point that) {
        return Float.compare(this.GetCost(), that.GetCost());
    }
}
