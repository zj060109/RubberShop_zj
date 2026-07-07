package com.rubbershop.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "rubbershop_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_ROLE = "role";
    private static final String KEY_SAVED_PHONE = "savedPhone";
    private static final String KEY_SAVED_PASSWORD = "savedPassword";

    private static SharedPreferences prefs;

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    private static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void save(String token, Long userId, String role) {
        SharedPreferences p = getPrefs();
        if (p != null) {
            p.edit().putString(KEY_TOKEN, token)
                    .putLong(KEY_USER_ID, userId != null ? userId : 0L)
                    .putString(KEY_ROLE, role).apply();
        }
    }

    public static void saveRegisteredCredentials(String phone, String password) {
        SharedPreferences p = getPrefs();
        if (p != null) {
            p.edit().putString(KEY_SAVED_PHONE, phone).putString(KEY_SAVED_PASSWORD, password).apply();
        }
    }

    public static String getSavedPhone() {
        SharedPreferences p = getPrefs();
        return p != null ? p.getString(KEY_SAVED_PHONE, null) : null;
    }

    public static String getSavedPassword() {
        SharedPreferences p = getPrefs();
        return p != null ? p.getString(KEY_SAVED_PASSWORD, null) : null;
    }

    public static void clearSavedCredentials() {
        SharedPreferences p = getPrefs();
        if (p != null) {
            p.edit().remove(KEY_SAVED_PHONE).remove(KEY_SAVED_PASSWORD).apply();
        }
    }

    public static String getToken() {
        SharedPreferences p = getPrefs();
        return p != null ? p.getString(KEY_TOKEN, null) : null;
    }

    public static Long getUserId() {
        SharedPreferences p = getPrefs();
        return p != null ? p.getLong(KEY_USER_ID, 0) : 0L;
    }

    public static String getRole() {
        SharedPreferences p = getPrefs();
        return p != null ? p.getString(KEY_ROLE, null) : null;
    }

    public static boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public static void clear() {
        SharedPreferences p = getPrefs();
        if (p != null) {
            p.edit().clear().apply();
        }
    }
}
