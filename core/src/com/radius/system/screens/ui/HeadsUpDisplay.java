package com.radius.system.screens.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.enums.BombType;
import com.radius.system.enums.BonusType;
import com.radius.system.objects.blocks.Bonus;
import com.radius.system.objects.bombs.Bomb;

import java.util.HashMap;
import java.util.Map;

public class HeadsUpDisplay extends Group implements Disposable {

    private final Texture background;

    private final Map<BonusType, HeadsUpDisplayItem> items = new HashMap<>();

    private final Map<BombType, BombTypeDisplay> bombs = new HashMap<>();

    private BombType bombType = BombType.NORMAL;

    public HeadsUpDisplay(float x, float y, float width, float height, float scale) {
        background = CreateTexture((int)width, (int)height);
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        float size = scale / 1.5f;
        float positionY = height / 2 - size / 2;

        items.put(BonusType.BOMB_STOCK, new HeadsUpDisplayItem(BonusType.BOMB_STOCK, scale, positionY, size, size).EnableCount());
        items.put(BonusType.FIRE_POWER, new HeadsUpDisplayItem(BonusType.FIRE_POWER, scale * 4, positionY, size, size).EnableCount());
        items.put(BonusType.MOVEMENT_SPEED, new HeadsUpDisplayItem(BonusType.MOVEMENT_SPEED, scale * 7, positionY, size, size).EnableCount());
        //items.put(BonusType.EMPTY, new HeadsUpDisplayItem(BonusType.EMPTY, scale * 10, positionY, size, size).DisableCount());

        bombs.put(BombType.NORMAL, new BombTypeDisplay(BombType.NORMAL, scale * 10, positionY, size, size));
        bombs.put(BombType.REMOTE, new BombTypeDisplay(BombType.REMOTE, scale * 10, positionY, size, size));
        bombs.put(BombType.PIERCE, new BombTypeDisplay(BombType.PIERCE, scale * 10, positionY, size, size));
        bombs.put(BombType.IMPACT, new BombTypeDisplay(BombType.IMPACT, scale * 10, positionY, size, size));

        bombs.get(bombType).Enable();

        AddItemsAsActors(items);
        AddBombsAsActors(bombs);
    }

    private void AddItemsAsActors(Map<BonusType, HeadsUpDisplayItem> map) {
        for (BonusType type : map.keySet()) {
            addActor(map.get(type));
        }
    }

    private void AddBombsAsActors(Map<BombType, BombTypeDisplay> map) {
        for (BombType type : map.keySet()) {
            addActor(map.get(type));
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

    public void SetBombType(BombType newBombType) {
        if (!bombs.containsKey(newBombType)) {
            return;
        }

        bombs.get(this.bombType).Disable();
        bombs.get(newBombType).Enable();

        this.bombType = newBombType;
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
