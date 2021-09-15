package com.ekspeace.buddyapp_v2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.User;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    private Button btnSignIn;
    private TextView tvSignUp, tvForgetPassword;
    private TextInputLayout email, password;
    private LinearLayout loading;
    private FirebaseAuth mAuth;
    private CheckBox chkBoxRememberMe;
    private FirebaseUser user;
    private View layout;
    private FirebaseFirestore mStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.signIn_email);
        password = findViewById(R.id.signIn_password);
        tvSignUp = findViewById(R.id.signIn_signUp_txt);
        btnSignIn = findViewById(R.id.signIn_button);
        tvForgetPassword = findViewById(R.id.signIn_forgot_password);
        chkBoxRememberMe = findViewById(R.id.signIn_remember_me);
        loading = findViewById(R.id.SignIn_progressBar);

        Paper.init(this);
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        String UserEmailKey = Paper.book().read(Common.UserEmailKey);
        String UserPasswordKey = Paper.book().read(Common.UserPasswordKey);
        if (UserEmailKey != null) {
            email.getEditText().setText(UserEmailKey);
            password.getEditText().setText(UserPasswordKey);
        }

        ClickEvents();
    }
    private void ClickEvents(){
        tvSignUp.setOnClickListener(view -> startActivity(new Intent(this, SignUp.class)));
        tvForgetPassword.setOnClickListener((view -> startActivity(new Intent(this, ResetPassword.class))));
        btnSignIn.setOnClickListener(view -> LoginProcess());
    }
    private void LoginProcess() {
        String User_Email = email.getEditText().getText().toString().trim();
        String User_Password = password.getEditText().getText().toString().trim();

        boolean error = false;
        if (!error) {
            if (TextUtils.isEmpty(User_Email)) {
                email.setError("Email is Required.");
                error = true;
            }

            if (TextUtils.isEmpty(User_Password)) {
                password.setError("Password is Required.");
                error = true;
            } else if (User_Password.length() < 6) {
                password.setError("Password Must be greater 5 Characters");
                error = true;
            }
            if (error)
                return;

        }
        loading.setVisibility(View.VISIBLE);
        if (chkBoxRememberMe.isChecked()) {
            Paper.book().write(Common.UserEmailKey, User_Email);
            Paper.book().write(Common.UserPasswordKey, User_Password);
        }
        if (Common.isOnline(this)) {
            mAuth.signInWithEmailAndPassword(User_Email, User_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        DocumentReference docRef = mStore.collection("Users").document(Objects.requireNonNull(task.getResult()).getUser().getUid());
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
                                    if(!User_Password.equals(user.getPassword())){
                                        docRef.update("password", User_Password);
                                    }
                                    Common.currentUser = user;
                                    PopUp.smallToast(SignIn.this, layout, R.drawable.success, "Your have logged in successfully", Toast.LENGTH_SHORT);
                                    loading.setVisibility(View.GONE);
                                    Intent myIntent = new Intent(SignIn.this,
                                            Dashboard.class);
                                    startActivity(myIntent);
                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                PopUp.smallToast(SignIn.this, layout, R.drawable.error, e.getMessage(), Toast.LENGTH_SHORT);
                                loading.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        PopUp.smallToast(SignIn.this, layout, R.drawable.error, "Please Check your credential and try again", Toast.LENGTH_SHORT);
                        loading.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            PopUp.Toast(SignIn.this, "Login failed...", "Please check your internet connectivity and try again");
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
            LoginProcess();
        }
    }
}