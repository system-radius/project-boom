package com.radius.system.ai.behaviortree.nodes;

/**
 * To be used by nodes that do not need execution
 */
public abstract class NoExecuteNode extends Node {

    @Override
    public void Execute() {
        // Do nothing.
    }

}
