package com.radius.system.enums;

public enum BotLevel {

    OMEGA("Omega"),

    S_CLASS("S"),

    A_CLASS("A"),

    B_CLASS("B"),

    C_CLASS("C"),

    D_CLASS("D");

    private final String key;

    BotLevel(String key) {
        this.key = key;
    }

    public String GetKey() {
        return key;
    }

}
