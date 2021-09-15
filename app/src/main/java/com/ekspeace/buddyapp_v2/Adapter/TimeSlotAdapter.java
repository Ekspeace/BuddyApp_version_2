package com.ekspeace.buddyapp_v2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Interface.IRecyclerViewItemSelectedListener;
import com.ekspeace.buddyapp_v2.Model.EventBus.EnableNextButton;
import com.ekspeace.buddyapp_v2.Model.EventBus.ToastEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.loadingEvent;
import com.ekspeace.buddyapp_v2.Model.TimeSlot;
import com.ekspeace.buddyapp_v2.R;
import com.google.firebase.Timestamp;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlots;
    List<CardView> cardViewList;

    public TimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlots = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public TimeSlotAdapter(Context context, List<TimeSlot> timeSlots){
        this.context =context;
        this.timeSlots = timeSlots;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.time_slot,parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String startTime;
        if (Common.Service.equals("Car Wash"))
            startTime = Common.convertTimeToString(position);
        else
            startTime = Common.ConvertTimeToString(position);
        String[] convertTime = startTime.split("-");

        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithoutHouse = Calendar.getInstance();
        bookingDateWithoutHouse.setTimeInMillis(Common.currentDate.getTimeInMillis());
        bookingDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
        bookingDateWithoutHouse.set(Calendar.MINUTE, startMinInt);

        Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(timestamp.toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(),0).toString();
        if(Common.Service.contains(" "))
          holder.txt_time_slot.setText(new StringBuffer(Common.convertTimeToString(position)).toString());
        else
            holder.txt_time_slot.setText(new StringBuffer(Common.ConvertTimeToString(position)).toString());
       if(timeSlots.size() == 0) {
           holder.txt_time_slot_des.setText("Available");
           holder.txt_time_slot_des.setTextColor(context.getResources().getColor(android.R.color.black));
           holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
           holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.colorWhite));
       }else {
           for(TimeSlot slotValue:timeSlots)
           {
               int slot = Integer.parseInt(slotValue.getSlot().toString());
               if(slot == position)
               {
                   holder.card_time_slot.setTag(Common.DISABLE_TAG);
                   holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                   holder.txt_time_slot_des.setText("Not Available");
                   holder.txt_time_slot_des.setTextColor(context.getResources().getColor(android.R.color.black));
                   holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
               }else {
                   holder.txt_time_slot_des.setText("Available");
                   holder.txt_time_slot_des.setTextColor(context.getResources().getColor(android.R.color.black));
                   holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
                   holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.colorWhite));
               }
           }
       }



       //Add all card to list (20 card because we have 20 time slot)
        //No add card already in cardViewList
        if(!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);

        //Check if card time slot is available
        if (!timeSlots.contains(position)) {
            holder.setiRecyclerItemSelectedListener(new IRecyclerViewItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int pos) {
                    //Loop all card in card list
                    for (CardView cardView : cardViewList) {
                        if (cardView.getTag() == null)
                            cardView.setCardBackgroundColor(context.getResources()
                                    .getColor(R.color.colorWhite));
                    }
                    //Our selected card will change color
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    if(cardViewList.get(position).getTag() != null)
                    {
                        EventBus.getDefault().postSticky(new ToastEvent(true));
                    }
                     else {
                        Common.TimeSlot = position;
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(Common.Service.contains(" "))
          return Common.TIME_SLOT_TOTAL;
        else
            return 4;
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
      private TextView txt_time_slot, txt_time_slot_des;
      private CardView card_time_slot;

        IRecyclerViewItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerViewItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_des = itemView.findViewById(R.id.txt_time_slot_description);
            card_time_slot = itemView.findViewById(R.id.card_time_slot);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
