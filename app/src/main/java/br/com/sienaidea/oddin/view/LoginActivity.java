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

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Session;
import br.com.sienaidea.oddin.retrofitModel.User;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
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

    private void login() {
        if (!validate()) {
            return;
        }

        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.authenticating));
            progressDialog.show();

            User user = new User(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());

            // Service setup
            HttpApi.HttpBinService service = Retrofit.getInstance();

            Call<Session> request = service.Login(user);

            request.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response) {
                    if (response.isSuccessful()) {
                        Session session = response.body();
                        onLoginSuccess(session);
                        progressDialog.dismiss();
                    } else {
                        onLoginFailure(response.code());
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t) {
                    onLoginFailure(502);
                    progressDialog.dismiss();
                }
            });
        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    private void onLoginSuccess(Session session) {
        //salvando as informações que irei utilizar durante o fluxo das telas no aparelho
        Preference preference = new Preference();
        preference.setToken(getApplicationContext(), session.getToken());
        preference.setUserId(getApplicationContext(), session.getPerson().getId());
        preference.setUserName(getApplicationContext(), session.getPerson().getName());
        preference.setUserEmail(getApplicationContext(), session.getPerson().getEmail());

        Intent intent = new Intent(LoginActivity.this, LectureActivity.class);
        intent.putExtra(Session.TAG, session);
        startActivity(intent);
        finish();
    }

    private void onLoginFailure(int statusCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setPositiveButton(R.string.dialog_ok, null);

        switch (statusCode) {
            case 500:
                builder.setMessage(R.string.error_server);
                builder.show();
                break;
            case 502:
                builder.setMessage(R.string.error_server);
                builder.show();
                break;
            case 401:
                builder.setMessage(R.string.unauthorized);
                builder.show();
                break;
            case 404:
                builder.setMessage(R.string.error_server);
                builder.show();
        }
    }

    private boolean validate() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTextInputLayoutEmail.setError(getResources().getString(R.string.error_invalid_email));
            valid = false;
        } else {
            mTextInputLayoutEmail.setError(null);

            if (password.isEmpty() || password.length() < 3) {
                mTextInputLayoutPassword.setError(getResources().getString(R.string.error_invalid_password));
                valid = false;
            } else {
                mTextInputLayoutPassword.setError(null);
            }
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        if (v == mSignInButton) {
            login();
        } else if (v == mForgotPasswordTextView) {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        }
    }
}

