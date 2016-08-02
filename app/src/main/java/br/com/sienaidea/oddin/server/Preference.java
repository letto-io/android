package br.com.sienaidea.oddin.server;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Siena Idea on 29/07/2016.
 */
public class Preference {
    public static final String TOKEN = Preference.class.getName();

    public void setToken(Context context, String token) {
        SharedPreferences settings = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putString("x-session-token", token);
        editor.commit();
    }

    public String getToken(Context context) {
        //Restaura as preferencias gravadas
        SharedPreferences settings = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE);
        return settings.getString("x-session-token", "");
    }
}
