package br.com.sienaidea.oddin.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.User;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ForgotPasswordActivity extends AppCompatActivity {
    //URL da requisiçao
    private static final String URL_RECOVER_PASSWORD = "controller/recover-password";

    private EditText mEmailEditText;
    private TextInputLayout mTextInputLayoutEmail;
    private View mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_forgot_password);
        mToolbar.setTitle(R.string.recover_password);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRootLayout = findViewById(R.id.root_layout);
        mTextInputLayoutEmail = (TextInputLayout) findViewById(R.id.til_forgot_password);
        mEmailEditText = (EditText) findViewById(R.id.input_email);
    }

    private void attempEnviar() {
        if (!validate()) {
            return;
        }

        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {

            HttpEntity entity = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.EMAIL, mEmailEditText.getText().toString());

                entity = new StringEntity(jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.sending));
            progressDialog.show();

            BossClient.post(getApplicationContext(), URL_RECOVER_PASSWORD, entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    onRecoverPasswordSuccess();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    onRecoverPasswordFailure();
                    progressDialog.dismiss();
                }
            });
        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean validate() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTextInputLayoutEmail.setError(getResources().getString(R.string.error_invalid_email));
            valid = false;
        } else {
            mTextInputLayoutEmail.setError(null);
        }

        return valid;
    }

    private void onRecoverPasswordSuccess() {
        Toast.makeText(getApplicationContext(), R.string.email_sent, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void onRecoverPasswordFailure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(R.string.error_server);
        builder.setPositiveButton(R.string.dialog_ok, null);
        builder.show();
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
