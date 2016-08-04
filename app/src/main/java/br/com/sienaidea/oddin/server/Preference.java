package br.com.sienaidea.oddin.server;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Siena Idea on 29/07/2016.
 */
public class Preference {
    public static final String TOKEN = Preference.class.getName();
    public static final String USER_TOKEN = "x-session-token";

    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_PROFILE = "USER_PROFILE";

    public void setToken(Context context, String token) {
        SharedPreferences settings = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putString(USER_TOKEN, token);
        editor.commit();
    }

    public void setUserName(Context context, String userName) {
        SharedPreferences settings = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public void setUserEmail(Context context, String userEmail) {
        SharedPreferences settings = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putString(USER_EMAIL, userEmail);
        editor.commit();
    }

    public void setUserProfile(Context context, int userProfile) {
        SharedPreferences settings = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putInt(USER_PROFILE, userProfile);
        editor.commit();
    }

    public String getToken(Context context) {
        //Restaura as preferencias gravadas
        SharedPreferences settings = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE);
        return settings.getString(USER_TOKEN, "");
    }

    public String getUserName(Context context) {
        //Restaura as preferencias gravadas
        SharedPreferences settings = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getString(USER_NAME, "");
    }

    public String getUserEmail(Context context) {
        //Restaura as preferencias gravadas
        SharedPreferences settings = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getString(USER_EMAIL, "");
    }

    public int getUserProfile(Context context) {
        //Restaura as preferencias gravadas
        SharedPreferences settings = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getInt(USER_PROFILE, -1);
    }
}
