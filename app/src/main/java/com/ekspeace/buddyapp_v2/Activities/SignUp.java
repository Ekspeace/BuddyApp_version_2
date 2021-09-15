package com.ekspeace.buddyapp_v2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import io.paperdb.Paper;

public class SignUp extends AppCompatActivity {
    private Button btnSignUp;
    private TextView tvSignIn;
    private TextInputLayout tUsername, tEmail, tPassword, tConfirm, tPhone;
    private LinearLayout loading;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;
    private View layout;
    private PopUp popUp = new PopUp();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        SetVariables();
        ClickEvents();
    }
    private void SetVariables(){
        tvSignIn = findViewById(R.id.signUp_signIn_txt);
        btnSignUp = findViewById(R.id.signUp_button);
        tUsername = findViewById(R.id.signUp_username);
        tEmail = findViewById(R.id.signUp_email);
        tPhone = findViewById(R.id.signUp_phone);
        tPassword = findViewById(R.id.signUp_password);
        tConfirm = findViewById(R.id.signUp_confirm_password);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        loading = findViewById(R.id.signUp_progressBar);
        Paper.init(this);
    }
    private void ClickEvents(){
        tvSignIn.setOnClickListener(view -> startActivity(new Intent(this, SignIn.class)));
        btnSignUp.setOnClickListener(view -> RegistrationProcess());
    }

    private void RegistrationProcess(){
        final String email = tEmail.getEditText().getText().toString().trim();
        final String password = tPassword.getEditText().getText().toString().trim();
        final String name = tUsername.getEditText().getText().toString();
        final String confirm = tConfirm.getEditText().getText().toString();
        final String phone = tPhone.getEditText().getText().toString();

        boolean error = false;
        if (!error) {
            if (TextUtils.isEmpty(email)) {
                tEmail.setError("Email is Required.");
                error = true;
            }
            if (TextUtils.isEmpty(name)) {
                tUsername.setError("Name is required");
                error = true;
            }
            if (TextUtils.isEmpty(password)) {
                tPassword.setError("Password is Required.");
                error = true;
            } else if (password.length() < 6) {
                tPassword.setError("Password Must be greater than 6 Characters");
                error = true;
            }
            if (TextUtils.isEmpty(phone)) {
                tPhone.setError("Phone is Required.");
                error = true;
            } else if (phone.length() == 11) {
                tPhone.setError("Phone Must be 10 Numbers");
                error = true;
            }
            if (!password.equals(confirm)) {
                tPassword.setError("Password does not match");
                tConfirm.setError("Password does not match");
                error = true;
            }

            if (error)
                return;
        }
        loading.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            // register the user in firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        PopUp.smallToast(SignUp.this, layout, R.drawable.success,"Registration successfully, Please verify your phone number",Toast.LENGTH_LONG);
                        Paper.book().destroy();
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = mStore.collection("Users").document(userID);
                        HashMap<String,Object> user = new HashMap<>();
                        OneSignal.setSubscription(true);
                        String playerId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                        user.put("id", userID);
                        user.put("name", name);
                        user.put("phone", phone);
                        user.put("email", email);
                        user.put("password", password);
                        user.put("playerId", playerId);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(SignUp.this, SignIn.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.setVisibility(View.GONE);
                                PopUp.smallToast(SignUp.this, layout, R.drawable.error, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                            }
                        });

                    }else {
                        loading.setVisibility(View.GONE);
                        PopUp.smallToast(SignUp.this, layout, R.drawable.error, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading.setVisibility(View.GONE);
                    PopUp.smallToast(SignUp.this, layout, R.drawable.error, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT);
                }
            });
        } else {
            loading.setVisibility(View.GONE);
            PopUp.Toast(SignUp.this, "Connection", "Please check your internet connectivity and try again");
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
           RegistrationProcess();
        }
    }
}