package com.ekspeace.buddyapp_v2.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ResetPassword extends AppCompatActivity {
    private Button btnResetPassword;
    private TextView tvBack;
    private TextInputLayout tvEmail;
    private FirebaseAuth mAuth;
    private LinearLayout loading;
    private View layout;
    private PopUp popUp = new PopUp();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        tvBack = findViewById(R.id.ResetPassword_back);
        btnResetPassword = findViewById(R.id.ResetPassword);
        tvEmail = findViewById(R.id.ResetEmail);
        loading = findViewById(R.id.ProgressBar_reset);
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        mAuth = FirebaseAuth.getInstance();
        tvBack.setOnClickListener(v -> startActivity(new Intent(ResetPassword.this, SignIn.class)));
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassword();
            }
        });
    }

    private void ResetPassword(){
        String email = tvEmail.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            tvEmail.setError("Please enter your email");
            return;
        }
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(ResetPassword.this)) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loading.setVisibility(View.GONE);
                                PopUp.smallToast(ResetPassword.this, layout, R.drawable.success, "Check your email to reset your password!", Toast.LENGTH_SHORT);
                                startActivity(new Intent(ResetPassword.this, SignIn.class));
                            } else {
                                loading.setVisibility(View.GONE);
                                PopUp.smallToast(ResetPassword.this, layout, R.drawable.error, "Fail to send reset password email, re-enter the email! ", Toast.LENGTH_SHORT);
                                startActivity(new Intent(ResetPassword.this, SignIn.class));
                            }
                        }
                    });
        } else {
            popUp.Toast(ResetPassword.this, "Connection...", "Please check your internet connectivity and try again");
            loading.setVisibility(View.GONE);
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
           ResetPassword();
        }
    }
}