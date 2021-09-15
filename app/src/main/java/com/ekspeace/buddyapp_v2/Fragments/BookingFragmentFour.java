package com.ekspeace.buddyapp_v2.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Activities.Dashboard;
import com.ekspeace.buddyapp_v2.Activities.Fragment_container;
import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Model.EventBus.ConfirmEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.DestroyFragmentManager;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.loadingEvent;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

import static com.ekspeace.buddyapp_v2.Common.Common.currentPickUp;
import static com.ekspeace.buddyapp_v2.Common.Common.simpleDateFormat;

public class BookingFragmentFour extends Fragment {
    private static BookingFragmentFour instance;
    private TextView userName, userPhone, userAddress, receiptHeader;
    private TextView serviceName, serviceSpecification, serviceDateTime, servicePrice;
    private Button serviceConfirm;
    private ImageView serviceImage;
    private View layout;
    public static BookingFragmentFour getInstance() {
        if (instance == null) {
            instance = new BookingFragmentFour();
        }
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_four, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        userName = view.findViewById(R.id.receipt_user_name);
        userPhone = view.findViewById(R.id.receipt_user_phone);
        userAddress = view.findViewById(R.id.receipt_user_address);
        receiptHeader = view.findViewById(R.id.header_text_receipt);

        serviceName = view.findViewById(R.id.receipt_service_name);
        serviceSpecification = view.findViewById(R.id.receipt_service_specification);
        serviceDateTime = view.findViewById(R.id.receipt_service_time_date);
        servicePrice = view.findViewById(R.id.receipt_service_price);
        serviceImage = view.findViewById(R.id.image_receipt_display);
        serviceConfirm = view.findViewById(R.id.btn_receipt_confirm);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, view.findViewById(R.id.custom_toast_container));

        serviceConfirm.setOnClickListener(view1 -> {
            if(Common.Service.contains("Pick"))
                PickProcess();
            else
                BookingProcess();
        });
    }
    private void setVariable(){
        userName.setText(Common.currentUser.getName());
        userPhone.setText(Common.currentUser.getPhone());
        userAddress.setText(Common.currentUserAddress);

        serviceName.setText(Common.Service);


        if(Common.Service.contains("Pick")) {
            serviceSpecification.setText(new StringBuilder(Common.currentPickUp.getType()).append(" - ").append(Common.currentPickUp.getInfo()));
            serviceDateTime.setText(Common.currentPickUp.getTimeDate());
            servicePrice.setText("Price will be available ofter confirmation");
            receiptHeader.setText("Check your pick up information");
        }
        else {
            serviceSpecification.setText(new StringBuilder(Common.currentCategory.getCategoryName()).append(" - ").append(Common.currentCategory.getCategoryPart()));
            servicePrice.setText(Common.currentCategory.getCategoryPrice());
            if(Common.Service.contains("Car"))
                serviceDateTime.setText(new StringBuilder(simpleDateFormat.format(Common.currentDate.getTime()).toString())
                    .append(" at ")
                    .append(Common.convertTimeToString(Common.currentTimeSlot)));
            else
                serviceDateTime.setText(new StringBuilder(simpleDateFormat.format(Common.currentDate.getTime()).toString())
                        .append(" at ")
                        .append(Common.ConvertTimeToString(Common.currentTimeSlot)));
        }

        switch (Common.Service){
            case "Car Wash":
                serviceImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.car_wash));
                break;
            case "Cleaning":
                serviceImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cleaner));
                break;
            case "Gardening":
                serviceImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.gardener));
                break;
            case "Pick up":
                serviceImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.delivery_man));
                break;
            default:
                break;
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
    public void setData(ConfirmEvent event) {
        if (event.isConfirm()) {
            setVariable();
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            if(Common.Service.contains("Pick"))
                PickProcess();
            else
                BookingProcess();
        }
    }
    private void BookingProcess(){
        EventBus.getDefault().postSticky(new loadingEvent(true));
        if (Common.isOnline(getContext())) {
            String startTime;
            if (Common.Service.contains(" "))
                startTime = Common.convertTimeToString(Common.currentTimeSlot);
            else
                startTime = Common.ConvertTimeToString(Common.currentTimeSlot);
            String[] convertTime = startTime.split("-");

            String[] startTimeConvert = convertTime[0].split(":");
            int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
            int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

            Calendar bookingDateWithoutHouse = Calendar.getInstance();
            bookingDateWithoutHouse.setTimeInMillis(Common.currentDate.getTimeInMillis());
            bookingDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
            bookingDateWithoutHouse.set(Calendar.MINUTE, startMinInt);

            Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());
            final HashMap<String,Object> bookingInformation = new HashMap<>();

            bookingInformation.put("timestamp",timestamp);
            bookingInformation.put("customerId", Common.currentUser.getId());
            bookingInformation.put("customerPlayerId", Common.currentUser.getPlayerId());
            bookingInformation.put("customerName", Common.currentUser.getName());
            bookingInformation.put("customerEmail",Common.currentUser.getEmail());
            bookingInformation.put("customerPhone",Common.currentUser.getPhone());
            bookingInformation.put("customerAddress",Common.currentUserAddress);
            bookingInformation.put("categoryName",Common.currentCategory.getCategoryName());
            bookingInformation.put("serviceName",Common.Service);
            bookingInformation.put("servicePrice",Common.currentCategory.getCategoryPrice());
            if (Common.Service.contains(" "))
                bookingInformation.put("time",new StringBuilder(Common.convertTimeToString(Common.currentTimeSlot))
                        .append(" at ")
                        .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
            else
                bookingInformation.put("time",new StringBuilder(Common.ConvertTimeToString(Common.currentTimeSlot))
                        .append(" at ")
                        .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
            bookingInformation.put("slot",(long) Common.currentTimeSlot);
            bookingInformation.put("verified", "Pending");
            DocumentReference bookingDate;

            if (Common.Service.contains(" ")) {
                bookingDate = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Car_Wash")
                        .document("Slot")
                        .collection(simpleDateFormat.format(Common.currentDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));
            } else if(Common.Service.contains("Cleaning")) {
                bookingDate = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Cleaning")
                        .document("Slot")
                        .collection(simpleDateFormat.format(Common.currentDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));
            }else {
                bookingDate = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Gardening")
                        .document("Slot")
                        .collection(simpleDateFormat.format(Common.currentDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));
            }
            addToUserBooking(bookingInformation, bookingDate);
        }else{
            PopUp.Toast(getContext(), "Connection...", "Please check your internet connectivity and try again");
            EventBus.getDefault().postSticky(new loadingEvent(false));
        }
    }
    private void addToUserBooking(final HashMap<String, Object> bookingInformation, DocumentReference doc) {
        if (Common.isOnline(getContext())) {
            final CollectionReference userBooking;
            if (Common.Service.contains(" ")) {
                userBooking = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Common.currentUser.getId())
                        .collection("Booking_Car_Wash");
            } else if (Common.Service.contains("Cleaning")) {
                userBooking = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Common.currentUser.getId())
                        .collection("Booking_Cleaning");
            }else {
                userBooking = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Common.currentUser.getId())
                        .collection("Booking_Gardening");
            }
            CollectionReference Booking = FirebaseFirestore.getInstance()
                    .collection("Bookings");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());
            userBooking
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.getResult().isEmpty()) {
                            String Id = userBooking.document().getId();
                            bookingInformation.put("serviceId",Id);
                            userBooking.document(Id).set(bookingInformation);
                            doc.set(bookingInformation);
                            Booking.document(Id).set(bookingInformation);
                            Notify();
                            EventBus.getDefault().postSticky(new loadingEvent(false));
                            PopUp.smallToast(getContext(), layout, R.drawable.success, "Successfully Booked!", Toast.LENGTH_SHORT);
                            startActivity(new Intent(getContext(), Dashboard.class));
                        } else {
                            EventBus.getDefault().postSticky(new loadingEvent(false));
                            PopUp.smallToast(getContext(), layout, R.drawable.error,"Sorry... but already booked for this service, Please delete history bookings",Toast.LENGTH_SHORT);
                            startActivity(new Intent(getContext(), Dashboard.class));
                        }
                    });
        }else{
            PopUp.Toast(getContext(), "Connection", "Please check your internet connectivity and try again");
            EventBus.getDefault().postSticky(new loadingEvent(false));
        }
    }
    private void PickProcess(){
        EventBus.getDefault().postSticky(new loadingEvent(true));
        if (Common.isOnline(getContext())) {
            String startTime = currentPickUp.getTimeDate();
            Timestamp timestamp;
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            if(!startTime.contains("Right")) {
                String[] convertTime = startTime.split("at");

                String[] startTimeConvert = convertTime[1].split(":");
                int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
                int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

                Calendar pickDateWithoutHouse = Calendar.getInstance();
                pickDateWithoutHouse.setTimeInMillis(Common.currentDate.getTimeInMillis());
                pickDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
                pickDateWithoutHouse.set(Calendar.MINUTE, startMinInt);

                timestamp = new Timestamp(pickDateWithoutHouse.getTime());
            }else {
                timestamp = new Timestamp(Calendar.getInstance().getTime());
            }
            final HashMap<String,Object> pickInformation = new HashMap<>();

            pickInformation.put("timestamp",timestamp);
            pickInformation.put("customerId", Common.currentUser.getId());
            pickInformation.put("customerPlayerId", Common.currentUser.getPlayerId());
            pickInformation.put("customerName", Common.currentUser.getName());
            pickInformation.put("customerEmail",Common.currentUser.getEmail());
            pickInformation.put("customerPhone",Common.currentUser.getPhone());
            pickInformation.put("customerAddress",Common.currentUserAddress);
            pickInformation.put("pickUpType", currentPickUp.getType());
            pickInformation.put("pickUpInfo", currentPickUp.getInfo());
            pickInformation.put("serviceName",Common.Service);
            pickInformation.put("servicePrice", "Price will be available ofter confirmation");
            pickInformation.put("timeAgo",currentTime);
            pickInformation.put("dateTime", currentPickUp.getTimeDate());
            pickInformation.put("verified", "Pending");

            addToUserPick(pickInformation);

        }else{
            PopUp.Toast(getContext(), "Connection", "Please check your internet connectivity and try again");
            EventBus.getDefault().postSticky(new loadingEvent(false));
        }
    }
    private void addToUserPick(final HashMap<String, Object> pickInformation) {
        if (Common.isOnline(getContext())) {
            final CollectionReference userPick;
                userPick = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Common.currentUser.getId())
                        .collection("Pick Ups");
            CollectionReference pick = FirebaseFirestore.getInstance()
                    .collection("Pick Ups");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());
            userPick
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.getResult().isEmpty()) {
                            String Id = userPick.document().getId();
                            pickInformation.put("serviceId",Id);
                            userPick.document(Id).set(pickInformation);
                            pick.document(Id).set(pickInformation);
                            Notify();
                            EventBus.getDefault().postSticky(new loadingEvent(false));
                            PopUp.smallToast(getContext(), layout, R.drawable.success, "Successfully ordered!", Toast.LENGTH_SHORT);
                            startActivity(new Intent(getContext(), Dashboard.class));
                        } else {
                            EventBus.getDefault().postSticky(new loadingEvent(false));
                            PopUp.smallToast(getContext(), layout, R.drawable.error,"Sorry, but already ordered for this service, Please delete history bookings",Toast.LENGTH_SHORT);
                            startActivity(new Intent(getContext(), Dashboard.class));
                        }
                    });
        }else{
            PopUp.Toast(getContext(), "Connection", "Please check your internet connectivity and try again");
            EventBus.getDefault().postSticky(new loadingEvent(false));
        }
    }

    private void Notify(){
        AsyncTask.execute(() -> {
            int SDK_INT = Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String message = "";
                if(Common.Service.contains("Pick"))
                    message = ("Buddy User: ").concat(Common.currentUser.getName()).concat(" have a pick up for you");
                else
                    message = ("Buddy User: ").concat(Common.currentUser.getName()).concat(" just booked your service");
                try {
                    String jsonResponse;

                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setRequestProperty("Authorization", "Basic NmE1NGU3NWYtYTA0Mi00NmRjLWJjMjktYjg0YTZjNjIwNThk");
                    con.setRequestMethod("POST");

                    String strJsonBody = "{"
                            +   "\"app_id\": \"0aaead53-0dc0-4e59-a854-25779c6c3f58\","
                            +   "\"included_segments\": [\"All\"],"
                            +   "\"data\": {\"foo\": \"bar\"},"
                            +   "\"contents\": {\"en\": \""+message+"\"}"
                            + "}";


                    System.out.println("strJsonBody:\n" + strJsonBody);

                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                    con.setFixedLengthStreamingMode(sendBytes.length);

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);

                    int httpResponse = con.getResponseCode();
                    System.out.println("httpResponse: " + httpResponse);

                    if (  httpResponse >= HttpURLConnection.HTTP_OK
                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    else {
                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    System.out.println("jsonResponse:\n" + jsonResponse);

                } catch(Throwable t) {
                    t.printStackTrace();
                }

            }

        });
    }
}