package com.radius.system.screens.config_ui.mode_select;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.assets.GlobalConstants;
import com.radius.system.enums.GameType;
import com.radius.system.events.listeners.ModeSelectListener;
import com.radius.system.screens.config_ui.GamePanel;
import com.radius.system.utils.FontUtils;

public class ModeSelectionDescription extends GamePanel implements ModeSelectListener {

    private static final float WORLD_SCALE = GlobalConstants.WORLD_SCALE;

    private static final String ACTIVE_MODE_ID = "activeModeId", ACTIVE_MODE_TITLE = "activeModeTitle";
    private static final String ACTIVE_MODE_TEXTURE = "activeModeTexture", ACTIVE_MODE_DESC = "activeModeDesc";

    private final BitmapFont titleRenderer, descriptionRenderer;

    private final Preferences prefs;

    private Texture activeModeTexture;

    private String activeModeTitle, activeDescription;

    private GameType activeModeId = GameType.CLASSIC;

    public ModeSelectionDescription(float x, float y, float width, float height) {
        super(x, y, width, height);
        titleRenderer = FontUtils.GetFont((int)(WORLD_SCALE / 2f), Color.WHITE, 1, Color.BLACK);
        descriptionRenderer = FontUtils.GetFont((int)(WORLD_SCALE / 4f), Color.WHITE, 1, Color.BLACK);
        prefs = Gdx.app.getPreferences(GlobalConstants.APP_NAME);
        activeModeId = GameType.valueOf(prefs.getString(ACTIVE_MODE_ID, GameType.CLASSIC.toString()));
        activeModeTitle = prefs.getString(ACTIVE_MODE_TITLE, GameType.CLASSIC.toString());
        activeDescription = prefs.getString(ACTIVE_MODE_DESC, GameType.CLASSIC.GetDescription());
        activeModeTexture = GlobalAssets.LoadTexture(prefs.getString(ACTIVE_MODE_TEXTURE, GameType.CLASSIC.GetAssetPath()));
    }

    public GameType GetActiveModeId() {
        return activeModeId;
    }

    public String GetActiveModeTitle() {
        return activeModeTitle;
    }

    @Override
    public void Resize() {

    }

    @Override
    public void Draw(Batch batch) {
        if (activeModeTexture == null) {
            return;
        }

        float textureWidth = WORLD_SCALE * 10f, textureHeight = WORLD_SCALE * 5f;
        float xPos = getX() + getWidth() / 2 - textureWidth / 2;
        float yPos = getHeight() - textureHeight - WORLD_SCALE / 2;

        batch.draw(activeModeTexture, xPos, yPos, textureWidth, textureHeight);
        titleRenderer.draw(batch, activeModeTitle, xPos + WORLD_SCALE / 4f, yPos + WORLD_SCALE / 2.5f);
        descriptionRenderer.draw(batch, activeDescription, xPos, yPos - WORLD_SCALE, textureWidth - WORLD_SCALE, Align.left, true);
    }

    @Override
    public void OnModeSelected(int id, Texture texture, GameType mode) {
        activeModeTexture = texture;
        prefs.putString(ACTIVE_MODE_ID, (activeModeId = mode).toString());
        prefs.putString(ACTIVE_MODE_TITLE, activeModeTitle = mode.GetTitle());
        prefs.putString(ACTIVE_MODE_DESC, activeDescription = mode.GetDescription());
        prefs.putString(ACTIVE_MODE_TEXTURE, mode.GetAssetPath());

        prefs.flush();
    }
}
