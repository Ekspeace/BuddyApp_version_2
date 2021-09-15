package com.ekspeace.buddyapp_v2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Adapter.ViewPagerBannerAdapter;
import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentFour;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentOne;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentThree;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentTwo;
import com.ekspeace.buddyapp_v2.Interface.IBannerLoadListener;
import com.ekspeace.buddyapp_v2.Interface.INotificationLoadListener;
import com.ekspeace.buddyapp_v2.Model.Banner;
import com.ekspeace.buddyapp_v2.Model.EventBus.DeleteEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.DestroyFragmentManager;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.Notification;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ekspeace.buddyapp_v2.Common.Common.Service;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IBannerLoadListener {
    private ImageButton btnForward;
    private ViewPager viewPagerBanner;
    private LinearLayout cvCarWash, cvCleaning, cvGardening, cvDelivery;
    private IBannerLoadListener iBannerLoadListener;
    private Banner banner;
    private View layout;
    private  LinearLayout loading;
    private List<LinearLayout> cardViews;
    private boolean haveNotifications = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();
        Events();
    }
    private void init(){
        reset();
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
        NavigationView navigationView = findViewById(R.id.navigationView);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        btnForward = findViewById(R.id.main_next_btn);
        cvCarWash = findViewById(R.id.car_wash);
        cvCleaning = findViewById(R.id.cleaning);
        cvDelivery = findViewById(R.id.delivery);
        cvGardening = findViewById(R.id.gardening);
        loading = findViewById(R.id.dash_progressbar);
        setSupportActionBar(toolbar);
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.user_profile_name);
        TextView tvEmail = headerView.findViewById(R.id.user_profile_email);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.menu);
        navigationView.setNavigationItemSelectedListener(this);

        iBannerLoadListener = this;
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        tvName.setText(Common.currentUser.getName());
        tvEmail.setText(Common.currentUser.getEmail());
        cardViews = new ArrayList();
        cardViews.add(cvCarWash);
        cardViews.add(cvCleaning);
        cardViews.add(cvGardening);
        cardViews.add(cvDelivery);
        LoadBanner();
    }
    private void reset(){
        if(Common.step != 0) {
            EventBus.getDefault().postSticky(new DestroyFragmentManager(true));
        }
    }
    private void Events(){
        btnForward.setEnabled(false);
        btnForward.setOnClickListener(view -> startActivity(new Intent(this, Fragment_container.class)));
        cvCarWash.setOnClickListener(view -> {
            cvCarWash.setFocusable(true);
            SelectService();
            SwitchService(Common.eService.Car_wash);
            cvCarWash.setFocusable(false);
            btnForward.setEnabled(EnableButton());

        });
        cvCleaning.setOnClickListener(view -> {
            cvCleaning.setFocusable(true);
            SelectService();
            SwitchService(Common.eService.Cleaning);
            cvCleaning.setFocusable(false);
            btnForward.setEnabled(EnableButton());
        });
        cvGardening.setOnClickListener(view -> {
            cvGardening.setFocusable(true);
            SelectService();
            SwitchService(Common.eService.Gardening);
            cvGardening.setFocusable(false);
            btnForward.setEnabled(EnableButton());
        });
        cvDelivery.setOnClickListener(view -> {
            cvDelivery.setFocusable(true);
            SelectService();
            SwitchService(Common.eService.Pick_up);
            cvDelivery.setFocusable(false);
            btnForward.setEnabled(EnableButton());
        });
    }
    private void SelectService(){
        for (LinearLayout cardView : cardViews) {
            if (cardView.hasFocusable()) {
                cardView.setBackground((ContextCompat
                        .getDrawable(this, R.drawable.border)));
            } else {
                cardView.setBackgroundColor((getResources()
                        .getColor(R.color.colorWhite)));
            }
        }
    }
    private void SwitchService(Common.eService service){
        switch (service){
            case Car_wash:
                Service = "Car Wash";
                break;
            case Cleaning:
                Service = "Cleaning";
                break;
            case Gardening:
                Service = "Gardening";
                break;
            case Pick_up:
                Service = "Pick up";
                break;
            default:
                break;
        }
    }
    private boolean EnableButton(){
        if(Service != null) {
            btnForward.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_border));
            return true;
        }else {
            btnForward.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            return false;
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.profile:
                startActivity(new Intent(this, UserProfile.class));
                break;
            case R.id.Bookings:
                startActivity(new Intent(this, Bookings.class));
                break;
            case R.id.orders:
                startActivity(new Intent(this, Picks.class));
                break;
            case R.id.aboutUs:
                startActivity(new Intent(this, AboutUs.class));
                break;
            case R.id.email:
                startActivity(new Intent(this, EmailUs.class));
                break;
            case R.id.rate:
                rateMe();
                break;
            case R.id.whatsapp:
                PopUp.Whatsapp(this);
                break;
            case R.id.SignOut :
                PopUp.SignOut(this);
                break;
            default:
                break;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(!haveNotifications) {
            inflater.inflate(R.menu.notification, menu);
        }else {
            inflater.inflate(R.menu.notification_active, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.notification) {
            startActivity(new Intent(this, Notifications.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
    private void LoadBanner(){
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            loadUserNotification();
            CollectionReference crBanner = FirebaseFirestore.getInstance()
                    .collection("Banner");

            List<Banner> banners = new ArrayList<>();

            crBanner
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        banner = queryDocumentSnapshot.toObject(Banner.class);
                                        banners.add(banner);
                                    }
                                    iBannerLoadListener.onBannerLoadSuccess(banners);
                                }
                                loading.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(e -> iBannerLoadListener.onBannerLoadFailed(e.getMessage()));

        } else {
            PopUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void loadUserNotification() {
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
                                    haveNotifications = true;
                                    }
                                }
                            }
                    }).addOnFailureListener(e -> PopUp.smallToast(this, layout, R.drawable.error,e.getMessage(), Toast.LENGTH_SHORT));

    }
    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        ViewPagerBannerAdapter adapter = new ViewPagerBannerAdapter(banners, this);
        viewPagerBanner.setAdapter(adapter);
        viewPagerBanner.setPadding(16, 0, 0, 0);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBannerLoadFailed(String message) {
        PopUp.smallToast(this, layout, R.drawable.error,message, Toast.LENGTH_SHORT);
        loading.setVisibility(View.GONE);
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
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            LoadBanner();
        }
    }
}
