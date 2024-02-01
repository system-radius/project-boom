package com.radius.system.controllers;

import com.radius.system.events.ButtonEventListener;

public class ButtonPressTrigger implements ButtonEventListener {

    private final ButtonEventListener parent;

    private final int playerId;

    private final int buttonId;

    public ButtonPressTrigger(int id, int buttonId, ButtonEventListener parent) {
        this.playerId = id;
        this.buttonId = buttonId;
        this.parent = parent;
    }

    @Override
    public void OnButtonPress(int id) {
        if (this.playerId != id) return;
        parent.OnButtonPress(buttonId);
    }
}
