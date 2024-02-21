package com.radius.system.enums;

public enum BonusType {

    LIFE(0),

    BOMB_STOCK(1),

    FIRE_POWER(2),

    FLASH_FIRE(3),

    MOVEMENT_SPEED(4),

    REMOTE_MINE(5),

    PIERCE_BOMB(6),

    IMPACT_BOMB(7),

    EMPTY(-1);

    private int type;

    BonusType(int index) {
        this.type = index;
    }

    public int GetType() {
        return type;
    }

}
