package com.radius.system.events.parameters;

import com.radius.system.enums.ButtonType;

public class ButtonPressEvent extends AbstractEvent {

    public ButtonType buttonType;

    public ButtonPressEvent(int id) {
        super(id);
    }
}
