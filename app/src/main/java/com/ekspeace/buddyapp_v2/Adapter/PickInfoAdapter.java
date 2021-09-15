package com.ekspeace.buddyapp_v2.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Interface.IRecyclerViewItemSelectedListener;
import com.ekspeace.buddyapp_v2.Model.EventBus.DeleteEvent;
import com.ekspeace.buddyapp_v2.Model.PickInformation;
import com.ekspeace.buddyapp_v2.R;
import com.google.firebase.Timestamp;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PickInfoAdapter extends RecyclerView.Adapter<PickInfoAdapter.MyViewHolder>  {
    final Context context;
    final List<PickInformation> pickInformationList;
    public PickInfoAdapter(Context context, List<PickInformation> pickInformations) {
        this.context = context;
        this.pickInformationList = pickInformations;
    }
    @NonNull
    @Override

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_pick_info, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PickInformation information = pickInformationList.get(position);
        String[] startTimeConvert = information.getTimeAgo().split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithoutHouse = Calendar.getInstance();
        bookingDateWithoutHouse.setTimeInMillis(Common.currentDate.getTimeInMillis());
        bookingDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
        bookingDateWithoutHouse.set(Calendar.MINUTE, startMinInt);

        Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                timestamp.toDate().getTime(),
                Calendar.getInstance().getTimeInMillis(),0).toString();

            if (pickInformationList.size() > 0) {
                holder.txt_service_name.setText(information.getServiceName());
                holder.txt_time_ago.setText(dateRemain);
                holder.txt_time.setText(information.getTime());
                holder.txt_OtherType.setText(information.getPickUpType());
                holder.txt_OtherInfo.setText(information.getPickUpInfo());
                if(information.getVerified().contains("Pending"))
                    holder.pending.setVisibility(View.VISIBLE);
                else if(information.getVerified().contains("Confirmed"))
                    holder.confirmed.setVisibility(View.VISIBLE);
                else
                    holder.declined.setVisibility(View.VISIBLE);

                holder.delete.setOnClickListener(view -> {
                    EventBus.getDefault().postSticky(new DeleteEvent(pickInformationList.get(position)));
                });
            }
    }
   /* private void userLoadOrder(){
        CollectionReference userOrder = FirebaseFirestore.getInstance()
                .collection("users")
                .document(Common.currentUser.getUserId())
                .collection("Orders");

        userOrder
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                            {
                                Common.currentOrderId = queryDocumentSnapshot.getId();
                            }

                        }
                    }
                });
    }*/
    @Override
    public int getItemCount() {
        return pickInformationList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView txt_service_name;
        final TextView txt_time_ago, txt_time;
        final TextView txt_OtherType;
        final TextView txt_OtherInfo;
        final ImageView delete;
        final LinearLayout pending, confirmed, declined;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_OtherType = itemView.findViewById(R.id.txt_user_OtherType_order);
            txt_OtherInfo = itemView.findViewById(R.id.txt_user_OtherInfo_order);
            txt_service_name = itemView.findViewById(R.id.txt_service_name_order);
            txt_time_ago = itemView.findViewById(R.id.txt_time_ago_order);
            txt_time = itemView.findViewById(R.id.txt_time_pick);
            delete = itemView.findViewById(R.id.delete_order);
            pending = itemView.findViewById(R.id.pick_pending);
            confirmed = itemView.findViewById(R.id.pick_confirm);
            declined = itemView.findViewById(R.id.pick_declined);
        }
    }
}
