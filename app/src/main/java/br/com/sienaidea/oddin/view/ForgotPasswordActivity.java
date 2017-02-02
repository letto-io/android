package br.com.sienaidea.oddin.view;

import android.content.DialogInterface;
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

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.User;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Retrofit;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
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

            HttpApi.HttpBinService service = Retrofit.getInstance();

            User user = new User();
            user.setEmail(mEmailEditText.getText().toString());

            Call<Void> call = service.recoverPassword(user);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AppCompatAlertDialogStyle);
                    if (response.isSuccessful()) {
                        builder.setMessage(getResources().getString(R.string.email_sent));
                        builder.setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_request_not_completed), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), String.valueOf(R.string.toast_request_not_completed), Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_menu, menu);
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
