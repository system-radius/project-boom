package com.radius.system.events.listeners;

import com.radius.system.events.parameters.EndGameEvent;

public interface EndGameEventListener {

    void OnEndGameTrigger(EndGameEvent event);

}
