package com.radius.system.events;

import com.radius.system.enums.BonusType;

public interface StatChangeListener {

    void OnStatChange(BonusType type, int value);

}
