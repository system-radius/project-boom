package com.radius.system.events.parameters;

public abstract class AbstractPlayerEvent {

    public final int playerId;

    public AbstractPlayerEvent(int id) {
        playerId = id;
    }

}
