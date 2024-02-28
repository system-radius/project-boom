package com.radius.system.screens.config_ui.map_select;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.radius.system.enums.ButtonType;
import com.radius.system.events.parameters.ButtonPressEvent;

public class MapSelectionItem extends Image {

    public MapSelectionItem(TextureRegion texture, float x, float y, float width, float height) {
        super(texture);
        setX(x); setY(y); setWidth(width); setHeight(height);
    }

}
