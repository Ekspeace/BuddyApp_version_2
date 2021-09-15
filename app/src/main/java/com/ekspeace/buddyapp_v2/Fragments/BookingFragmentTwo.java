package com.ekspeace.buddyapp_v2.Fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Adapter.TimeSlotAdapter;
import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Common.SpacesItemDecoration;
import com.ekspeace.buddyapp_v2.Interface.ITimeSlotLoadListener;
import com.ekspeace.buddyapp_v2.Model.EventBus.DisplayTimeSlotEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.EnableNextButton;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.ToastEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.loadingEvent;
import com.ekspeace.buddyapp_v2.Model.TimeSlot;
import com.ekspeace.buddyapp_v2.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookingFragmentTwo extends Fragment implements ITimeSlotLoadListener {
    private static BookingFragmentTwo instance;
    private String saveCurrentTime;
    private View layout;
    private TimePicker timePicker;
    private CheckBox cbRightAway;
    private LinearLayout linearLayoutPick, linearLayoutBook;
    private RecyclerView recycler_time_slot;
    private DocumentReference serviceDoc;
    private ITimeSlotLoadListener iTimeSlotLoadListener;
    private HorizontalCalendarView horizontalCalendar;
    public static BookingFragmentTwo getInstance() {
        if (instance == null) {
            instance = new BookingFragmentTwo();
        }
        return instance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iTimeSlotLoadListener = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_two, container, false);
        init(view);
        return view;
    }
    private void init(View view) {
        recycler_time_slot = view.findViewById(R.id.recycler_time_slot);
        horizontalCalendar = view.findViewById(R.id.calenderView);
        linearLayoutBook = view.findViewById(R.id.layout_date_time_book);
        linearLayoutPick = view.findViewById(R.id.layout_time_slot_pick);
        cbRightAway = view.findViewById(R.id.pickup_chb_time_now);
        timePicker = view.findViewById(R.id.timeView_picker);
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, view.findViewById(R.id.custom_toast_container));
        setLayout(view);
    }
    private void setHorizontalCalendar(View view, int id){
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,3);
        Common.currentDate.add(Calendar.DATE,1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, id)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .configure()
                .formatTopText("dd")
                .formatMiddleText("MMMM")
                .formatBottomText("yyyy")
                .textSize(10, 14,10)
                .showTopText(true)
                .showBottomText(true)
                .end()
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.currentDate.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.currentDate = date;
                    if(!Common.Service.contains("Pick"))
                        loadAvailableTimeSlot(Common.Service, Common.simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }
    private void setLayout(View view){
        if(Common.Service.contains("Pick")){
            linearLayoutPick.setVisibility(View.VISIBLE);
            setHorizontalCalendar(view, R.id.calenderView_picker);
            timePicker.setIs24HourView(true);
            timePicker.setOnTimeChangedListener((timePicker, i, i1) -> {
                saveCurrentTime = i + ":" + i1;
        });
        }
        else{
            linearLayoutBook.setVisibility(View.VISIBLE);
            loadAvailableTimeSlot(Common.Service,
                    Common.simpleDateFormat.format(Common.currentDate.getTime()));
            setHorizontalCalendar(view, R.id.calenderView);

        }
    }
    private void setLayoutLogic(){
        if(Common.Service.contains("Pick")) {
            if (cbRightAway.isChecked()) {
                Common.currentPickUp.setTimeDate("Right Away");
                EventBus.getDefault().postSticky(new EnableNextButton(2, 0));
            } else {
                if (saveCurrentTime != null && Common.currentDate != null) {
                    String timeDate = Common.simpleDateFormat.format(Common.currentDate.getTime()) + " at " + saveCurrentTime;
                    Common.currentPickUp.setTimeDate(timeDate);
                    EventBus.getDefault().postSticky(new EnableNextButton(2, 0));
                }else{
                    PopUp.smallToast(getContext(), layout, R.drawable.error,"Please choose all necessary option",Toast.LENGTH_SHORT);
                    Common.step--;
                }
            }
        }else {
            loadAvailableTimeSlot(Common.Service,
                    Common.simpleDateFormat.format(Common.currentDate.getTime()));
            if(Common.TimeSlot != -1) {
                EventBus.getDefault().postSticky(new EnableNextButton(2, Common.TimeSlot));
            }else {
                PopUp.smallToast(getContext(), layout, R.drawable.error,"Please choose a time",Toast.LENGTH_SHORT);
                Common.step--;}
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadAllTimeSlotAvailable(DisplayTimeSlotEvent event) {
        if (event.isDisplay()) {
            // In Booking activity, we have pass this event with isDisplay = true
            setLayoutLogic();
        }
        else {
            loadAvailableTimeSlot(Common.Service,
                    Common.simpleDateFormat.format(Common.currentDate.getTime()));
        }
    }
    private void loadAvailableTimeSlot(String serviceId, final String bookDate) {
        EventBus.getDefault().postSticky(new loadingEvent(true));
        if (Common.isOnline(getContext())) {
            if (serviceId.contains("W")) {
                serviceDoc = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Car_Wash")
                        .document("Slot");
            } else if(serviceId.contains("Cleaning")) {
                serviceDoc = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Cleaning")
                        .document("Slot");
            }else {
                serviceDoc = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Gardening")
                        .document("Slot");
            }


            serviceDoc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    CollectionReference date;
                    if (documentSnapshot.exists()) {
                        if (serviceId.contains(Common.eService.Car_wash.toString())) {
                            date = FirebaseFirestore.getInstance()
                                    .collection("Time_Slot_Car_Wash")
                                    .document("Slot")
                                    .collection(bookDate);
                        }else if(serviceId.contains("Cleaning"))   {
                            date = FirebaseFirestore.getInstance()
                                    .collection("Time_Slot_Cleaning")
                                    .document("Slot")
                                    .collection(bookDate);
                        } else {
                            date = FirebaseFirestore.getInstance()
                                    .collection("Time_Slot_Gardening")
                                    .document("Slot")
                                    .collection(bookDate);
                        }

                        date.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                QuerySnapshot querySnapshot = task1.getResult();
                                if (querySnapshot.isEmpty())
                                    iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                else {
                                    List<TimeSlot> timeSlots = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task1.getResult()) {
                                        TimeSlot timeSlot = document.toObject(TimeSlot.class);
                                        timeSlot.setSlot(Long.valueOf(document.getId()));
                                        timeSlots.add(timeSlot);
                                    }
                                    iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                }
                            }
                        }).addOnFailureListener(e -> iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage()));
                    }
                }
            });
        }else{
            PopUp.Toast(getContext(), "Connection", "Please check your internet connectivity and try again");
            EventBus.getDefault().postSticky(new loadingEvent(false));
        }
    }
    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        TimeSlotAdapter adapter = new TimeSlotAdapter(getContext(),timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        EventBus.getDefault().postSticky(new loadingEvent(false));
    }
    @Override
    public void onTimeSlotLoadFailed(String message) {
       PopUp.smallToast(getContext(), layout, R.drawable.error,message,Toast.LENGTH_SHORT);
        EventBus.getDefault().postSticky(new loadingEvent(false));
    }
    @Override
    public void onTimeSlotLoadEmpty() {
        TimeSlotAdapter adapter = new TimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);
        EventBus.getDefault().postSticky(new loadingEvent(false));
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            loadAvailableTimeSlot(Common.Service,
                    Common.simpleDateFormat.format(Common.currentDate.getTime()));
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void NotAvailable(ToastEvent event) {
        if (event.isShown()) {
            PopUp.smallToast(getContext(), layout, R.drawable.error,"Slot not available",Toast.LENGTH_SHORT);
        }
    }
}