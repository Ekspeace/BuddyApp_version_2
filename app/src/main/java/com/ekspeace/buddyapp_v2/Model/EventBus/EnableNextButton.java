package com.ekspeace.buddyapp_v2.Model.EventBus;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Model.BookingInformation;
import com.ekspeace.buddyapp_v2.Model.Category;
import com.ekspeace.buddyapp_v2.Model.PickUp;
import com.ekspeace.buddyapp_v2.Model.TimeSlot;

public class EnableNextButton {

    private int step;
    private String address;
    private Category category;
    private PickUp pickUp;
    private int timeSlot;

    public EnableNextButton(int step, Category category) {
        this.step = step;
        this.category = category;
    }
    public EnableNextButton(int step, PickUp pickUp) {
        this.step = step;
        this.pickUp = pickUp;
    }

    public EnableNextButton(int step, int timeSlot) {
        this.step = step;
        this.timeSlot = timeSlot;
    }
    public EnableNextButton(int step, String address) {
        this.step = step;
        this.address = address;
    }
    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public PickUp getPickUp() {
        return pickUp;
    }

    public void setPickUp(PickUp pickUp) {
        this.pickUp = pickUp;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
