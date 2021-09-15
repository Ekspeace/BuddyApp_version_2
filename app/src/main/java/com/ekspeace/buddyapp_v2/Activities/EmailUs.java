package com.ekspeace.buddyapp_v2.Activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class EmailUs extends AppCompatActivity {
    private Button btn_Send;
    private TextInputLayout InputSubject, InputMessage;
    private Toolbar mToolbar;
    private View layout;
    private PopUp popUp = new PopUp();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        InputSubject = findViewById(R.id.email_subject);
        InputMessage = findViewById(R.id.email_massage);
        btn_Send = findViewById(R.id.btn_email_send);
        mToolbar = findViewById(R.id.toolbar);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        Actionbar();
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String Subject = InputSubject.getEditText().getText().toString().trim();
        String Message = InputMessage.getEditText().getText().toString().trim();
        boolean error = false;
        if(!error)
        {
            if(TextUtils.isEmpty(Subject)){
                InputSubject.setError("Subject is Required.");
                error = true;
            }

            if(TextUtils.isEmpty(Message)) {
                InputMessage.setError("Message is Required.");
                error = true;
            }
            if(error)
                return;

        }
            Intent intent = new Intent(Intent.ACTION_SEND);
            String mailto = "BuddyApp4@gmail.com";
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailto});
            intent.putExtra(Intent.EXTRA_SUBJECT, Subject);
            intent.putExtra(Intent.EXTRA_TEXT, Message);
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client"));
            finish();

    }

    private void Actionbar() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
}
