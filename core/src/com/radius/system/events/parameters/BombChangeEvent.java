package com.radius.system.events.parameters;

import com.radius.system.enums.BombType;

public class BombChangeEvent extends AbstractEvent {

    public BombType bomb;

    public BombChangeEvent(int id) {
        super(id);
    }
}
