package com.radius.system.events.parameters;

public abstract class AbstractEvent {

    public final int playerId;

    public AbstractEvent(int id) {
        playerId = id;
    }

}
