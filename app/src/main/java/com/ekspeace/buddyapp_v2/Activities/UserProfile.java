package com.ekspeace.buddyapp_v2.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.User;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {
    private FirebaseUser user;
    private TextInputLayout InputPhone, InputName, InputAddress;
    private Button updateInfoButton;
    private Toolbar mToolbar;
    private View layout;
    private LinearLayout loading;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;
    private PopUp popUp = new PopUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mToolbar = findViewById(R.id.toolbar);
        InputName = findViewById(R.id.update_name);
        InputPhone = findViewById(R.id.update_phone);
        updateInfoButton = findViewById(R.id.update_user_info);

        loading = findViewById(R.id.ProgressBar_user_profile);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        Actionbar();
        SetFieldWithCurrentInfo();
        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProcess();
            }
        });
    }
    private void UpdateProcess(){
        final String email = Common.currentUser.getEmail();
        final String password = Common.currentUser.getPassword();
        final String Name = InputName.getEditText().getText().toString();
        final String Phone = InputPhone.getEditText().getText().toString();
        boolean error = false;
        if (!error) {
            if (TextUtils.isEmpty(Name)) {
                InputName.setError("Name is required");
                error = true;
            }
            if (TextUtils.isEmpty(Phone)) {
                InputPhone.setError("Phone is Required.");
                error = true;
            } else if (Phone.length() == 11) {
                InputPhone.setError("Phone Must be 10 Numbers");
                error = true;
            }

            if (error)
                return;
        }

        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(UserProfile.this)) {
            mAuth.updateCurrentUser(mAuth.getCurrentUser())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = mStore.collection("Users").document(userID);
                                HashMap<String, Object> user = new HashMap<>();
                                user.put("id", userID);
                                user.put("name", Name);
                                user.put("phone", Phone);
                                user.put("email", email);
                                user.put("password", password);
                                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        PopUp.smallToast(UserProfile.this, layout, R.drawable.success,"Successfully updated your information",Toast.LENGTH_SHORT);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        PopUp.smallToast(UserProfile.this, layout, R.drawable.error,"Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                                    }
                                });
                                mAuth.updateCurrentUser(mAuth.getCurrentUser());
                                DocumentReference docRef = mStore.collection("users").document(mAuth.getCurrentUser().getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                                            assert user != null;
                                            user.setName(task.getResult().get("name").toString());
                                            user.setEmail(task.getResult().get("email").toString());
                                            user.setPassword(task.getResult().get("password").toString());
                                            user.setPhone(task.getResult().get("phone").toString());
                                            user.setId(task.getResult().get("userId").toString());
                                            Common.currentUser = user;
                                            startActivity(new Intent(UserProfile.this, Dashboard.class));
                                            loading.setVisibility(View.GONE);
                                        }
                                    }

                                });

                            }
                        }
                    });
        }else {
            popUp.Toast(UserProfile.this, "Connection", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
        }
    }
    private void SetFieldWithCurrentInfo() {
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
                DocumentReference docRef = mStore.collection("Users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User usr = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            assert usr != null;
                            usr.setName(task.getResult().get("name").toString());
                            usr.setPhone(task.getResult().get("phone").toString());
                            SetVariable(usr);
                            loading.setVisibility(View.GONE);
                        }else{
                            loading.setVisibility(View.GONE);
                            PopUp.smallToast(UserProfile.this, layout, R.drawable.error,"Please try again ....",Toast.LENGTH_SHORT);
                        }
                    }
                });
        }else {
            loading.setVisibility(View.GONE);
            popUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
        }
    }
    private void SetVariable(User user) {
        InputName.getEditText().setText(user.getName());
        InputPhone.getEditText().setText(user.getPhone());
    }

    private void Actionbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
           UpdateProcess();
        }
    }
}

