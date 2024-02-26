package com.radius.system.configs;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {

    private List<PlayerConfig> playerConfigs = new ArrayList<>();

    private FieldConfig fieldConfig = new FieldConfig();

    private boolean dirty;

    public boolean IsDirty() {
        return dirty;
    }

    public void SetupConfig() {
        
    }

}
