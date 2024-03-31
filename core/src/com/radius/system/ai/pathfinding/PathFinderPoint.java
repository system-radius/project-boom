package com.radius.system.ai.pathfinding;

public class PathFinderPoint extends Point implements Comparable<PathFinderPoint> {

    public float h, parentCost;

    public PathFinderPoint parent;

    public PathFinderPoint(PathFinderPoint parent, int x, int y) {
        this(parent, x, y, 0, 0, 0);
    }

    public PathFinderPoint(PathFinderPoint parent, int x, int y, float selfCost, float g, float h) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.selfCost = selfCost;
        this.parentCost = g;
        this.h = h;
    }

    public PathFinderPoint GetParent() {
        return parent;
    }

    public float GetCost() {
        return h + parentCost + selfCost;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]: " + GetCost() + "---> " + selfCost;
    }

    @Override
    public int compareTo(PathFinderPoint that) {
        return Float.compare(this.GetCost(), that.GetCost());
    }
}
