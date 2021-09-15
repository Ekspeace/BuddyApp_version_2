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

import com.ekspeace.buddyapp_v2.Adapter.PickInfoAdapter;
import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Interface.IOrderInfoLoadListener;
import com.ekspeace.buddyapp_v2.Model.EventBus.DeleteEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.PickInformation;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class Picks extends AppCompatActivity implements IOrderInfoLoadListener {
    private Toolbar toolbar;
    private PopUp popUp = new PopUp();
    private LinearLayout loading;
    private RecyclerView recyclerView;
    private LinearLayout no_order_txt;
    private IOrderInfoLoadListener iOrderInfoLoadListener;
    private View layout;
    @Override
    public void onResume() {
        super.onResume();
        loadUserOrders();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picks);

        toolbar = findViewById(R.id.toolbarOrder);
        no_order_txt = findViewById(R.id.no_order_txt);
        recyclerView = findViewById(R.id.recycler_order_info);
        loading = findViewById(R.id.ProgressBar_picks);
        iOrderInfoLoadListener = this;
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        loadUserOrders();
        initView();
        Actionbar();
    }

    private void loadUserOrders() {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference userOrders = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection("Pick Ups");

            List<PickInformation> pickInformationList = new ArrayList<>();

            userOrders
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentPick = queryDocumentSnapshot.toObject(PickInformation.class);
                                        pickInformationList.add(Common.currentPick);
                                        Picks.this.AssignOrder(Common.currentPick, queryDocumentSnapshot);
                                    }
                                    iOrderInfoLoadListener.onOrderInfoLoadSuccess(pickInformationList);
                                }
                                loading.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(e -> iOrderInfoLoadListener.onOrderInfoLoadFailed(e.getMessage()));

        } else {
            popUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
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
    public void DeleteOrderFromUser() {
        Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.pop_up);

        TextView tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextView tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        ImageView imIcon = alertDialog.findViewById(R.id.dialog_icon);
        Button btnClose = alertDialog.findViewById(R.id.dialog_close);
        Button btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


        tvTitle.setText("Delete pick up");
        tvDesc.setText("Do you really want to delete this pick up information?");
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
                deleteOrderFromUser();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    private void AssignOrder(PickInformation pickInformation, DocumentSnapshot documentSnapshot) {
        pickInformation.setCustomerName(documentSnapshot.get("customerName").toString());
        pickInformation.setCustomerAddress(documentSnapshot.get("customerAddress").toString());
        pickInformation.setServiceName(documentSnapshot.get("serviceName").toString());
        pickInformation.setTime(documentSnapshot.get("dateTime").toString());
        pickInformation.setTimeAgo(documentSnapshot.get("timeAgo").toString());
        pickInformation.setPickId(documentSnapshot.get("serviceId").toString());
        pickInformation.setPickUpInfo(documentSnapshot.get("pickUpInfo").toString());
        pickInformation.setPickUpType(documentSnapshot.get("pickUpType").toString());
        pickInformation.setVerified(documentSnapshot.get("verified").toString());
    }
    private void deleteOrderFromUser() {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            DocumentReference userOrderInfo = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getId())
                    .collection("Pick ups")
                    .document(Common.currentPick.getPickId());

            userOrderInfo.delete().addOnFailureListener(e -> {
                PopUp.smallToast(Picks.this, layout, R.drawable.error, e.getMessage(),Toast.LENGTH_SHORT);
                loading.setVisibility(View.GONE);
            }).addOnSuccessListener(aVoid -> {
                loading.setVisibility(View.GONE);
                PopUp.smallToast(Picks.this, layout, R.drawable.success, "Successfully deleted the order!",Toast.LENGTH_SHORT);
                DocumentReference userOrder = FirebaseFirestore.getInstance()
                        .collection("Pick Ups").document(Common.currentPick.getPickId());
                userOrder.delete();
                Picks.this.recreate();

            });
        }else{
            popUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    @Override
    public void onOrderInfoLoadSuccess(List<PickInformation> pickInformation) {
        if(pickInformation.size() > 0){
            no_order_txt.setVisibility(View.GONE);
        }
        PickInfoAdapter adapter = new PickInfoAdapter(this, pickInformation);
        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }
    @Override
    public void onOrderInfoLoadFailed(String message) {
        PopUp.smallToast(this, layout, R.drawable.error,message,Toast.LENGTH_SHORT);
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
    public void deletePick(DeleteEvent event) {
        Common.currentPick = event.getPickInformation();
        DeleteOrderFromUser();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            loadUserOrders();
        }
    }

}
