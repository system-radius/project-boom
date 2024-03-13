package com.radius.system.objects.blocks;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.radius.system.assets.GlobalAssets;
import com.radius.system.enums.BoardRep;
import com.radius.system.enums.BombType;
import com.radius.system.enums.BonusType;
import com.radius.system.objects.blocks.Block;
import com.radius.system.objects.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Bonus extends Block {

    private static final int BONUS_BLOCKS = 7;

    private static final BonusType[] BONUSES = new BonusType[] {
            BonusType.BOMB_STOCK,
            BonusType.FIRE_POWER,
            BonusType.FLASH_FIRE,
            BonusType.MOVEMENT_SPEED,
            //BonusType.REMOTE_MINE,
            BonusType.PIERCE_BOMB,
            //BonusType.IMPACT_BOMB
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
            case PIERCE_BOMB:
                player.ChangeBombType(BombType.PIERCE);
                break;
            case REMOTE_MINE:
                player.ChangeBombType(BombType.REMOTE);
                break;
            case IMPACT_BOMB:
                player.ChangeBombType(BombType.IMPACT);
                break;
            case BOMB_STOCK:
            default:
                player.IncreaseBombStock();
        }

        GlobalAssets.PlaySound(GlobalAssets.BONUS_GET_SFX_PATH);
        this.Burn();
    }

    private void InitializeBonus() {

        TextureRegion[] frames = new TextureRegion[1];
        frames[0] = this.frames[BONUS_BLOCKS][bonusType.GetType()];

        activeAnimation = new Animation<>(0, frames);
    }

    @Override
    public boolean HasActiveCollision(Player player) {
        return false;
    }

    @Override
    public boolean Burn() {
        if (destroyed) {
            return false;
        }
        Destroy();
        return true;
    }

}
