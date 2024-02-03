package com.radius.system.objects;

import com.badlogic.gdx.utils.Disposable;
import com.radius.system.enums.BoardRep;

public abstract class BoomGameObject extends GameObject implements Disposable, Burnable {

    protected BoardRep rep;

    protected float animationElapsedTime;

    protected int life;

    public BoomGameObject(BoardRep rep, float x, float y) {
        super(x, y);
        this.rep = rep;
    }

    public BoardRep GetRep() {
        return rep;
    }

}
