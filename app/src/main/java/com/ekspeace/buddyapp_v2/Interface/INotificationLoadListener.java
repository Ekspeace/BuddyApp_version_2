package com.ekspeace.buddyapp_v2.Interface;


import com.ekspeace.buddyapp_v2.Model.Notification;

import java.util.List;

public interface INotificationLoadListener {
    void onNotificationLoadSuccess(List<Notification> notifications);
    void onNotificationsLoadFailed(String message);
}
