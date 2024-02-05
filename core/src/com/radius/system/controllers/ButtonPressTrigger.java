package com.radius.system.controllers;

import com.radius.system.enums.ButtonType;
import com.radius.system.events.ButtonEventListener;

public class ButtonPressTrigger implements ButtonEventListener {

    private final ButtonEventListener parent;

    private final int playerId;

    private final ButtonType buttonType;

    public ButtonPressTrigger(int id, ButtonType buttonId, ButtonEventListener parent) {
        this.playerId = id;
        this.buttonType = buttonId;
        this.parent = parent;
    }

    @Override
    public void OnButtonPress(int id) {
        if (this.playerId != id) return;
        parent.OnButtonPress(buttonType.GetID());
    }
}
