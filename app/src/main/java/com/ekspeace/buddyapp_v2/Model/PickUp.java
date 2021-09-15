
package com.ekspeace.buddyapp_v2.Model;

public class PickUp {
    private String type, info, timeDate;

    public PickUp(String type, String info) {
        this.type = type;
        this.info = info;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

