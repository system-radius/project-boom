package com.radius.system.utils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public final class SegmentIntersector {

    private static final int INSIDE = 0x0000;
    public static final int WEST = 0x0001;
    public static final int EAST = 0x0010;
    public static final int SOUTH = 0x0100;
    public static final int NORTH = 0x1000;

    private static final Vector2 intersectingPoint = new Vector2();

    public static int HasIntersection(Rectangle intersector, Rectangle region) {

        boolean hasIntersection = UseCohenSutherlandLineClip(intersector.x, intersector.y, intersector.x + intersector.width, intersector.y + intersector.height, region);
        if (!hasIntersection) {
            hasIntersection = UseCohenSutherlandLineClip(intersector.x, intersector.y + intersector.height, intersector.x + intersector.width, intersector.y, region);
        }

        if (hasIntersection) {
            return GetClipCode(intersectingPoint.x, intersectingPoint.y, region);
        }

        return INSIDE;
    }

    public static boolean UseCohenSutherlandLineClip(float x1, float y1, float x2, float y2, Rectangle region) {

        int point1 = GetClipCode(x1, y1, region);
        int point2 = GetClipCode(x2, y2, region);

        float xMin = region.x;
        float xMax = region.x + region.width;
        float yMin = region.y;
        float yMax = region.y + region.height;

        intersectingPoint.x = 0;
        intersectingPoint.y = 0;

        while (true) {
            if (point1 == INSIDE) {
                intersectingPoint.x = x2;
                intersectingPoint.y = y2;
                return true;
            } else if (point2 == INSIDE) {
                intersectingPoint.x = x1;
                intersectingPoint.y = y1;
                return true;
            } else if ((point1 & point2) != 0) {
                return false;
            } else {
                float x = 0f;
                float y = 0f;

                int point = Math.max(point1, point2);

                if ((point & NORTH) != 0) {
                    x = x1 + (x2 - x1) / (y2 - y1) * (yMax - y1);
                    y = yMax;
                } else if ((point & SOUTH) != 0) {
                    x = x1 + (x2 - x1) / (y2 - y1) * (yMin - y1);
                    y = yMin;
                } else if ((point & EAST) != 0) {
                    x = xMax;
                    y = y1 + (y2 - y1) / (x2 - x1) * (xMax - x1);
                } else if ((point & WEST) != 0) {
                    x = xMin;
                    y = y1 + (y2 - y1) / (x2 - x1) * (xMin - x1);
                }

                if (point == point1) {
                    x1 = x; y1 = y;
                    point1 = GetClipCode(x1, y1, region);
                } else {
                    x2 = x; y2 = y;
                    point2 = GetClipCode(x2, y2, region);
                }
            }
        }
    }

    private static int GetClipCode(double x, double y, Rectangle region) {
        int code = INSIDE;
        if (x < region.x) {
            code |= WEST;
        } else if (x > region.x + region.width) {
            code |= EAST;
        }

        if (y < region.y) {
            code |= SOUTH;
        } else if (y > region.y + region.height) {
            code |= NORTH;
        }

        return code;
    }

}
