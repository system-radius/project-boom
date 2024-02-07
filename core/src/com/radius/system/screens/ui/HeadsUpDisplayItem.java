package com.radius.system.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.radius.system.enums.BonusType;
import com.radius.system.objects.blocks.Block;
import com.radius.system.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;

public class HeadsUpDisplayItem extends Actor implements Disposable {

    public static final Texture SYMBOLS_TEXTURE = new Texture(Gdx.files.internal("img/Lettering.png"));

    protected static final TextureRegion[][] SYMBOLS = TextureRegion.split(SYMBOLS_TEXTURE, 20, 20);

    protected static final TextureRegion[] ICONS = TextureRegion.split(Block.BLOCKS_SPRITE_SHEET, 32, 32)[7];

    private final List<TextureRegion> rendering = new ArrayList<>();

    private final TextureRegion icon, colon;

    private TextureRegion tens, ones;

    private BonusType type;

    public HeadsUpDisplayItem(BonusType type, float x, float y, float width, float height) {
        this.type = type;
        this.icon = ICONS[type.GetType()];
        this.colon = SYMBOLS[5][0];
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        SetValue(0);

    }

    private void DeriveDisplay(int value) {
        int onesValue = value % 10;
        int tensValue = value / 10;

        ones = SYMBOLS[0][onesValue];
        tens = SYMBOLS[0][tensValue];
        System.out.println("Type[" + type.toString() + "]: (" + value + ") -> " + tensValue + " " + onesValue);
    }

    public void SetValue(int value) {
        DeriveDisplay(value);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        /*
        batch.draw(icon, getX(), getY(), getWidth(), getHeight());
        batch.draw(tens, getX() + getWidth(), getY(), getWidth(), getHeight());
        batch.draw(ones, getX() + getWidth() * 2, getY(), getWidth(), getHeight());
        */
        batch.draw(icon, getX(), getY(), getWidth(), getHeight());
        DrawItem(batch, colon, 1);
        DrawItem(batch, tens, 2);
        DrawItem(batch, ones, 3);
    }

    private void DrawItem(Batch batch, TextureRegion texture, int index) {
        batch.draw(texture, getX() + (getWidth() * index) / 1.5f, getY(), getWidth(), getHeight());
    }

    @Override
    public void dispose() {

    }
}
