package com.radius.system.configs;

import com.radius.system.board.BoardState;
import com.radius.system.enums.GameType;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {

    private GameType gameType = GameType.CLASSIC;

    private List<PlayerConfig> playerConfigs = new ArrayList<>();

    private FieldConfig fieldConfig = new FieldConfig();

    public void SetGameMode(GameType gameType) {
        this.gameType = gameType;
    }

    public GameType GetGameMode() {
        return gameType;
    }

    public void SetField(String texturePath) {
        fieldConfig.LoadField(texturePath);
    }

    public FieldConfig GetFieldConfig() {
        return fieldConfig;
    }

    public void AddPlayerConfigs(List<PlayerConfig> configs) {
        playerConfigs = configs;
    }

    public List<PlayerConfig> GetPlayerConfigs() {
        return playerConfigs;
    }

}
