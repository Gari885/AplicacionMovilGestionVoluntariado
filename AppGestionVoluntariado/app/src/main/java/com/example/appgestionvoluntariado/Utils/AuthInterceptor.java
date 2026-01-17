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

public class AuthInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();

        // LIST OF PUBLIC ENDPOINTS TO IGNORE
        if (url.contains("/categories/") || url.contains("/login") || url.contains("/register")) {
             return chain.proceed(originalRequest);
        }

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null) {
            try {
                GetTokenResult tokenResult = Tasks.await(mUser.getIdToken(false));
                String token = tokenResult.getToken();

                if (token != null) {
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return chain.proceed(originalRequest);
    }
}