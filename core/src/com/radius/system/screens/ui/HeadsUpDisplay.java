package com.radius.system.screens.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.enums.BonusType;
import com.radius.system.objects.blocks.Bonus;

import java.util.HashMap;
import java.util.Map;

public class HeadsUpDisplay extends Group implements Disposable {

    private final Texture background;

    private final Map<BonusType, HeadsUpDisplayItem> items = new HashMap<>();

    public HeadsUpDisplay(float x, float y, float width, float height, float scale) {
        background = CreateTexture((int)width, (int)height);
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        float size = scale / 1.5f;

        items.put(BonusType.BOMB_STOCK, new HeadsUpDisplayItem(BonusType.BOMB_STOCK, scale, height / 2 - size / 2, size, size));
        items.put(BonusType.FIRE_POWER, new HeadsUpDisplayItem(BonusType.FIRE_POWER, scale * 4, height / 2 - size / 2, size, size));
        items.put(BonusType.MOVEMENT_SPEED, new HeadsUpDisplayItem(BonusType.MOVEMENT_SPEED, scale * 7, height / 2 - size / 2, size, size));

        for (BonusType type : items.keySet()) {
            addActor(items.get(type));
        }
    }

    private Texture CreateTexture(int width, int height) {
        Pixmap map = new Pixmap(width, height, Pixmap.Format.RGB888);
        map.setColor(Color.BLACK);
        map.fillRectangle(0, 0, width, height);

        Texture texture = new Texture(map);
        map.dispose();
        
        return texture;
    }

    public void SetValue(BonusType type, int value) {
        if (!items.containsKey(type)) {
            return;
        }

        items.get(type).SetValue(value);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(background, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1, 1, 1, 1);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void dispose() {
        background.dispose();
    }

}
