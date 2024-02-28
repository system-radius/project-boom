package com.radius.system.enums;

public enum BoardRep {

    PERMANENT_BLOCK('#'),

    HARD_BLOCK('x'),

    SOFT_BLOCK('@'),

    BOMB('o'),

    BONUS('*'),

    PLAYER('+'),

    PLAYER_1('1'),

    PLAYER_2('2'),

    PLAYER_3('3'),

    PLAYER_4('4'),

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
