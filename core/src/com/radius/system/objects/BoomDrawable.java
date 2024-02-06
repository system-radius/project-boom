package com.radius.system.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface BoomDrawable {

    void Draw(Batch batch);

    void DrawDebug(ShapeRenderer renderer);

}
