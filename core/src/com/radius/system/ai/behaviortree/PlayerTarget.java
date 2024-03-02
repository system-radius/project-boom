package com.radius.system.ai.behaviortree;

import com.radius.system.objects.players.Player;

public class PlayerTarget implements Comparable<PlayerTarget> {

    public final int ownerId, targetId;

    private final Player owner, target;

    private final float strengthMultiplier, proximityMultiplier;

    private float distance;

    public PlayerTarget(Player owner, Player target, float strengthMultiplier, float proximityMultiplier) {
        this.owner = owner;
        this.target = target;
        this.strengthMultiplier = strengthMultiplier;
        this.proximityMultiplier = proximityMultiplier;

        this.ownerId = owner.id;
        this.targetId = target.id;
    }

    public void ComputeDistance() {
        this.distance = owner.GetDistance(target);
    }

    public boolean IsTargetAlive() {
        return target.IsAlive();
    }

    public int GetWorldX() {
        return target.GetWorldX();
    }

    public int GetWorldY() {
        return target.GetWorldY();
    }

    @Override
    public int compareTo(PlayerTarget that) {
        return Float.compare(this.distance, that.distance);
    }
}
