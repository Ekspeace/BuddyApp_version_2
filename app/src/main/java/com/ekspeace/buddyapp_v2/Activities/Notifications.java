package com.ekspeace.buddyapp_v2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Adapter.NotificationsAdapter;
import com.ekspeace.buddyapp_v2.Adapter.PickInfoAdapter;
import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Interface.INotificationLoadListener;
import com.ekspeace.buddyapp_v2.Interface.IOrderInfoLoadListener;
import com.ekspeace.buddyapp_v2.Model.EventBus.DeleteEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.Notification;
import com.ekspeace.buddyapp_v2.Model.PickInformation;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

public class Notifications extends AppCompatActivity implements INotificationLoadListener {
    private Toolbar toolbar;
    private LinearLayout loading;
    private RecyclerView recyclerView;
    private LinearLayout no_notification_txt;
    private INotificationLoadListener iNotificationLoadListener;
    private Notification notification;
    private View layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        OneSignal.sendTag("User_ID", "Client");
        toolbar = findViewById(R.id.notification_toolbar);
        no_notification_txt = findViewById(R.id.no_notification_txt);
        recyclerView = findViewById(R.id.notification_recyclerView);
        loading = findViewById(R.id.notification_progressbar);
        iNotificationLoadListener = this;
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        initView();
        Actionbar();
        loadUserNotification();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadUserNotification();
    }
    private void loadUserNotification() {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference userNotifications = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection("Notifications");

            List<Notification> notificationList = new ArrayList<>();

            userNotifications
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        notification = queryDocumentSnapshot.toObject(Notification.class);
                                        notificationList.add(notification);
                                        //Picks.this.AssignOrder(Common.currentPick, queryDocumentSnapshot);
                                    }
                                    iNotificationLoadListener.onNotificationLoadSuccess(notificationList);
                                }
                                loading.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(e -> iNotificationLoadListener.onNotificationsLoadFailed(e.getMessage()));

        } else {
            PopUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void deleteNotification() {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            DocumentReference userOrderInfo = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection("Notifications")
                    .document(notification.getUid());

            userOrderInfo.delete().addOnFailureListener(e -> {
                PopUp.smallToast(this, layout, R.drawable.error, e.getMessage(),Toast.LENGTH_SHORT);
                loading.setVisibility(View.GONE);
            }).addOnSuccessListener(aVoid -> {
                loading.setVisibility(View.GONE);
                PopUp.smallToast(this, layout, R.drawable.success, "Successfully deleted the notification!",Toast.LENGTH_SHORT);
                DocumentReference userOrder = FirebaseFirestore.getInstance()
                        .collection("Notifications").document(notification.getUid());
                userOrder.delete();
                this.recreate();

            });
        }else{
            PopUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    @Override
    public void onNotificationLoadSuccess(List<Notification> notifications) {
        if(notifications.size() > 0){
            no_notification_txt.setVisibility(View.GONE);
        }
        NotificationsAdapter adapter = new NotificationsAdapter(this, notifications);
        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }
    @Override
    public void onNotificationsLoadFailed(String message) {
        PopUp.smallToast(this, layout, R.drawable.error,message, Toast.LENGTH_SHORT);
        loading.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.togglemenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Dashboard.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void deleteNotification(DeleteEvent event) {
        Common.currentPick = event.getPickInformation();
        deleteNotification();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            loadUserNotification();
        }
    }
}