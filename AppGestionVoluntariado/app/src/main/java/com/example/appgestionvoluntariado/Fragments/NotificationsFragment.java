package com.example.appgestionvoluntariado.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.NotificationsAdapter;
import com.example.appgestionvoluntariado.Models.AppDatabase;
import com.example.appgestionvoluntariado.Models.NotificationEntity;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.concurrent.Executors;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarNotifications);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter();
        recyclerView.setAdapter(adapter);

        TextView tvClearAll = view.findViewById(R.id.tvClearAll);

        db = AppDatabase.getDatabase(requireContext());

        // Observe notifications
        db.notificationDao().getAllNotifications().observe(getViewLifecycleOwner(), new Observer<List<NotificationEntity>>() {
            @Override
            public void onChanged(List<NotificationEntity> notificationEntities) {
                adapter.setNotifications(notificationEntities);
            }
        });

        // Click on item -> Mark as read
        adapter.setOnItemClickListener(notification -> {
            if (!notification.isRead) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.notificationDao().markAsRead(notification.id);
                });
            }
        });

        // Click on "Mark all as read"
        tvClearAll.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                db.notificationDao().markAllAsRead();
            });
        });

        return view;
    }
}
