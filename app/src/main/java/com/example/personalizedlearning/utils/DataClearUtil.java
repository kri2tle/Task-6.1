package com.example.personalizedlearning.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.personalizedlearning.db.DatabaseHelper;

public class DataClearUtil {
    
    public static void clearAllData(Context context) {
        try {
            // Get current user email from SharedPreferences
            ProfileManager profileManager = ProfileManager.getInstance(context);
            String userEmail = profileManager.getEmail();
            
            // Clear user data from SQLite database if email is available
            if (userEmail != null && !userEmail.isEmpty()) {
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
                dbHelper.deleteUserByEmail(userEmail);
            }
            
            // Clear profile data from SharedPreferences
            profileManager.clearProfile();
            
            // Clear learning progress from SharedPreferences
            // Since LearningProgressManager doesn't have a direct clear method,
            // we'll clear its SharedPreferences directly
            context.getSharedPreferences(
                "learning_progress_prefs", 
                Context.MODE_PRIVATE
            ).edit().clear().apply();
            
        } catch (Exception e) {
            Toast.makeText(context, "Error clearing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
} 