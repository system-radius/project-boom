package com.radius.system.events.parameters;

public class MovementEvent extends AbstractPlayerEvent {

    public float x, y;

    public MovementEvent(int id, float x, float y) {
        super(id);
        this.x = x;
        this.y = y;
    }

}
