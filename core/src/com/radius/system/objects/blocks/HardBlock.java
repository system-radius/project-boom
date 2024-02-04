package com.radius.system.objects.blocks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.enums.BoardRep;

public class HardBlock extends Block {

    public HardBlock(BoardRep rep, int fieldIndex, float x, float y, float width, float height) {
        super(rep, fieldIndex, x, y, width, height);
    }

    public HardBlock(int fieldIndex, float x, float y, float width, float height) {
        super(BoardRep.HARD_BLOCK, fieldIndex, x, y, width, height);

        this.life = randomizer.nextInt(10) + 5;
    }

    @Override
    protected void Initialize(int fieldIndex) {
        TextureRegion[] frames = new TextureRegion[3];
        System.arraycopy(REGIONS[fieldIndex], 3, frames, 0, frames.length);

        animation = new Animation<>(1f/3f, frames);
    }

    @Override
    protected void Destroy() {
        super.Destroy();
        hasBonus = true;
    }

    @Override
    public void Burn() {
        if (burning) {
            return;
        }

        life --;

        burning = life == 0;
        if (burning) {
            animationElapsedTime = 0f;
        }
    }
}
