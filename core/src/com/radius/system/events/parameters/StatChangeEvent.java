package com.radius.system.events.parameters;

import com.radius.system.enums.BonusType;
import com.radius.system.objects.blocks.Bonus;

public class StatChangeEvent extends AbstractEvent {

    public BonusType bonus;

    public int value;

    public StatChangeEvent(int id) {
        super(id);
    }
}
