package com.radius.system.events.listeners;

import com.radius.system.events.parameters.AbstractPlayerEvent;

public interface AbstractPlayerEventListener<T extends AbstractPlayerEvent> {

    void OnActivate(T event);

}
