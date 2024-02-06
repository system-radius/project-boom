package com.radius.system.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.radius.system.enums.BoardRep;

public abstract class AnimatedGameObject extends BoomGameObject {

    protected float animationElapsedTime;

    protected Animation<TextureRegion> activeAnimation;

    protected int life;

    public AnimatedGameObject(BoardRep rep, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.rep = rep;
    }

    public BoardRep GetRep() {
        return rep;
    }

    @Override
    public void Update(float delta) {
        animationElapsedTime += delta;
    }

    @Override
    public void Draw(Batch batch) {
        batch.draw(activeAnimation.getKeyFrame(animationElapsedTime), scaledPosition.x, scaledPosition.y, size.x, size.y);
    }

}
