package br.com.sienaidea.oddin.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.view.LectureActivity;

/**
 * Created by Siena Idea on 31/08/2016.
 */
public class ServerUtil {
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public ServerUtil(Context context) {
        mContext = context;
        mProgressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
    }

    public ServerUtil() {
    }

    public void onRequestStart(){
        mProgressDialog.setMessage("Processando...");
        mProgressDialog.show();
    }

    public void onRequestSuccess(){
        mProgressDialog.dismiss();
    }

    public void onRequesFailure(int requestCode){
        mProgressDialog.dismiss();
        Toast.makeText(mContext, "Falha no servidor. Erro: "+requestCode, Toast.LENGTH_SHORT).show();
    }

    public void onRequesFailure(String message){
        mProgressDialog.dismiss();
        Toast.makeText(mContext, "Falha no servidor. Messagem de Erro: "+message, Toast.LENGTH_SHORT).show();
    }
}
