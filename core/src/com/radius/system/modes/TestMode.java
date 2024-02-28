package com.radius.system.modes;

import com.radius.system.configs.FieldConfig;
import com.radius.system.configs.PlayerConfig;

import java.util.List;

public class TestMode extends GameMode {
    public TestMode(FieldConfig fieldConfig, List<PlayerConfig> playerConfigs) {
        super(fieldConfig, playerConfigs);
    }

    @Override
    public boolean ContinueGame() {
        return true;
    }
}
