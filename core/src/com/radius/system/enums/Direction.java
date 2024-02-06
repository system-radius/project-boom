package com.radius.system.enums;

public enum Direction {

    SOUTH (0),

    NORTH (1),

    WEST (2),

    EAST (3),

    DEAD (4);

    private final int index;

    Direction (int index) {
        this.index = index;
    }

    public int GetIndex() {
        return index;
    }
}
