package com.radius.system.enums;

import java.awt.Button;

public enum ButtonType {
    A(0),
    B(1),
    PAUSE(2),
    PLAY(3),
    RESTART(4);

    private final int id;

    ButtonType(int id) {
        this.id = id;
    }

    public int GetID() {
        return id;
    }
}
