package com.radius.system.enums;

public enum ControlKeys {

    NORTH,
    SOUTH,
    EAST,
    WEST,
    BOMB;

    private ControlKeys oppositeKey;

    static {
        NORTH.SetOpposite(SOUTH);
        SOUTH.SetOpposite(NORTH);
        EAST.SetOpposite(WEST);
        WEST.SetOpposite(EAST);
    }

    void SetOpposite(ControlKeys key) {
        this.oppositeKey = key;
    }

    public ControlKeys GetOppositeKey() {
        return oppositeKey;
    }

}
