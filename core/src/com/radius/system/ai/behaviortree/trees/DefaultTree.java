package com.radius.system.ai.behaviortree.trees;

import com.radius.system.ai.behaviortree.NodeKeys;
import com.radius.system.ai.behaviortree.checks.HasTargetPoint;
import com.radius.system.ai.behaviortree.checks.IsPlantingBomb;
import com.radius.system.ai.behaviortree.checks.OnFirePath;
import com.radius.system.ai.behaviortree.nodes.Node;
import com.radius.system.ai.behaviortree.nodes.Selector;
import com.radius.system.ai.behaviortree.nodes.Sequencer;
import com.radius.system.ai.behaviortree.tasks.FindBombArea;
import com.radius.system.ai.behaviortree.tasks.FindBonus;
import com.radius.system.ai.behaviortree.tasks.FindPlayer;
import com.radius.system.ai.behaviortree.tasks.FindSpace;
import com.radius.system.ai.behaviortree.tasks.MoveToTarget;
import com.radius.system.ai.behaviortree.tasks.PlantBomb;
import com.radius.system.states.BoardState;

public class DefaultTree extends Tree {

    public DefaultTree(int id, int fireThreshold, BoardState boardState) {
        super(id, fireThreshold, boardState);
    }
}
