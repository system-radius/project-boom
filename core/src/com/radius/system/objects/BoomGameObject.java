package com.radius.system.objects;

import com.badlogic.gdx.utils.Disposable;

public abstract class BoomGameObject extends GameObject implements Disposable, Burnable {

    protected char charRep;

    protected float animationElapsedTime;

    protected int life;

    public BoomGameObject(char charRep, float x, float y) {
        super(x, y);
        this.charRep = charRep;
    }

    public char GetCharRep() {
        return charRep;
    }

}
