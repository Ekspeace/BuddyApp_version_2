package com.ekspeace.buddyapp_v2.Model;


import com.google.firebase.Timestamp;

public class BookingInformation {
    private String categoryName,BookingId, serviceName;
    private String customerName;
    private String customerPhone;
    private String time;
    private String customerAddress;
    private Long slot;
    private Timestamp timestamp;
    private String verified;
    private String Price;

    public BookingInformation() {
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryname) {
        this.categoryName = categoryname;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String servicename) {
        this.serviceName = servicename;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public BookingInformation(String categoryName, String bookingId, String serviceName, String customerName, String customerPhone, String time, String customerAddress, Long slot, Timestamp timestamp, String verified, String price) {
        this.categoryName = categoryName;
        BookingId = bookingId;
        this.serviceName = serviceName;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.customerAddress = customerAddress;
        this.slot = slot;
        this.timestamp = timestamp;
        this.verified = verified;
        Price = price;
    }
}
