package com.example.appgestionvoluntariado.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String body;
    public long timestamp;
    public boolean isRead;

    public NotificationEntity(String title, String body, long timestamp) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.isRead = false;
    }
}
