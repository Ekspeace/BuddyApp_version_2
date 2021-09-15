package com.ekspeace.buddyapp_v2.Model.EventBus;

public class DestroyFragmentManager {
    private boolean isDestroy;

    public DestroyFragmentManager(boolean isDestroy) {
        this.isDestroy = isDestroy;
    }

    public boolean isDestroy() {
        return isDestroy;
    }

    public void setUnable(boolean isDestroy) {
        this.isDestroy = isDestroy;
    }
}
