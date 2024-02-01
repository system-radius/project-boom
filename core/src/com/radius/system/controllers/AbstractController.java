package com.radius.system.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.radius.system.objects.players.Player;

public abstract class AbstractController {

    protected final Player player;

    public AbstractController(Player player) {
        this.player = player;
    }

    public abstract void Update(float delta);

    public abstract void Draw(Batch batch);

}
