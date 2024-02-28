package com.radius.system.enums;

public enum ConfigState {

    MODE_SELECT("MODE SELECT"),

    MAP_SELECT("MAP SELECT"),

    PLAYERS_CONFIG("PLAYERS CONFIG");

    private final String text;

    ConfigState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
