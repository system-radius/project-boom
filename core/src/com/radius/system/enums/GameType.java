package com.radius.system.enums;

import com.radius.system.assets.GlobalAssets;

public enum GameType {

    TEST("TEST", "For testing purposes only. There are no win conditions for this mode.", GlobalAssets.CFT_MODE_PATH),

    CLASSIC("CLASSIC", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", GlobalAssets.CLASSIC_MODE_PATH),

    DEATH_MATCH("DEATH MATCH", "Curabitur dignissim faucibus dignissim. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nunc nec dui maximus, aliquam nisl ac, dignissim odio.", GlobalAssets.DEATH_MATCH_MODE_PATH),

    CAPTURE_THE_FLAG("CAPTURE THE FLAG", "Ut ante leo, fermentum sed tempor ut, lacinia ut lectus. Nulla dapibus malesuada ante, non tempus lacus aliquam vel.", GlobalAssets.CFT_MODE_PATH);

    private final String title, description, asset;

    GameType(String title, String description, String asset) {
        this.title = title;
        this.description = description;
        this.asset = asset;
    }

    public String GetDescription() {
        return description;
    }

    public String GetAssetPath() {
        return asset;
    }

    public String GetTitle() {
        return title;
    }

}
