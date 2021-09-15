package com.ekspeace.buddyapp_v2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp_v2.Model.EventBus.DeleteEvent;
import com.ekspeace.buddyapp_v2.Model.Notification;
import com.ekspeace.buddyapp_v2.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>{
    private Context context;
    private List<Notification> notificationList;

    public NotificationsAdapter(Context context, List<Notification> notificationList)
    {
        this.context = context;
        this.notificationList = notificationList;

    }

    @NonNull
    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_notifications_recycler_view, parent, false);
        return new NotificationsAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.MyViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvDesc.setText(notification.getContent());
        holder.tvTime.setText(notification.getServerTimestamp().toDate().toString().substring(0,16));
        holder.ivDelete.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new DeleteEvent(notificationList.get(position)));
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
       private TextView tvTitle, tvDesc, tvTime;
       private ImageView ivDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.notification_title);
            tvDesc = itemView.findViewById(R.id.notification_description);
            tvTime = itemView.findViewById(R.id.notification_time);
            ivDelete = itemView.findViewById(R.id.notification_delete);
        }

    }
}