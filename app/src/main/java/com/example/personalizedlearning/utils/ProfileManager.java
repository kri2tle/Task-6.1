package com.example.personalizedlearning.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileManager {
    private static final String PREF_NAME = "profile_prefs";
    private static final String KEY_PROFILE_CREATED = "profile_created";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_INTERESTS = "interests";

    private static ProfileManager instance;
    private final SharedPreferences preferences;

    private ProfileManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileManager(context);
        }
        return instance;
    }

    public boolean isProfileCreated() {
        return preferences.getBoolean(KEY_PROFILE_CREATED, false);
    }

    public void setProfileCreated(boolean created) {
        preferences.edit().putBoolean(KEY_PROFILE_CREATED, created).apply();
    }

    public void saveProfile(String username, String email, String interests) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_INTERESTS, interests);
        editor.putBoolean(KEY_PROFILE_CREATED, true);
        editor.apply();
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getInterests() {
        return preferences.getString(KEY_INTERESTS, "");
    }

    public void clearProfile() {
        preferences.edit().clear().apply();
    }

    public void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("username");
        editor.remove("email");
        editor.remove("interests");
        editor.apply();
    }
} 