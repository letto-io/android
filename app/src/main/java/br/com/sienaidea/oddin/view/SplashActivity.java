package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.sienaidea.oddin.server.Preference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference preference = new Preference();
        String auth_token_string = preference.getToken(getApplicationContext());

        if (auth_token_string != null){
            startActivity(new Intent(this, LectureActivity.class));
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
