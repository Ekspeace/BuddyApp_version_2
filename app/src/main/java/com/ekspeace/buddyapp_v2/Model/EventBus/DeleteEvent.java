package com.ekspeace.buddyapp_v2.Model.EventBus;

import com.ekspeace.buddyapp_v2.Model.BookingInformation;
import com.ekspeace.buddyapp_v2.Model.Notification;
import com.ekspeace.buddyapp_v2.Model.PickInformation;

public class DeleteEvent {
    private BookingInformation bookingInformation;
    private PickInformation pickInformation;
    private Notification notification;

    public DeleteEvent(BookingInformation bookingInformation) {
       this.bookingInformation = bookingInformation;
    }

    public DeleteEvent(PickInformation pickInformation) {
        this.pickInformation = pickInformation;
    }

    public DeleteEvent(Notification notification) {
        this.notification = notification;
    }

    public BookingInformation getBookingInformation() {
        return bookingInformation;
    }

    public void setBookingInformation(BookingInformation bookingInformation) {
        this.bookingInformation = bookingInformation;
    }

    public PickInformation getPickInformation() {
        return pickInformation;
    }

    public void setPickInformation(PickInformation pickInformation) {
        this.pickInformation = pickInformation;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
