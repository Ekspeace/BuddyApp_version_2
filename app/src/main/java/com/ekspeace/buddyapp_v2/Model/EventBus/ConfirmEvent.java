package com.ekspeace.buddyapp_v2.Model.EventBus;

public class ConfirmEvent {

    private boolean isConfirm;

    public ConfirmEvent(boolean isConfirm) {
        this.isConfirm = isConfirm;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }

}
