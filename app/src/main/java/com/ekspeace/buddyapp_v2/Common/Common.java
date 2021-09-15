package com.ekspeace.buddyapp_v2.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ekspeace.buddyapp_v2.Model.BookingInformation;
import com.ekspeace.buddyapp_v2.Model.Category;
import com.ekspeace.buddyapp_v2.Model.PickInformation;
import com.ekspeace.buddyapp_v2.Model.PickUp;
import com.ekspeace.buddyapp_v2.Model.User;
import com.ekspeace.buddyapp_v2.R;
import com.google.firebase.Timestamp;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Common {
    public static final int TIME_SLOT_TOTAL = 10;
    public static final Object DISABLE_TAG = "DISABLE";
    public static String Service;
    public static int step = 0;
    public static int TimeSlot = -1;
    public static User currentUser;
    public static int currentTimeSlot = -1;
    public static PickUp currentPickUp;
    public static Category currentCategory;
    public static String currentUserAddress;
    public static BookingInformation currentBooking;
    public static PickInformation currentPick;
    public static Calendar currentDate = Calendar.getInstance();
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM ");
    public static String UserEmailKey = "EMAIL_KEY";
    public static String UserPasswordKey = "PASSWORD_KEY";


    public static String convertTimeToString(int slot) {

        switch (slot)
        {
            case 0:
                return "08:00 - 09:00";
            case 1:
                return "09:00 - 10:00";
            case 2:
                return "10:00 - 11:00";
            case 3:
                return "11:00 - 12:00";
            case 4:
                return "12:00 - 13:00";
            case 5:
                return "13:00 - 14:00";
            case 6:
                return "14:00 - 15:00";
            case 7:
                return "15:00 - 16:00";
            case 8:
                return "16:00 - 17:00";
            case 9:
                return "17:00 - 18:00";
            default:
                return "Closed";

        }

    }
    public static String ConvertTimeToString(int slot) {

        switch (slot)
        {
            case 0:
                return "08:00 - 10:30";
            case 1:
                return "11:00 - 13:30";
            case 2:
                return "14:00 - 16:30";
            case 3:
                return "17:00 - 18:30";
            default:
                return "Closed";

        }

    }
    public static String convertTimeStampToStringKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
        return simpleDateFormat.format(date);
    }

    public static Boolean isOnline(Context context)	{
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }
    public static void NetworkCheck(Context context){

    }
    public static void showNotification(Context context, int noti_id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context,
                    noti_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String NOTIFICATION_CHANNEL_ID = "ekspeace_client_app";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        Notification notification = builder.build();

        notificationManager.notify(noti_id, notification);
    }

    public enum eService {
        Car_wash,
        Cleaning,
        Gardening,
        Pick_up
    }
}
