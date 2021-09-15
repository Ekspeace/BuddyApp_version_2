package com.ekspeace.buddyapp_v2;

import android.app.Application;

import com.onesignal.OneSignal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
//end
