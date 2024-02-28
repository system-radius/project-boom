package com.radius.system.screens.config_ui.mode_select;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.GameType;
import com.radius.system.events.listeners.ModeSelectListener;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectionItem extends Image {

    private final List<ModeSelectListener> listeners = new ArrayList<>();

    private final GameType mode;

    private final Texture texture;

    private final int id;

    public ModeSelectionItem(int id, float x, float y, float width, float height, GameType mode) {
        setX(x); setY(y); setWidth(width); setHeight(height);

        this.texture = GlobalAssets.LoadTexture(mode.GetAssetPath());
        this.id = id;
        this.mode = mode;

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FireOnSelectEvent();
            }
        });
        setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public void AddSelectionListener(ModeSelectListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void FireOnSelectEvent() {
        for (ModeSelectListener listener : listeners) {
            listener.OnModeSelected(id, texture, mode);
        }
    }

}
