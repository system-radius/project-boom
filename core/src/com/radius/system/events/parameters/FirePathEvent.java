package com.radius.system.events.parameters;

public class FirePathEvent extends AbstractPlayerEvent{

    public boolean onFirePath;

    public boolean hasNorth, hasSouth, hasEast, hasWest;

    public FirePathEvent(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "[FirePathEvent] hasNorth: " + hasNorth + ", hasSouth: " + hasSouth + ", hasEast: " + hasEast + ", hasWest: " + hasWest;
    }
}
