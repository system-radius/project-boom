package com.radius.system.enums;

public enum ControlKeys {

    NORTH,
    SOUTH,
    EAST,
    WEST,
    BOMB,

    DETONATE;

    private ControlKeys oppositeKey;

    static {
        NORTH.SetOpposite(SOUTH);
        SOUTH.SetOpposite(NORTH);
        EAST.SetOpposite(WEST);
        WEST.SetOpposite(EAST);
        BOMB.SetOpposite(null);
        DETONATE.SetOpposite(null);
    }

    void SetOpposite(ControlKeys key) {
        this.oppositeKey = key;
    }

    public ControlKeys GetOppositeKey() {
        return oppositeKey;
    }

}
