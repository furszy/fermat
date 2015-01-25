package com.bitdubai.smartwallet.platform.layer._2_event.manager;

import com.bitdubai.smartwallet.platform.layer._1_definition.event.EventMonitor;

/**
 * Created by ciencias on 24.01.15.
 */
public class UserLoggedInEventListener implements EventListener {

    EventMonitor eventMonitor;
    private EventType eventType;
    private EventHandler eventHandler;

    public UserLoggedInEventListener (EventType eventType, EventMonitor eventMonitor){
        this.eventType = eventType;
        this.eventMonitor = eventMonitor;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void getEventHandler() {

    }

    @Override
    public void raiseEvent(PlatformEvent platformEvent) {

        try
        {
            this.eventHandler.raiseEvent(platformEvent);
        }
        catch (Exception e)
        {
            eventMonitor.handleEventException(e);
        }

    }
}
