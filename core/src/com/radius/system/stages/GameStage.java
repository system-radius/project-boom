package com.radius.system.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameStage extends Stage {

    private final Texture aTexture = new Texture(Gdx.files.internal("img/A.png"));

    private final Texture bTexture = new Texture(Gdx.files.internal("img/B.png"));

    private final Image aButton;

    private final Image bButton;

    private boolean hasEventA;

    private boolean hasEventB;

    public GameStage(Viewport viewport, float scale) {
        super(viewport);
        float buttonSize = 2f * scale;

        Camera camera = viewport.getCamera();

        aButton = CreateButton(aTexture, camera.position.x + viewport.getWorldWidth() / 3f + scale / 2f, camera.position.y - viewport.getWorldHeight() / 3f, buttonSize, buttonSize);
        aButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hasEventA = true;
            }
        });

        bButton = CreateButton(bTexture, camera.position.x + viewport.getWorldWidth() / 4f - scale / 2f, camera.position.y - viewport.getWorldHeight() / 3f, buttonSize, buttonSize);
        bButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hasEventB = true;
            }
        });

        this.addActor(aButton);
        this.addActor(bButton);
    }

    private Image CreateButton(Texture texture, float x, float y, float width, float height) {
        Image image = new Image(texture);
        image.setSize(width, height);
        image.setPosition(x, y);
        image.getColor().a = 0.5f;

        return image;
    }

    public void ResetEventA() {
        hasEventA = false;
    }

    public void ResetEventB() {
        hasEventB = false;
    }

    public boolean HasEventA() {
        return hasEventA;
    }

    public boolean HasEventB() {
        return hasEventB;
    }

    @Override
    public void dispose() {
        aTexture.dispose();
        bTexture.dispose();
        super.dispose();
    }

}
