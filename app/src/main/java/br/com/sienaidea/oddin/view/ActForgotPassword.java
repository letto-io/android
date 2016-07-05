package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ActForgotPassword extends AppCompatActivity {
    //URL da requisiçao
    private static final String URL_RECOVER_PASSWORD = "controller/recover-password";

    private EditText mEmailView;
    private TextInputLayout mTextInputLayoutEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_forgot_password);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_forgot_password);
        mToolbar.setTitle("Recuperar senha");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mEmailView = (EditText) findViewById(R.id.et_email);
        mTextInputLayoutEmail = (TextInputLayout) findViewById(R.id.til_forgot_password);
    }

    private void attempEnviar() {
        // Reset errors.
        mTextInputLayoutEmail.setError(null);

        String email = mEmailView.getText().toString();

        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mTextInputLayoutEmail.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            cancel = true;
        } else if (!isEmailValid(email)) {
            mTextInputLayoutEmail.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
            cancel = true;
        }

        if (!cancel) {
            DetectConnection detectConnection = new DetectConnection(this);
            if (detectConnection.existConnection()) {

                HttpEntity entity = null;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", mEmailView.getText().toString());

                    entity = new StringEntity(jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                BossClient.post(getApplicationContext(), URL_RECOVER_PASSWORD, entity, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(ActForgotPassword.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Informação");
                        builder.setMessage("Email enviado");
                        builder.setPositiveButton("OK", null);
                        builder.show();

                        Intent intent = new Intent(ActForgotPassword.this, LoginActivity.class);

                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(ActForgotPassword.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Ops...");
                        builder.setMessage("Email não enviado");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    }
                });
            } else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ActForgotPassword.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Ops...");
                builder.setMessage("Verifique sua conexão");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return (email.contains(".com") && email.contains("@"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_send) {
            attempEnviar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
