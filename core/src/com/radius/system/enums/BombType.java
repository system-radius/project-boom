package com.radius.system.enums;

public enum BombType {

    NORMAL(0),
    REMOTE(1),
    PIERCE(2),
    POWER(3);

    private final int type;

    BombType(int type) {
        this.type = type;
    }

    public int GetType() {
        return type;
    }
}
