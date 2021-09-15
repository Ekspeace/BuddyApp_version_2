package com.ekspeace.buddyapp_v2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ekspeace.buddyapp_v2.Adapter.ViewPagerAdapter;
import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentFour;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentOne;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentThree;
import com.ekspeace.buddyapp_v2.Fragments.BookingFragmentTwo;
import com.ekspeace.buddyapp_v2.Model.EventBus.CategoryLoadDoneEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.ConfirmEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.DisplayTimeSlotEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.EnableNextButton;
import com.ekspeace.buddyapp_v2.Model.EventBus.GetUserAddressEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.DestroyFragmentManager;
import com.ekspeace.buddyapp_v2.Model.EventBus.loadingEvent;
import com.ekspeace.buddyapp_v2.R;
import com.shuhart.stepview.StepView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Fragment_container extends AppCompatActivity {
    private ImageButton btnForward, btnBack;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private StepView stepView;
    private LinearLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        initializeVariables();

    }
    private void initializeVariables(){
        btnForward = findViewById(R.id.btn_forward);
        btnBack = findViewById(R.id.btn_back);
        toolbar = findViewById(R.id.toolbar_fragment);
        viewPager = findViewById(R.id.view_pager);
        stepView = findViewById(R.id.step_view);
        loading = findViewById(R.id.progressBar_fragment);

        setSupportActionBar(toolbar);
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        setupStepView();
        setColorButton();
        Events();

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Show step
                stepView.go(position, true);

                if (position == 0) {
                    btnBack.setEnabled(false);
                } else {
                    btnBack.setEnabled(true);
                }

                btnForward.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void Events(){
        btnBack.setOnClickListener(view -> {
            if (Common.step == 3 || Common.step > 0) {
               Common.step--;
                viewPager.setCurrentItem(Common.step);
                // Always enable NEXT when Step <3
                if (Common.step < 3) {
                    btnForward.setEnabled(true);
                    setColorButton();
                }
            }
        });
        btnForward.setOnClickListener(view -> {
            if (Common.step < 3 || Common.step == 0) {
                    Common.step++; 
                    if (Common.step == 1) {
                        loadCategory();
                        loadTimeSlot(false);
                    }
                     else if (Common.step == 2) {
                        if (Common.currentCategory != null || Common.currentPickUp != null) {
                            loadTimeSlot(true);
                        }
                    }
                    else if (Common.step == 3) {
                        if (Common.currentTimeSlot != -1) {
                            userAddress();
                            confirmBooking();
                        }
                }
            //  viewPager.setCurrentItem(Common.step);
            }

        });
    }
    private void userAddress() {
        // send broadcast to fragment step3
        EventBus.getDefault().postSticky(new GetUserAddressEvent(true));
    }
    private void confirmBooking() {
        // send broadcast to fragment step4
        EventBus.getDefault().postSticky(new ConfirmEvent(true));
    }
    private void loadTimeSlot(boolean b) {
        // Send Local Broadcast to Fragment step2
        EventBus.getDefault().postSticky(new DisplayTimeSlotEvent(b));
    }
    private void loadCategory() {
        // Send Local Broadcast to Fragment step1
        EventBus.getDefault().postSticky(new CategoryLoadDoneEvent(true));
    }
    private void setColorButton() {
        if (btnForward.isEnabled()) {
            btnForward.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_border));
        }
        else {
            btnForward.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        if (btnBack.isEnabled()) {
            btnBack.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_border));
        }
        else {
            btnBack.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void setupStepView() {
        stepView.getState()
                .commit();
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
    public void buttonNextReceiver(EnableNextButton event) {
        int step = event.getStep();
        if (step == 1) {
            if(Common.Service.contains("Pick"))
                Common.currentPickUp = event.getPickUp();
            else
                Common.currentCategory = event.getCategory();
            viewPager.setCurrentItem(step);
        } else if (step == 2) {
            Common.currentTimeSlot = event.getTimeSlot();
            viewPager.setCurrentItem(step);
        } else if (step == 3) {
            Common.currentUserAddress = event.getAddress();
            viewPager.setCurrentItem(step);
        }
        btnForward.setEnabled(true);
        if(Common.step == 3)
            btnForward.setEnabled(false);
        setColorButton();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void clearFragment(DestroyFragmentManager event) {
        if (event.isDestroy()) {

        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void LoadingBar(loadingEvent event) {
        if (event.isLoading())
            loading.setVisibility(View.VISIBLE);
        else
            loading.setVisibility(View.GONE);
    }
    @Override
    protected void onDestroy() {
        Common.step = 0;
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.togglemenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Dashboard.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
    }

}