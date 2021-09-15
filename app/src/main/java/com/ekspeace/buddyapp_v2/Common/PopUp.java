package com.ekspeace.buddyapp_v2.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ekspeace.buddyapp_v2.Activities.SignIn;
import com.ekspeace.buddyapp_v2.Model.EventBus.GetUserAddressEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.R;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import io.paperdb.Paper;

public class PopUp extends AppCompatActivity {

    static TextView tvTitle, tvDesc;
    static ImageView imIcon;
    static Button btnClose, btnConfirm;
    static LinearLayout linearLayout;
    static CardView cardView;

    public static void Whatsapp(Context context) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);

        tvTitle.setText("WhatsApp");
        tvDesc.setText("Chat with us on WhatsApp");
        imIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.whatsapp));
        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        btnConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

            intent.putExtra(ContactsContract.Intents.Insert.NAME, "BuddyApp")
                    .putExtra(ContactsContract.Intents.Insert.PHONE, "0719073758");

            context.startActivity(intent);
            alertDialog.dismiss();

        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }
    public static void SignOut(Context context) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);



        tvTitle.setText("Sign Out");
        tvDesc.setText("Are you sure? You want to sign out?");
        imIcon.setImageResource(R.drawable.exit_app);

        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Paper.book().destroy();
            context.startActivity(new Intent(context, SignIn.class));
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }
    public static void smallToast(Context context, View layout, int icon, String message, int duration) {

        tvDesc = layout.findViewById(R.id.toast_message);
        imIcon = layout.findViewById(R.id.toast_img);
        linearLayout = layout.findViewById(R.id.custom_toast_container);

        if(icon == R.drawable.error)
        {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorError));
            tvDesc.setTextColor(context.getResources().getColor(R.color.colorErrorLight));
        }else {
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
            tvDesc.setTextColor(context.getResources().getColor(R.color.colorPrimaryLight));
        }

        tvDesc.setText(message);
        imIcon.setImageResource(icon);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 40);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
    public static void Toast(Context context, String title, String message) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);
        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        cardView = alertDialog.findViewById(R.id.pop_up_cardView);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);

        tvTitle.setText(title);
        tvDesc.setText(message);
        imIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.internet));


        btnConfirm.setText("Try again");
        btnClose.setText("Close");
        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

      btnConfirm.setOnClickListener(v -> {
          alertDialog.cancel();
          alertDialog.dismiss();
          EventBus.getDefault().postSticky(new NetworkConnectionEvent(true));

      });
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
            alertDialog.onBackPressed();
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if(!((Activity) context).isFinishing())
        {
            alertDialog.show();
        }


    }

}
