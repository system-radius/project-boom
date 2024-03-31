package com.radius.system.ai.pathfinding;

public class Point{
    public int x, y;

    public float selfCost;

    public Point(PathFinderPoint pathFinderPoint) {
        this.x = pathFinderPoint.x;
        this.y = pathFinderPoint.y;
        this.selfCost = (int) pathFinderPoint.selfCost;
    }

    public Point() {

    }

    public boolean IsEqualPosition(Point that) {
        return this.x == that.x && this.y == that.y;
    }

    public boolean IsEqualPosition(float x, float y) {
        float diff = 0.1f;
        boolean xEqual = Math.abs(this.x - x) < diff;
        boolean yEqual = Math.abs(this.y - y) < diff;
        return xEqual && yEqual;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
