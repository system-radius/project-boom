package com.radius.system.screens.config_ui.players_config;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.configs.PlayerConfig;
import com.radius.system.enums.BotLevel;
import com.radius.system.enums.ButtonType;
import com.radius.system.enums.ControlType;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.listeners.PlayerConfigResetListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.screens.config_ui.GamePanel;
import com.radius.system.screens.config_ui.TextGameButton;
import com.radius.system.screens.game_ui.buttons.GameButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerConfigView extends GamePanel implements ButtonPressListener {

    private static final String PLAYER_DISPLAY = "/playerDisplay", CONTROLLER_TYPE = "/controller", BOT_LEVEL = "/botLevel";

    private final TextureRegionDrawable[] displays;

    private final String[] playerTexturePaths;

    private final ControlType[] controllers = new ControlType[]{ ControlType.AI, ControlType.HUMAN, ControlType.OFF};

    private final BotLevel[] botLevels = new BotLevel[]{
            BotLevel.A_CLASS, BotLevel.B_CLASS, BotLevel.C_CLASS, BotLevel.D_CLASS
    };

    private final Color[] controllerColors = new Color[] {
            new Color(0xff6363ff), new Color(0x63ff63ff), new Color(0x888888ff)
    };

    private final Color[] botColors = new Color[] {
            new Color(0x101040ff), new Color(0x401040ff), new Color(0x404010ff), new Color(0x104010ff)
    };

    private final List<PlayerConfigResetListener> playerConfigResetListeners = new ArrayList<>();

    private final Map<String, TextureRegion[][]> textureRegionMapping = new HashMap<>();

    private final Preferences prefs;

    private final PlayerConfig playerConfig;

    private final int id;

    private String prefPath;

    private GameButton mainDisplay;

    private TextGameButton controllerSelect, botLevel;

    private int selectedMainDisplay = 0, selectedController = 0, selectedBotLevel = 0;

    private boolean off = false, notAI = false;

    public PlayerConfigView(int id, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.id = id;
        playerConfig = new PlayerConfig();
        this.prefs = Gdx.app.getPreferences(GlobalConstants.APP_NAME);

        FileHandle playerDirectory = LoadPlayerDirectory();
        FileHandle[] playerTextures = playerDirectory.list();
        int size = playerTextures.length;
        displays = new TextureRegionDrawable[size];
        playerTexturePaths = new String[size];

        LoadPlayerTextures(playerTextures, playerDirectory);

        float adjustment = 4f;

        this.addActor(mainDisplay = new GameButton(ButtonType.PLAY, 0, height / (adjustment - 1f), width, height - height / (adjustment - 1.5f)));
        this.addActor(controllerSelect = CreateTextButton(GlobalAssets.WHITE_SQUARE,  ButtonType.RESTART, 0,0, width / 2, height / adjustment));
        this.addActor(botLevel = CreateTextButton(GlobalAssets.WHITE_SQUARE, ButtonType.CANCEL, width / 2, 0, width / 2, height / adjustment));

        mainDisplay.AddListener(this);

    }

    private void UpdateDisplay() {
        off = ControlType.OFF.equals(controllers[selectedController]);
        mainDisplay.setDrawable(displays[selectedMainDisplay]);
        mainDisplay.setColor(off ? Color.BLACK : Color.WHITE);

        controllerSelect.setColor(controllerColors[selectedController]);
        controllerSelect.SetText(controllers[selectedController].toString());

        notAI = !ControlType.AI.equals(controllers[selectedController]);
        botLevel.setColor(notAI ? Color.DARK_GRAY : botColors[selectedBotLevel]);
        botLevel.SetText(botLevels[selectedBotLevel].GetKey());

        prefs.putInteger(prefPath + PLAYER_DISPLAY, selectedMainDisplay);
        prefs.putInteger(prefPath + CONTROLLER_TYPE, selectedController);
        prefs.putInteger(prefPath + BOT_LEVEL, selectedBotLevel);
        prefs.flush();
    }

    private TextGameButton CreateTextButton(String texturePath, ButtonType type, float x, float y, float width, float height) {
        TextureRegion texture = new TextureRegion(GlobalAssets.LoadTexture(texturePath));
        TextGameButton button = new TextGameButton(texture, type, x, y, width, height);

        button.AddListener(this);
        return button;
    }

    private FileHandle LoadPlayerDirectory() {

        FileHandle fieldDirectory;
        if (Application.ApplicationType.Android.equals(Gdx.app.getType())) {
            fieldDirectory = Gdx.files.internal("/players");
        } else {
            fieldDirectory = Gdx.files.internal("./assets/players");
        }

        return fieldDirectory;
    }

    private void LoadPlayerTextures(FileHandle[] playerTextureHandles, FileHandle playerDirectory) {
        for (int i = 0; i < playerTextureHandles.length; i++) {
            FileHandle playerTextureHandle = playerTextureHandles[i];
            String path = playerDirectory.name() + "/" + playerTextureHandle.name();
            playerTexturePaths[i] = path;
            TextureRegion[][] playerTexture = GlobalAssets.LoadTextureRegion(path, 32, 32);
            displays[i] = new TextureRegionDrawable(playerTexture[0][0]);
            textureRegionMapping.put(path, playerTexture);
        }
    }

    public void AddPlayerConfigResetListener(PlayerConfigResetListener listener) {
        if (playerConfigResetListeners.contains(listener)) return;
        playerConfigResetListeners.add(listener);
    }

    public void SetPrefPath(String parentPrefPath) {
        this.prefPath = parentPrefPath + id;
        selectedMainDisplay = prefs.getInteger(prefPath + PLAYER_DISPLAY, 0);
        selectedController = prefs.getInteger(prefPath + CONTROLLER_TYPE, 0 );
        selectedBotLevel = prefs.getInteger(prefPath + BOT_LEVEL, 0);
        UpdateDisplay();
    }

    public int GetID() {
        return id;
    }

    public void ResetControllerSelection() {
        selectedController = selectedController == 1 ? 0 : selectedController;
        UpdateDisplay();
    }

    public PlayerConfig GetPlayerConfig() {
        playerConfig.id = id;
        playerConfig.controlType = controllers[selectedController];
        playerConfig.texturePath = playerTexturePaths[selectedMainDisplay];
        playerConfig.botLevel = botLevels[selectedBotLevel];

        return playerConfig;
    }

    @Override
    public void Resize() {

    }

    @Override
    public void OnButtonPress(ButtonPressEvent event) {

        switch (event.buttonType) {
            case PLAY:
                if (!off) {
                    selectedMainDisplay = selectedMainDisplay + 1 == displays.length ? 0 : selectedMainDisplay + 1;
                }
                break;
            case RESTART:
                selectedController = selectedController + 1 == controllers.length ? 0 : selectedController + 1;
                if (selectedController == 1) {
                    FirePlayerConfigReset();
                }
                break;
            case CANCEL:
                if (!off && (selectedController == 0)) {
                    selectedBotLevel = selectedBotLevel + 1 == botLevels.length ? 0 : selectedBotLevel + 1;
                }
                break;
        }

        UpdateDisplay();

    }

    private void FirePlayerConfigReset() {
        for (PlayerConfigResetListener listener : playerConfigResetListeners) {
            listener.OnPlayerConfigReset(id);
        }
    }
}
