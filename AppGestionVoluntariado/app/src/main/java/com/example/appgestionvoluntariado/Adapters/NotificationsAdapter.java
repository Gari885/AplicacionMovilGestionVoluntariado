package com.example.appgestionvoluntariado.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.NotificationEntity;
import com.example.appgestionvoluntariado.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<NotificationEntity> notifications = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotificationEntity notification);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<NotificationEntity> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationEntity notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvDate;
        ImageView imgUnread;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvBody = itemView.findViewById(R.id.tvNotifBody);
            tvDate = itemView.findViewById(R.id.tvNotifDate);
            imgUnread = itemView.findViewById(R.id.imgUnreadDot);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(notifications.get(position));
                }
            });
        }

        public void bind(NotificationEntity notification) {
            tvTitle.setText(notification.title);
            tvBody.setText(notification.body);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(notification.timestamp)));

            if (notification.isRead) {
                imgUnread.setVisibility(View.GONE);
                tvTitle.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            } else {
                imgUnread.setVisibility(View.VISIBLE);
                tvTitle.setTextColor(itemView.getContext().getResources().getColor(R.color.black)); // Assuming black exists or hardcode
            }
        }
    }
}
