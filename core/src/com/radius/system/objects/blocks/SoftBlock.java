package com.radius.system.objects.blocks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.enums.BoardRep;

public class SoftBlock extends HardBlock {
    public SoftBlock(int fieldIndex, float x, float y, float width, float height) {
        super(BoardRep.SOFT_BLOCK, fieldIndex, x, y, width, height);
        this.life = 1;
    }

    @Override
    protected void Initialize(int fieldIndex) {
        TextureRegion[] frames = new TextureRegion[3];
        System.arraycopy(REGIONS[fieldIndex], 0, frames, 0, frames.length);

        activeAnimation = new Animation<>(1f/3f, frames);
    }

    @Override
    protected void Destroy() {
        super.Destroy();
        hasBonus = randomizer.nextInt(100) >= 75;
    }
}
