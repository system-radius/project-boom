package com.radius.system.events.listeners;

import com.badlogic.gdx.graphics.Texture;
import com.radius.system.enums.GameType;

public interface ModeSelectListener {

    void OnModeSelected(int id, Texture texture, GameType mode);

}
