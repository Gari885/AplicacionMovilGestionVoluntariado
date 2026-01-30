package com.example.appgestionvoluntariado.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // Send to backend if user is logged in
        sendTokenToBackend(token);
    }

    private void sendTokenToBackend(String token) {
        com.example.appgestionvoluntariado.Utils.SessionManager session = 
            com.example.appgestionvoluntariado.Utils.SessionManager.getInstance(getApplicationContext());
        
        if (session.isLoggedIn()) {
            String role = session.getUserRole();
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("fcmToken", token);

            if ("VOLUNTEER".equalsIgnoreCase(role)) {
                com.example.appgestionvoluntariado.Services.APIClient.getVolunteerService()
                    .updateProfile(data).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            if (response.isSuccessful()) Log.d(TAG, "Token actualizado (Voluntario)");
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                            Log.e(TAG, "Falló actualización token (Voluntario)");
                        }
                    });
            } else if ("ORGANIZATION".equalsIgnoreCase(role)) {
                com.example.appgestionvoluntariado.Services.APIClient.getOrganizationService()
                    .updateProfile(data).enqueue(new retrofit2.Callback<Void>() {
                         @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            if (response.isSuccessful()) Log.d(TAG, "Token actualizado (Organización)");
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                             Log.e(TAG, "Falló actualización token (Organización)");
                        }
                    });
            } 
            // Admin mostly likely doesn't have a profile endpoint yet or uses a different one
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "========== ¡MENSAJE RECIBIDO! ==========");
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String title = "Notificación";
        String body = "";

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + body);
        } else if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
        }

        if (body != null) {
            Log.d(TAG, "Body no es null, procesando...");
            Log.d(TAG, "Title: " + title + ", Body: " + body);
            // 1. Save to Room Database
            saveNotificationToDb(title, body);
            Log.d(TAG, "Guardado en BD");
            
            // 2. Show System Notification (Visual)
            // USER REQUEST 2026-01-28: Disable foreground system notification.
            // When app is open, only update the badge (via DB observer).
            // sendNotification(title, body);
            // Log.d(TAG, "Notificación enviada");
        } else {
            Log.e(TAG, "Body es NULL, no se procesa");
        }
    }

    private void saveNotificationToDb(String title, String body) {
        com.example.appgestionvoluntariado.Models.NotificationEntity notification = 
            new com.example.appgestionvoluntariado.Models.NotificationEntity(title, body, System.currentTimeMillis());
        
        com.example.appgestionvoluntariado.Models.AppDatabase.getDatabase(getApplicationContext())
                .notificationDao()
                .insert(notification);
    }

    private void sendNotification(String title, String messageBody) {
        android.content.Intent intent = new android.content.Intent(this, com.example.appgestionvoluntariado.Activities.MainActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(this, 0 /* Request code */, intent,
                android.app.PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        android.net.Uri defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION);
        androidx.core.app.NotificationCompat.Builder notificationBuilder =
                new androidx.core.app.NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(com.example.appgestionvoluntariado.R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId,
                    "Channel human readable title",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
