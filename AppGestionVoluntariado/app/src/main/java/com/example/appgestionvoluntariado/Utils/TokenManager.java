package com.example.appgestionvoluntariado.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "jwt_token";

    private final SharedPreferences prefs;

    // Singleton instance
    private static TokenManager instance;

    private TokenManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveRole(String role) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_role", role);
        editor.apply();
    }

    public String getRole() {
        return prefs.getString("user_role", null);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.remove("user_role");
        editor.apply();
    }
}
