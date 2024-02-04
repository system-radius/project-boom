package com.radius.system.objects.blocks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BonusType;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Bonus extends Block {

    private static final int BONUS_BLOCKS = 7;

    private static final BonusType[] BONUSES = new BonusType[] {
            BonusType.BOMB_STOCK, BonusType.FIRE_POWER, BonusType.FLASH_FIRE, BonusType.MOVEMENT_SPEED
    };

    private final BonusType bonusType;

    public Bonus(float x, float y, float width, float height) {
        super(BoardRep.BONUS, -1, x, y, width, height);

        bonusType = BONUSES[randomizer.nextInt(BONUSES.length)];
        InitializeBonus();
    }

    public void ApplyBonus(Player player) {
        switch (bonusType) {
            case FIRE_POWER:
                player.IncreaseFirePower(1);
                break;
            case FLASH_FIRE:
                player.IncreaseFirePower(3);
                break;
            case MOVEMENT_SPEED:
                player.IncreaseMovementSpeed();
                break;
            case BOMB_STOCK:
            default:
                player.IncreaseBombStock();
        }

        this.Burn();
    }

    private void InitializeBonus() {

        TextureRegion[] frames = new TextureRegion[1];
        frames[0] = REGIONS[BONUS_BLOCKS][bonusType.GetType()];

        animation = new Animation<>(0, frames);
    }

    @Override
    public boolean HasActiveCollision(Player player) {
        return false;
    }

    @Override
    public void Burn() {
        Destroy();
    }

}