package com.radius.system.screens.config_ui.map_select;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.ButtonType;
import com.radius.system.events.listeners.ButtonPressListener;
import com.radius.system.events.parameters.ButtonPressEvent;
import com.radius.system.screens.config_ui.GamePanel;
import com.radius.system.screens.game_ui.buttons.GameButton;

public class MapSelection extends GamePanel implements ButtonPressListener {

    private static final String SELECTED_MAP = "config-selectedMap";

    private final Preferences prefs;

    private final String[] mapChoices;

    private final MapSelectionItem[] mapItems;

    private final GameButton backward, forward;

    private int selectedMap;

    public MapSelection(float x, float y, float width, float height) {
        super(x, y, width, height);

        prefs = Gdx.app.getPreferences(GlobalConstants.APP_NAME);

        FileHandle fieldDirectory = LoadFieldDirectory();
        FileHandle[] maps = fieldDirectory.list();
        int size = maps.length;
        mapChoices = new String[size];
        mapItems = new MapSelectionItem[size];

        LoadMapItems(maps, fieldDirectory);

        float buttonSizeMultiplier = 2f;
        float buttonWidth = GlobalConstants.WORLD_SCALE * buttonSizeMultiplier / 2;
        float buttonHeight = GlobalConstants.WORLD_SCALE * buttonSizeMultiplier;

        forward = CreateGameButton(GlobalAssets.FORWARD_TEXTURE_PATH, ButtonType.A, width * 0.75f - buttonWidth / 2, height / 2 - buttonHeight / 2, buttonWidth, buttonHeight);
        backward = CreateGameButton(GlobalAssets.BACKWARD_TEXTURE_PATH, ButtonType.B, width * 0.25f - buttonWidth / 2, height / 2 - buttonHeight / 2, buttonWidth, buttonHeight);

        this.addActor(backward);
        this.addActor(forward);

        SetBGColor(Color.RED);

        selectedMap = prefs.getInteger(SELECTED_MAP, 0);
        FixView();
    }

    private FileHandle LoadFieldDirectory() {

        FileHandle fieldDirectory;
        if (Application.ApplicationType.Android.equals(Gdx.app.getType())) {
            fieldDirectory = Gdx.files.internal("/field");
        } else {
            fieldDirectory = Gdx.files.internal("./assets/field");
        }

        return fieldDirectory;
    }

    private void LoadMapItems(FileHandle[] maps, FileHandle fieldDirectory) {

        float mapSize = GlobalConstants.WORLD_SCALE * 7;
        for (int i = 0; i < maps.length; i++) {
            FileHandle handle = maps[i];
            mapChoices[i] = fieldDirectory.name() + "/" + handle.name();
            this.addActor(mapItems[i] = CreateSelectionItem(mapChoices[i], getWidth() / 2 - mapSize / 2, getHeight() / 2 - mapSize / 2, mapSize, mapSize));
        }
    }

    private void AdvanceSelectedItem() {
        selectedMap++;
        if (selectedMap >= mapItems.length) {
            selectedMap = mapItems.length - 1;
        }
    }

    private void RollbackSelectedItem() {
        selectedMap--;
        if (selectedMap < 0) {
            selectedMap = 0;
        }
    }

    private void FixView() {
        for (MapSelectionItem item : mapItems) {
            item.setVisible(false);
        }

        mapItems[selectedMap].setVisible(true);
        forward.setVisible(selectedMap + 1 < mapItems.length);
        backward.setVisible(selectedMap > 0);

        prefs.putInteger(SELECTED_MAP, selectedMap);
        prefs.flush();
    }

    private GameButton CreateGameButton(String texturePath, ButtonType buttonType, float x, float y, float width, float height) {
        TextureRegion texture = new TextureRegion(GlobalAssets.LoadTexture(texturePath));
        GameButton button = new GameButton(texture, buttonType, x, y, width, height, 1);
        button.AddListener(this);
        return button;
    }

    private MapSelectionItem CreateSelectionItem(String texturePath, float x, float y, float width, float height) {
        TextureRegion texture = new TextureRegion(GlobalAssets.LoadTexture(texturePath));
        return new MapSelectionItem(texture, x, y, width, height);
    }

    public String GetSelectedMap() {
        return mapChoices[selectedMap];
    }

    @Override
    public void Resize() {

    }

    @Override
    public void OnButtonPress(ButtonPressEvent event) {
        switch (event.buttonType) {
            case A:
                AdvanceSelectedItem();
                break;
            case B:
                RollbackSelectedItem();
                break;
        }

        FixView();
    }
}
