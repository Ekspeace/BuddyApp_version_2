package com.ekspeace.buddyapp_v2.Model;


import com.google.firebase.Timestamp;

public class PickInformation {
    private String categoryName,serviceName,serviceId;
    private String customerPhone,customerName, customerAddress;
    private String time;
    private String timeAgo;
    private String date;
    private String pickUpType;
    private String pickUpInfo;
    private String pickId;
    private Timestamp timestamp;
    private String verified;

    public PickInformation() {
    }

    public String getPickId() {
        return pickId;
    }

    public void setPickId(String orderId) {
        this.pickId = orderId;
    }

    public PickInformation(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public String getPickUpType() {
        return pickUpType;
    }

    public void setPickUpType(String otherType) {
        this.pickUpType = pickUpType;
    }

    public String getPickUpInfo() {
        return pickUpInfo;
    }

    public void setPickUpInfo(String pickUpInfo) {
        this.pickUpInfo = pickUpInfo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
