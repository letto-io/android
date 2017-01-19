package br.com.sienaidea.oddin.server;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class Retrofit {
    private static HttpApi.HttpBinService instance = null;

    /**
     * @return new instance of Retrofit
     */
    private static HttpApi.HttpBinService newInstance(){
        // Retrofit setup
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(HttpApi.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        return retrofit.create(HttpApi.HttpBinService.class);
    }

    /**
     * @return instance of Retrofit
     */
    public static HttpApi.HttpBinService getInstance(){
        if (instance == null){
            instance = newInstance();
        }
        return instance;
    }
}
