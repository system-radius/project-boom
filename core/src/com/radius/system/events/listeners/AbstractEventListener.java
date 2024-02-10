package com.radius.system.events.listeners;

import com.radius.system.events.parameters.AbstractEvent;

public interface AbstractEventListener<T extends AbstractEvent> {

    void OnActivate(T event);

}
