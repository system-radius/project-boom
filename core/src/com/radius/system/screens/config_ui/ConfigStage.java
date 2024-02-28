package com.radius.system.screens.config_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.configs.GameConfig;
import com.radius.system.enums.ButtonType;
import com.radius.system.enums.ConfigState;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.StartGameListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.screens.config_ui.map_select.MapSelection;
import com.radius.system.screens.config_ui.mode_select.ModeSelection;
import com.radius.system.screens.config_ui.players_config.PlayerSetup;

import java.util.ArrayList;
import java.util.List;

public class ConfigStage extends Stage implements ButtonPressListener {

    private static final ConfigState[] STATES = new ConfigState[] {
            ConfigState.MODE_SELECT,
            ConfigState.MAP_SELECT,
            ConfigState.PLAYERS_CONFIG
    };

    private final float WORLD_SCALE;

    private final List<StartGameListener> startGameListeners = new ArrayList<>();

    private final GameConfigHeader configHeader;

    private final ModeSelection modeSelection;

    private final MapSelection mapSelection;

    private final PlayerSetup playerSetup;

    private final TextGameButton nextButton, backButton;

    private final GameConfig gameConfig;

    private ConfigState state;

    private int stateCounter = 0;

    public ConfigStage(Viewport viewport, float scale) {
        super(viewport);

        gameConfig = new GameConfig();

        WORLD_SCALE = scale;

        nextButton = CreateTextButton(GlobalAssets.WHITE_SQUARE, ButtonType.A, 0, 0, WORLD_SCALE * 1.5f, WORLD_SCALE * 0.75f, 1, "NEXT");
        backButton = CreateTextButton(GlobalAssets.WHITE_SQUARE, ButtonType.B, 0, 0, WORLD_SCALE * 1.5f, WORLD_SCALE * 0.75f, 1, "BACK");

        float viewportWidth = viewport.getWorldWidth();
        float viewportHeight = viewport.getWorldHeight();
        float adjustedHeight = viewportHeight / 9f;

        modeSelection = new ModeSelection(0, 0, viewportWidth, viewportHeight - adjustedHeight);
        mapSelection = new MapSelection(0, 0, viewportWidth, viewportHeight - adjustedHeight);
        playerSetup = new PlayerSetup(0, 0, viewportWidth, viewportHeight - adjustedHeight);
        modeSelection.SetBGColor(Color.RED);
        this.addActor(modeSelection);
        this.addActor(mapSelection);
        this.addActor(playerSetup);

        configHeader = new GameConfigHeader(0, 0, viewportWidth, adjustedHeight);
        this.addActor(configHeader);

        this.addActor(backButton);
        this.addActor(nextButton);

        state = ConfigState.PLAYERS_CONFIG;
        AdjustView();
    }

    private TextGameButton CreateTextButton(String texturePath, ButtonType type, float x, float y, float width, float height, float alpha, String text) {
        TextureRegion texture = new TextureRegion(GlobalAssets.LoadTexture(texturePath));
        TextGameButton button = new TextGameButton(texture, type, x, y, width, height);
        button.SetText(text);
        button.getColor().a = alpha;
        button.AddListener(this);
        return button;
    }

    private void AdvanceState() {
        if (stateCounter + 1 >= STATES.length) {
            // Compile the necessary configs here.
            gameConfig.SetGameMode(modeSelection.GetActiveGameMode());
            gameConfig.SetField(mapSelection.GetSelectedMap());
            gameConfig.AddPlayerConfigs(playerSetup.GetPlayerConfigs());
            FireStartGame();
            return;
        }
        stateCounter++;
    }

    private void RollbackState() {
        stateCounter = stateCounter == 0 ? stateCounter : stateCounter - 1;
    }

    private void AdjustView() {
        state = STATES[stateCounter];
        backButton.setVisible(stateCounter > 0);
        nextButton.SetText(stateCounter == STATES.length - 1 ? "PLAY!" : "NEXT");

        configHeader.SetHeaderTitle(state.toString());
        modeSelection.setVisible(ConfigState.MODE_SELECT.equals(state));
        mapSelection.setVisible(ConfigState.MAP_SELECT.equals(state));
        playerSetup.setVisible(ConfigState.PLAYERS_CONFIG.equals(state));

        if (ConfigState.PLAYERS_CONFIG.equals(state)) {
            playerSetup.SetPrefPath(modeSelection.GetActiveModeTitle() + "/");
        }
    }

    public void ResetState() {
        stateCounter = 0;
        AdjustView();
    }

    public void Resize() {
        float viewportWidth = getViewport().getWorldWidth();
        float viewportHeight = getViewport().getWorldHeight();
        float adjustedHeight = viewportHeight - configHeader.getHeight();

        configHeader.setPosition(configHeader.getX(), adjustedHeight);
        modeSelection.setSize(viewportWidth, adjustedHeight);
        modeSelection.Resize();

        backButton.setPosition(WORLD_SCALE, WORLD_SCALE);
        nextButton.setPosition(viewportWidth - WORLD_SCALE * 2.5f, WORLD_SCALE);
    }

    public void AddStartGameListener(StartGameListener listener) {
        if (startGameListeners.contains(listener)) return;
        startGameListeners.add(listener);
    }

    private void FireStartGame() {
        for (StartGameListener listener : startGameListeners) {
            listener.OnGameStart(gameConfig);
        }
    }

    @Override
    public void OnButtonPress(ButtonPressEvent event) {
        switch (event.buttonType) {
            case A:
                AdvanceState();
                break;
            case B:
                RollbackState();
                break;
        }

        AdjustView();
    }
}
