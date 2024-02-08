package com.radius.system.enums;

public enum BonusType {

    BOMB_STOCK(0),

    FIRE_POWER(1),

    FLASH_FIRE(2),

    MOVEMENT_SPEED(3),

    REMOTE_MINE(4),

    PIERCE_BOMB(5),

    IMPACT_BOMB(6),

    EMPTY(7);

    private int type;

    BonusType(int index) {
        this.type = index;
    }

    public int GetType() {
        return type;
    }

}
