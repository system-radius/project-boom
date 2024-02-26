package com.radius.system.enums;

public enum BoardRep {

    PERMANENT_BLOCK('#'),

    HARD_BLOCK('x'),

    SOFT_BLOCK('@'),

    BOMB('o'),

    BONUS('*'),

    PLAYER('+'),

    EMPTY(' ');

    private char rep;

    BoardRep(char rep) {
        this.rep = rep;
    }

    public char GetRep() {
        return rep;
    }

    @Override
    public String toString() {
        return String.valueOf(rep);
    }

}
