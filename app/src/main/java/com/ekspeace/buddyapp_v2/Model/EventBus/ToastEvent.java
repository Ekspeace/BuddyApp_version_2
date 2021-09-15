package com.ekspeace.buddyapp_v2.Model.EventBus;

public class ToastEvent {
    private boolean isShown;

    public ToastEvent(boolean isShown) {
        this.isShown = isShown;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean isShown) {
        this.isShown= isShown;
    }
}
