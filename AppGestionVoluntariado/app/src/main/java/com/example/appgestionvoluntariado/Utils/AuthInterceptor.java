package com.example.appgestionvoluntariado.Utils;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.example.appgestionvoluntariado.Activities.MainActivity;

public class AuthInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();

        // Allow /register to receive token if it exists (for Admin creation)
        if (url.contains("/categories/") || url.contains("/login") || url.contains("/forgot-password")) {
            return chain.proceed(originalRequest);
        }
        // Check for custom API token first
        String apiToken = TokenManager.getInstance(com.example.appgestionvoluntariado.App.getContext()).getToken();
        Request finalRequest = originalRequest;
        
        if (apiToken != null) {
            finalRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + apiToken)
                    .build();
        }

        Response response = chain.proceed(finalRequest);

        if (response.code() == 401) {
            Context context = com.example.appgestionvoluntariado.App.getContext();
            TokenManager.getInstance(context).clearToken();
            SessionManager.getInstance(context).logout();

            new Handler(Looper.getMainLooper()).post(() -> 
                Toast.makeText(context, "Sesión caducada. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
            );

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}