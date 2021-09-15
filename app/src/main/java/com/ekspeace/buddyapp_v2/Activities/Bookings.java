package com.ekspeace.buddyapp_v2.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp_v2.Adapter.BookingInfoAdapter;
import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Interface.IBookingInfoLoadListener;
import com.ekspeace.buddyapp_v2.Model.BookingInformation;
import com.ekspeace.buddyapp_v2.Model.EventBus.DeleteEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.EnableNextButton;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import io.paperdb.Paper;

public class Bookings extends AppCompatActivity implements IBookingInfoLoadListener {
    private Toolbar toolbar;
    private LinearLayout loading;
    private RecyclerView recyclerView;
    private LinearLayout no_booking_txt;
    private View layout;
    private IBookingInfoLoadListener iBookingInfoLoadListener;

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        toolbar = findViewById(R.id.toolbarbook);
        no_booking_txt = findViewById(R.id.no_booking_txt);
        recyclerView = findViewById(R.id.recycler_booking_info);
        loading = findViewById(R.id.ProgressBar_booking);
        iBookingInfoLoadListener = this;

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        Actionbar();
        initView();
        loadUserBooking();
    }
    private void loadUserBooking() {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference userBookingCar = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection("Booking_Car_Wash");

            List<BookingInformation> bookingInformationList = new ArrayList<>();

            userBookingCar.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentBooking = queryDocumentSnapshot.toObject(BookingInformation.class);
                                        bookingInformationList.add(Common.currentBooking);
                                        Bookings.this.AssignBooking(Common.currentBooking, queryDocumentSnapshot);
                                    }
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformationList);
                                    loading.setVisibility(View.GONE);
                                }

                            }
                        }
                    }).addOnFailureListener(e -> iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage()));

            CollectionReference userBookingClean = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection("Booking_Cleaning");

            userBookingClean
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentBooking = queryDocumentSnapshot.toObject(BookingInformation.class);
                                        bookingInformationList.add(Common.currentBooking);
                                        Bookings.this.AssignBooking(Common.currentBooking, queryDocumentSnapshot);

                                    }
                                }
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformationList);
                                loading.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(e -> iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage()));

            CollectionReference userBookingGarden = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection("Booking_Gardening");

            userBookingGarden
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentBooking = queryDocumentSnapshot.toObject(BookingInformation.class);
                                        bookingInformationList.add(Common.currentBooking);
                                        Bookings.this.AssignBooking(Common.currentBooking, queryDocumentSnapshot);

                                    }
                                }
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformationList);
                                loading.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(e -> iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage()));

        }else {
            loading.setVisibility(View.GONE);
            PopUp.Toast(this, "Connection", "Please check your internet connectivity and try again");

        }
    }
    private void AssignBooking(BookingInformation bookingInformation, DocumentSnapshot documentSnapshot) {
        bookingInformation.setCustomerName(documentSnapshot.get("customerName").toString());
        bookingInformation.setCustomerPhone(documentSnapshot.get("customerPhone").toString());
        bookingInformation.setCustomerAddress(documentSnapshot.get("customerAddress").toString());
        bookingInformation.setServiceName(documentSnapshot.get("serviceName").toString());
        bookingInformation.setCategoryName(documentSnapshot.get("categoryName").toString());
        bookingInformation.setTimestamp((Timestamp) documentSnapshot.get("timestamp"));
        bookingInformation.setSlot(documentSnapshot.getLong("slot"));
        bookingInformation.setTime(documentSnapshot.get("time").toString());
        bookingInformation.setVerified(documentSnapshot.get("verified").toString());
        bookingInformation.setBookingId(documentSnapshot.get("serviceId").toString());
        bookingInformation.setPrice(documentSnapshot.get("servicePrice").toString());
    }
    private void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> finish());
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
    public void DeleteBookingFromUser() {
        Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.pop_up);

        TextView tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextView tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        ImageView imIcon = alertDialog.findViewById(R.id.dialog_icon);
        Button btnClose = alertDialog.findViewById(R.id.dialog_close);
        Button btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


        tvTitle.setText("Delete booking");
        tvDesc.setText("Do you really want to delete this booking information?");
        imIcon.setImageResource(R.drawable.delete);

        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                deleteBookingFromSlot();
                alertDialog.cancel();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    private void deleteBookingFromSlot() {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            DocumentReference BookingInfo = FirebaseFirestore.getInstance()
                    .collection(BookingSlot(Common.currentBooking.getServiceName()))
                    .document("Slot")
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());
            BookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    PopUp.smallToast(Bookings.this, layout, R.drawable.error,e.getMessage(),Toast.LENGTH_SHORT);
                    loading.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deleteBookingFromUser();
                }
            });
        } else {
            PopUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void deleteBookingFromUser() {
        if (Common.isOnline(this)) {
            DocumentReference userBookingInfo1 = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection(BookingService(Common.currentBooking.getServiceName()))
                    .document(Common.currentBooking.getBookingId());

            userBookingInfo1.delete().addOnFailureListener(e -> {
                PopUp.smallToast(Bookings.this, layout, R.drawable.error, e.getMessage(),Toast.LENGTH_SHORT);
                loading.setVisibility(View.GONE);
            }).addOnSuccessListener(aVoid -> {
                loading.setVisibility(View.GONE);
                PopUp.smallToast(Bookings.this, layout, R.drawable.success,"Successfully deleted the booking !",Toast.LENGTH_SHORT);
                DocumentReference userOrder = FirebaseFirestore.getInstance()
                        .collection(BookingService(Common.currentBooking.getServiceName())).document(Common.currentBooking.getBookingId());
                userOrder.delete();
                Bookings.this.recreate();

            });
        }else{
            PopUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    public String BookingService(String name) {
        if(name.contains(" "))
            return "Booking_Car_Wash";
        else if(name.contains("Cleaning"))
            return "Booking_Cleaning";
        else
            return "Booking_Gardening";

    }
    public String BookingSlot(String name) {
        if(name.contains(" "))
            return "Time_Slot_Car_Wash";
        else if(name.contains("Cleaning"))
            return "Time_Slot_Cleaning";
        else
            return "Time_Slot_Gardening";

    }

    @Override
    public void onBookingInfoLoadSuccess(List<BookingInformation> bookingInformation) {
        if(bookingInformation.size() > 0){
            no_booking_txt.setVisibility(View.GONE);
        }
        BookingInfoAdapter adapter = new BookingInfoAdapter(this,bookingInformation);
        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.GONE);

    }
    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(Bookings.this, message, Toast.LENGTH_SHORT).show();
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
    public void deleteBooking(DeleteEvent event) {
      Common.currentBooking = event.getBookingInformation();
      DeleteBookingFromUser();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            loadUserBooking();
        }
    }

}
