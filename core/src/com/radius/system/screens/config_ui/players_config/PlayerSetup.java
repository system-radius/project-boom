package com.radius.system.screens.config_ui.players_config;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.configs.PlayerConfig;
import com.radius.system.events.listeners.PlayerConfigResetListener;
import com.radius.system.screens.config_ui.GamePanel;

import java.util.ArrayList;
import java.util.List;

public class PlayerSetup extends GamePanel implements PlayerConfigResetListener {

    private final List<PlayerConfigView> views = new ArrayList<>();

    private String prefPath;

    public PlayerSetup(float x, float y, float width, float height) {
        super(x, y, width, height);

        this.prefPath = prefPath;

        float itemWidth = GlobalConstants.WORLD_SCALE * 3;
        float itemHeight = GlobalConstants.WORLD_SCALE * 5;

        float yPos = height / 2 - itemHeight / 3;

        CreateConfigView(width / 2 - itemWidth * 2 - GlobalConstants.WORLD_SCALE * 1.5f, yPos, itemWidth, itemHeight);
        CreateConfigView(width / 2 - itemWidth - GlobalConstants.WORLD_SCALE * 0.5f, yPos, itemWidth, itemHeight);
        CreateConfigView(width / 2 + GlobalConstants.WORLD_SCALE * 0.5f, yPos, itemWidth, itemHeight);
        CreateConfigView(width / 2 + GlobalConstants.WORLD_SCALE * 4.5f, yPos, itemWidth, itemHeight);

        SetBGColor(Color.TEAL);
    }

    private void CreateConfigView(float x, float y, float width, float height) {
        PlayerConfigView view = new PlayerConfigView(views.size(), x, y, width, height);
        this.addActor(view);
        views.add(view);

        view.AddPlayerConfigResetListener(this);
    }

    public void SetPrefPath(String prefPath) {
        for (PlayerConfigView view : views) {
            view.SetPrefPath(prefPath);
        }
    }

    public List<PlayerConfig> GetPlayerConfigs() {
        List<PlayerConfig> configs = new ArrayList<>();
        for (PlayerConfigView view : views) {
            configs.add(view.GetPlayerConfig());
        }

        return configs;
    }

    @Override
    public void Resize() {

    }

    @Override
    public void OnPlayerConfigReset(int id) {
        for (Actor child : getChildren()) {
            if (!(child instanceof PlayerConfigView)) {
                continue;
            }

            PlayerConfigView configView = (PlayerConfigView) child;
            if (configView.GetID() == id) {
                continue;
            }
            configView.ResetControllerSelection();
        }
    }
}
