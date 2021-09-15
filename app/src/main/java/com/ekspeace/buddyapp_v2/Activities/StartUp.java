package com.ekspeace.buddyapp_v2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.User;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class StartUp extends AppCompatActivity {
    private Button btnSignIn, btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore mStore;
    private LinearLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_up);

        btnSignIn = findViewById(R.id.startup_signIn_btn);
        btnSignUp = findViewById(R.id.startup_signUp_btn);
        loading = findViewById(R.id.startup_progressBar);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        CheckUser();
        ClickEvents();
    }
    private void ClickEvents(){
        btnSignUp.setOnClickListener(view -> startActivity(new Intent(this, SignUp.class)));
        btnSignIn.setOnClickListener(view -> startActivity(new Intent(this, SignIn.class)));
    }
    private void CheckUser(){
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            if (user != null) {
                DocumentReference docRef = mStore.collection("Users").document(user.getUid());
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
                           //user.setAddress(task.getResult().get("address").toString());
                            user.setId(task.getResult().get("id").toString());
                            Common.currentUser = user;
                            loading.setVisibility(View.GONE);
                        }
                    }
                });
            }else{
                loading.setVisibility(View.GONE);
            }
        }else {
            loading.setVisibility(View.GONE);
            PopUp.Toast(this, "Connection", "Please check your internet connectivity and try again");
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
            CheckUser();
        }
    }
}