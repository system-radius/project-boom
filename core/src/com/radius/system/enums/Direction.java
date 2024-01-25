package com.radius.system.enums;

import com.badlogic.gdx.Input;

public enum Direction {

    NORTH (Input.Keys.W),

    SOUTH (Input.Keys.D),

    WEST (Input.Keys.A),

    EAST (Input.Keys.D);

    private final int key;

    Direction (int keyCode) {
        this.key = keyCode;
    }

    public int GetKeyCode() {
        return key;
    }
}
