package com.radius.system.ai.pathfinding;

import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.Direction;

import java.util.Map;

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

    public boolean IsEqualPosition(Direction direction, float x, float y) {
        boolean xEqual = this.x == Math.round(x), yEqual = this.y == Math.round(y);
        System.out.println("Comparing: " + this.x + " == " + x + " && " + this.y + " == " + y + " ---> " + (xEqual && yEqual));
        switch (direction) {
            case NORTH:
                yEqual = Math.ceil(this.y - y) == 0;
                System.out.println("Processing NORTH: " + (this.y - y) + " ---> " + Math.ceil(this.y - y));
                break;
            case SOUTH:
                yEqual = Math.floor(this.y - y) == 0;
                System.out.println("Processing SOUTH: " + (this.y - y) + " ---> " + Math.floor(this.y - y));
                break;
            case WEST:
                xEqual = Math.floor(this.x - x) == 0;
                System.out.println("Processing WEST: " + (this.x - x) + " ---> " + Math.floor(this.x - x));
                break;
            case EAST:
                xEqual = Math.ceil(this.x - x) == 0;
                System.out.println("Processing EAST: " + (this.x - x) + " ---> " + Math.ceil(this.x - x));
                break;
        }
        //return Math.abs(this.x - x) < diffThreshold && Math.abs(this.y - y) < diffThreshold;
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
