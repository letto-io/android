package br.com.sienaidea.oddin.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DetectConnection {
    private Context mContext;

    public DetectConnection(Context context){
        this.mContext = context;
    }

    /**
     *
     * @return true if connections is stablished
     */
    public boolean existConnection(){
        ConnectivityManager connectivity = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();

            // Se não existe nenhum tipo de conexão retorna false
            if (netInfo == null) {
                return false;
            }

            int netType = netInfo.getType();

            // Verifica se a conexão é do tipo WiFi ou Mobile e
            // retorna true se estiver conectado ou false em
            // caso contrário
            return (netType == ConnectivityManager.TYPE_WIFI || netType == ConnectivityManager.TYPE_MOBILE) && netInfo.isConnected();
        }else{
            return false;
        }
    }
}
