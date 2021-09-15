package com.ekspeace.buddyapp_v2.Model.EventBus;

public class GetUserAddressEvent {
    private boolean isUserAddress;

    public GetUserAddressEvent(boolean isUserAddress) {
        this.isUserAddress = isUserAddress;
    }

    public boolean isUserAddress() {
        return isUserAddress;
    }

    public void setUserAddress(boolean userAddress) {
        isUserAddress = userAddress;
    }
}
