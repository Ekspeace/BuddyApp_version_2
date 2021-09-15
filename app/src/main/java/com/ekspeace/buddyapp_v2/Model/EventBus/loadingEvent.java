package com.ekspeace.buddyapp_v2.Model.EventBus;

public class loadingEvent {
    private boolean isLoading;

    public loadingEvent(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

}
