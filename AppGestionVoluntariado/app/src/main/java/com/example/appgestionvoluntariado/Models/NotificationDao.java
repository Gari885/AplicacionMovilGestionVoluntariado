package com.example.appgestionvoluntariado.Models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    void insert(NotificationEntity notification);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    LiveData<List<NotificationEntity>> getAllNotifications();

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    LiveData<Integer> getUnreadCount();

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    void markAsRead(int id);
    
    @Query("UPDATE notifications SET isRead = 1")
    void markAllAsRead();
}
