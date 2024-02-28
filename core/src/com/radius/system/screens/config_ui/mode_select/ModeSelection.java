package com.radius.system.screens.config_ui.mode_select;

import com.badlogic.gdx.graphics.Color;
import com.radius.system.enums.GameType;
import com.radius.system.screens.config_ui.GamePanel;

public class ModeSelection extends GamePanel {

    private final ModeSelectionPanel selectionPanel;

    private final ModeSelectionDescription selectionDescription;
    public ModeSelection(float x, float y, float width, float height) {
        super(x, y, width, height);

        float adjustment = 3.5f;
        float adjustedWidth = width / adjustment;
        selectionPanel = new ModeSelectionPanel(x, y, adjustedWidth, height);
        selectionDescription = new ModeSelectionDescription(x + adjustedWidth, y, width - adjustedWidth, height);
        this.addActor(selectionPanel);
        this.addActor(selectionDescription);

        SetSelectionPanelBGColor(new Color(0.5f, 0.4f, 0.4f, 1));
        SetSelectionDescriptionBGColor(Color.GRAY);

        selectionPanel.AddSelectionListener(selectionDescription);
    }

    public String GetActiveModeTitle() {
        return selectionDescription.GetActiveModeTitle();
    }

    public GameType GetActiveGameMode() {
        return selectionDescription.GetActiveModeId();
    }

    @Override
    public void Resize() {
        selectionPanel.Resize();
    }

    public void SetSelectionPanelBGColor(Color color) {
        selectionPanel.SetBGColor(color);
    }

    public void SetSelectionDescriptionBGColor(Color color) {
        selectionDescription.SetBGColor(color);
    }
}
