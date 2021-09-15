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

import com.ekspeace.buddyapp_v2.Activities.Bookings;
import com.ekspeace.buddyapp_v2.Interface.IRecyclerViewItemSelectedListener;
import com.ekspeace.buddyapp_v2.Model.BookingInformation;
import com.ekspeace.buddyapp_v2.Model.EventBus.DeleteEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.EnableNextButton;
import com.ekspeace.buddyapp_v2.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class BookingInfoAdapter extends RecyclerView.Adapter<BookingInfoAdapter.MyViewHolder> {
    private Context context;
    private List<BookingInformation> bookinginfoList;
    private List<CardView> cardViewList;

    public BookingInfoAdapter(Context context, List<BookingInformation> bookinginfoList)
    {
        this.context = context;
        this.bookinginfoList = bookinginfoList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_booking_info, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookingInformation information = bookinginfoList.get(position);
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookinginfoList.get(position).getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(),0).toString();
            if (bookinginfoList.size() > 0) {
                holder.txt_ServicePrice.setText(information.getPrice());
                holder.txt_category_name.setText(information.getCategoryName());
                holder.txt_service_name.setText(information.getServiceName());
                holder.txt_time.setText(information.getTime());
                holder.txt_time_remain.setText(dateRemain);

                if(information.getVerified().contains("Pending"))
                    holder.pending.setVisibility(View.VISIBLE);
                else if(information.getVerified().contains("Confirmed"))
                    holder.confirmed.setVisibility(View.VISIBLE);
                else
                    holder.declined.setVisibility(View.VISIBLE);

                holder.delete.setOnClickListener(view -> {
                    EventBus.getDefault().postSticky(new DeleteEvent(information));
                });

            }
    }
  /*  private void UserLoadBooking(int position) {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getUserId())
                .collection(Booking(bookinginfoList.get(position).getServiceName()));

        userBooking
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                            {
                                Common.currentBookingId = queryDocumentSnapshot.getId();
                            }
                        }
                    }
                });
    }
    public String Booking(String name)
    {
        if(name.contains(" "))
           return "Booking_Car_Wash";
        else
            return "Booking_Cleaning";
    }*/

    @Override
    public int getItemCount() {
        return bookinginfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final CardView card_booking_info;
        final TextView txt_service_name;
        final TextView txt_ServicePrice;
        final TextView txt_time;
        final TextView txt_time_remain;
        final TextView txt_category_name;
        private ImageView delete;
        final LinearLayout pending, confirmed, declined;
        LinearLayout dialog;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_category_name = itemView.findViewById(R.id.txt_Category_name);
            card_booking_info = itemView.findViewById(R.id.card_booking_info);
            txt_ServicePrice =itemView.findViewById(R.id.txt_user_ServicePrice_booking);
            txt_service_name= itemView.findViewById(R.id.txt_service_name);
            txt_time_remain = itemView.findViewById(R.id.txt_time_remain);
            txt_time = itemView.findViewById(R.id.txt_time);
            delete = itemView.findViewById(R.id.delete_booking);
            pending = itemView.findViewById(R.id.booking_pending);
            confirmed = itemView.findViewById(R.id.booking_confirm);
            declined = itemView.findViewById(R.id.booking_decline);
        }

    }
}
