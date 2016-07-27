package br.com.sienaidea.oddin.server;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;

public class BossClient {
    private static final String BASE_URL = "http://ws-edupanel.herokuapp.com/"; // TESTE
    //private static final String BASE_URL = "http://ws-oddin.herokuapp.com/"; //PRODUÇÃO
    private static String CLOSE_PRESENTATION_URL;

    //Servidor é lento, necessário setar um tempo maior para as requisições
    private static final int DEFAULT_TIMEOUT = 20 * 1000;

    private static AsyncHttpClient client = new AsyncHttpClient();

    //este método é chamado sempre que ocorre o logout, limpa os cookies e o cabeçalho para nova requisição
    public static void clearCookie(PersistentCookieStore cookieStore){
        cookieStore.clear();
        client.setCookieStore(cookieStore);
        client.removeHeader("Cookie");
    }

    //GET default para receber os retornos do tipo Json
    public static void get(String url, RequestParams params, String phpSession, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(DEFAULT_TIMEOUT);
        client.addHeader("Accept", "application/json");
        client.addHeader("Cookie", phpSession);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    //post sem envio de json
    public static void post(String url, String phpSession, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(DEFAULT_TIMEOUT);
        client.addHeader("Accept", "application/json");
        client.addHeader("Cookie", phpSession);
        client.post(getAbsoluteUrl(url), null, responseHandler);
    }


    //mando uma requisição POST enviando o HttpEnty que contém o JSON com os atributos email e senha
    public static void postLogin(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler){
        client.setTimeout(DEFAULT_TIMEOUT);
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        client.setCookieStore(new PersistentCookieStore(context));
    }

    //post enviando JSON
    public static void post(Context context, String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler){
        client.setTimeout(DEFAULT_TIMEOUT);
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    //tentativas do new Material
    public static void postMaterial(String url, RequestParams params, String phpSession, AsyncHttpResponseHandler responseHandler){
        client.setTimeout(DEFAULT_TIMEOUT);
        client.addHeader("Content-Type", "multpart/form-data");
        client.addHeader("Cookie", phpSession);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postMaterial(Context context, String url, String phpSession, HttpEntity entity , AsyncHttpResponseHandler responseHandler){
        client.setTimeout(DEFAULT_TIMEOUT);
        client.addHeader("Accept", "multpart/form-data");
        client.addHeader("Cookie", phpSession);
        //client.post(getAbsoluteUrl(url), responseHandler);
        client.post(context, getAbsoluteUrl(url), entity, "multpart/form-data", responseHandler);
    }
    //fim tentativas



    public static void delete(String url, String phpSession, AsyncHttpResponseHandler responseHandler){
        client.setTimeout(DEFAULT_TIMEOUT);
        client.addHeader("Accept", "application/json");
        client.addHeader("Cookie", phpSession);
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    //concatena a URL_BASE com a url da requisição desejada
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
