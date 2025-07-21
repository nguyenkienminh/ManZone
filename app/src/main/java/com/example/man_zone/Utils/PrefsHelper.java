package com.example.man_zone.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PrefsHelper {
    private static final String TAG = "PrefsHelper";
    private static final String PREFS_NAME = "ManZonePrefs";
    private static final String OLD_PREFS_NAME = "user_data";

    // Keys
    private static final String TOKEN_KEY = "token";
    private static final String EMAIL_KEY = "email";
    private static final String USER_ID_KEY = "userId";
    private static final String USER_ID_STRING_KEY = "userIdString";

    /**
     * Get the main SharedPreferences instance
     */
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Migrate data from old SharedPreferences to new one if needed
     */
    public static void migrateOldPrefs(Context context) {
        SharedPreferences oldPrefs = context.getSharedPreferences(OLD_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences newPrefs = getPrefs(context);

        // Check if migration is needed
        if (oldPrefs.contains(TOKEN_KEY) && !newPrefs.contains(TOKEN_KEY)) {
            Log.d(TAG, "Migrating preferences from old format");

            SharedPreferences.Editor editor = newPrefs.edit();

            // Migrate token
            String token = oldPrefs.getString(TOKEN_KEY, "");
            if (!token.isEmpty()) {
                editor.putString(TOKEN_KEY, token);
                Log.d(TAG, "Migrated token");
            }

            // Migrate email
            String email = oldPrefs.getString(EMAIL_KEY, "");
            if (!email.isEmpty()) {
                editor.putString(EMAIL_KEY, email);
                Log.d(TAG, "Migrated email");
            }

            editor.apply();

            // Clear old preferences
            oldPrefs.edit().clear().apply();
            Log.d(TAG, "Migration completed and old prefs cleared");
        }
    }

    /**
     * Save login information
     */
    public static void saveLoginInfo(Context context, String token, String email, String userId) {
        SharedPreferences.Editor editor = getPrefs(context).edit();

        editor.putString(TOKEN_KEY, token);
        editor.putString(EMAIL_KEY, email);

        if (userId != null && !userId.isEmpty()) {
            try {
                int userIdInt = Integer.parseInt(userId);
                editor.putInt(USER_ID_KEY, userIdInt);
                Log.d(TAG, "Saved user ID as int: " + userIdInt);
            } catch (NumberFormatException e) {
                editor.putString(USER_ID_STRING_KEY, userId);
                Log.d(TAG, "Saved user ID as string: " + userId);
            }
        }

        editor.apply();
        Log.d(TAG, "Login info saved successfully");
    }

    /**
     * Get stored token
     */
    public static String getToken(Context context) {
        String token = getPrefs(context).getString(TOKEN_KEY, "");
        Log.d(TAG, "Retrieved token: " + (!token.isEmpty() ? "present (" + token.length() + " chars)" : "empty"));
        return token;
    }

    /**
     * Get stored email
     */
    public static String getEmail(Context context) {
        return getPrefs(context).getString(EMAIL_KEY, "");
    }

    /**
     * Get stored user ID
     */
    public static int getUserId(Context context) {
        return getPrefs(context).getInt(USER_ID_KEY, -1);
    }

    /**
     * Get stored user ID as string
     */
    public static String getUserIdString(Context context) {
        return getPrefs(context).getString(USER_ID_STRING_KEY, "");
    }

    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        int userId = getUserId(context);
        boolean hasToken = !token.isEmpty();
        boolean hasUserId = userId != -1 || !getUserIdString(context).isEmpty();

        Log.d(TAG, "Login check - hasToken: " + hasToken + ", hasUserId: " + hasUserId);
        return hasToken && hasUserId;
    }

    /**
     * Clear all login data (logout)
     */
    public static void clearLoginData(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(TOKEN_KEY);
        editor.remove(EMAIL_KEY);
        editor.remove(USER_ID_KEY);
        editor.remove(USER_ID_STRING_KEY);
        editor.apply();
        Log.d(TAG, "Login data cleared");
    }
}
