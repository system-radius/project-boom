package com.radius.system.screens.config_ui.mode_select;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.GameType;
import com.radius.system.events.listeners.ModeSelectListener;
import com.radius.system.screens.config_ui.GamePanel;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectionPanel extends GamePanel {

    private static final GameType[] GAME_MODES = new GameType[]{
            GameType.TEST,
            GameType.CLASSIC,
            GameType.DEATH_MATCH,
            GameType.CAPTURE_THE_FLAG
    };

    private final List<ModeSelectionItem> modes = new ArrayList<>();

    public ModeSelectionPanel(float x, float y, float width, float height) {
        super(x, y, width, height);

        float itemWidth = GlobalConstants.WORLD_SCALE * 4f, itemHeight = GlobalConstants.WORLD_SCALE * 2f;
        float itemPosX = width / 2 - itemWidth / 2;

        for (GameType mode : GAME_MODES) {
            this.addActor(CreateItem(itemPosX, 0, itemWidth, itemHeight, mode));
        }
    }

    @Override
    public void Resize() {

        float height = getHeight();

        for (int i = 0; i < getChildren().size; i++) {
            Actor child = getChild(i);
            float itemPosY = height - ((child.getHeight() + GlobalConstants.WORLD_SCALE / 3f) * (i + 1));
            child.setPosition(child.getX(), itemPosY);
        }
    }

    private ModeSelectionItem CreateItem(float x, float y, float width, float height, GameType mode) {

        ModeSelectionItem item = new ModeSelectionItem(modes.size(), x, y, width, height, mode);
        modes.add(item);
        return item;
    }

    public void AddSelectionListener(ModeSelectListener listener) {
        if (modes.size() == 0) {
            return;
        }

        for (ModeSelectionItem mode : modes) {
            mode.AddSelectionListener(listener);
        }
    }
}
