package br.com.sienaidea.oddin.util;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

public class CookieUtil {

    public static String getCookie(Context context){
        List<Cookie> cookies = new PersistentCookieStore(context).getCookies();
        String phpSession = "";
        if (!cookies.isEmpty()) {
            for (int i = 0; i < cookies.size(); i++) {
                phpSession = cookies.get(i).getName()+"="+cookies.get(i).getValue()+";";
            }
        }
        return phpSession;
    }
}
