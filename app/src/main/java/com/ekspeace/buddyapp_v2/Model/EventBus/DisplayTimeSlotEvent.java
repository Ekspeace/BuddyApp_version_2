package com.ekspeace.buddyapp_v2.Model.EventBus;

public class DisplayTimeSlotEvent {
    
    private boolean isDisplay;

    public DisplayTimeSlotEvent(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
    }
}
