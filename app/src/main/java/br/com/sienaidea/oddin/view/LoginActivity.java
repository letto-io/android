package br.com.sienaidea.oddin.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONObject;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.User;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //URL request
    private static final String URL_LOGIN = "controller/login";

    // UI references.
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mForgotPasswordTextView;
    private Button mSignInButton;

    private TextInputLayout mTextInputLayoutEmail;
    private TextInputLayout mTextInputLayoutPassword;

    private View mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!getCookie().equals("[]")) {
            startActivity(new Intent(LoginActivity.this, ActDiscipline.class));
        }

        // Set up the login form.
        mRootLayout = findViewById(R.id.root_layout);

        mEmailEditText = (EditText) findViewById(R.id.input_email);
        mPasswordEditText = (EditText) findViewById(R.id.input_password);

        mForgotPasswordTextView = (TextView) findViewById(R.id.link_forgot_password);
        mForgotPasswordTextView.setOnClickListener(this);

        mSignInButton = (Button) findViewById(R.id.btn_login);
        mSignInButton.setOnClickListener(this);

        mTextInputLayoutEmail = (TextInputLayout) findViewById(R.id.til_email);
        mTextInputLayoutEmail.setErrorEnabled(true);

        mTextInputLayoutPassword = (TextInputLayout) findViewById(R.id.til_password);
        mTextInputLayoutPassword.setErrorEnabled(true);
    }

    public void login() {

        if (!validate()) {
            return;
        }

        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Autenticando...");
            progressDialog.show();

            final String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();

            HttpEntity entity = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.EMAIL, email);
                jsonObject.put(User.PASSWORD, password);

                entity = new StringEntity(jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            BossClient.postLogin(getApplicationContext(), URL_LOGIN, entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Intent intent = new Intent(LoginActivity.this, ActDiscipline.class);
                    intent.putExtra(User.EMAIL, email);
                    startActivity(intent);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    BossClient.clearCookie(new PersistentCookieStore(getApplicationContext()));
                    onLoginFailed(statusCode);
                    progressDialog.dismiss();
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    public void onLoginSuccess() {
        //_loginButton.setEnabled(true);
        finish();
    }

    private void onLoginFailed(int statusCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setPositiveButton("OK", null);

        switch (statusCode) {
            case 502:
                builder.setTitle("Informação");
                builder.setMessage("Falha no servidor");
                builder.show();
                break;
            case 401:
                builder.setTitle("Informação");
                builder.setMessage("Email e/ou Senha incorretos");
                builder.show();
                break;
            case 404:
                builder.setTitle("Informação");
                builder.setMessage("Email e/ou Senha incorretos");
                builder.show();
        }
    }

    private boolean validate() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTextInputLayoutEmail.setError("entre com um email válido");
            valid = false;
        } else {
            mTextInputLayoutEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 || password.length() > 10) {
            mTextInputLayoutPassword.setError("entre 3 e 10 caracteres");
            valid = false;
        } else {
            mTextInputLayoutPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        if (v == mSignInButton) {
            login();
        }
        if (v == mForgotPasswordTextView) {
            startActivity(new Intent(LoginActivity.this, ActForgotPassword.class));
        }
    }

    private String getCookie() {
        return new PersistentCookieStore(getApplicationContext()).getCookies().toString();
    }
}

