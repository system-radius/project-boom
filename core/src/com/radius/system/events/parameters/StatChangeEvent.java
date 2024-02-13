package com.radius.system.events.parameters;

import com.radius.system.enums.BonusType;

public class StatChangeEvent extends AbstractPlayerEvent {

    public BonusType bonusType;

    public int value;

    public StatChangeEvent(int id) {
        super(id);
    }
}
