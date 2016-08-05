package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewPresentationActivity extends AppCompatActivity {
    private EditText mEditTextTheme;
    private Presentation mPresentation;
    private Instruction mInstruction;

    private View mRootLayout;
    private TextInputLayout mTextInputLayoutPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_presentation);

        mTextInputLayoutPresentation = (TextInputLayout) findViewById(R.id.til_presentation);
        mEditTextTheme = (EditText) findViewById(R.id.input_theme);
        mRootLayout = findViewById(R.id.root);

        if (savedInstanceState != null) {
            mEditTextTheme.setText(savedInstanceState.getString("mEtTheme"));
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Instruction.TAG) != null) {
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_new_presentation);
        mToolbar.setTitle(R.string.new_presentation);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void newPresentation() {
        if (!validate()) {
            return;
        }

        DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
        if (mDetectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            mPresentation = new Presentation();
            mPresentation.setSubject(mEditTextTheme.getText().toString());

            Preference preference = new Preference();
            final String auth_token_string = preference.getToken(getApplicationContext());

            Call<Presentation> call = service.NewPresentation(auth_token_string,
                    String.valueOf(mInstruction.getId()),
                    mPresentation);

            call.enqueue(new Callback<Presentation>() {
                @Override
                public void onResponse(Call<Presentation> call, Response<Presentation> response) {
                    if (response.isSuccessful()) {
                        mPresentation = response.body();
                        onRequestSuccess();
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<Presentation> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newPresentation();
                        }
                    }).show();
        }
    }

    private void onRequestSuccess() {
        Toast.makeText(getApplication(), "Enviado...", Toast.LENGTH_LONG).show();
        Intent intentResult = new Intent();
        intentResult.putExtra(Presentation.TAG, mPresentation);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void onRequestFailure(int statusCode) {
        Toast.makeText(getApplicationContext(), "Requisição não completada, tente novamente! ", Toast.LENGTH_LONG).show();
        //TODO
    }

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEditTextTheme.getText().toString().trim())) {
            mTextInputLayoutPresentation.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutPresentation.setError(null);
        }

        return valid;
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
        }
        if (id == R.id.action_send) {
            newPresentation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mEtTheme", mEditTextTheme.toString());
        outState.putParcelable(Instruction.TAG, mInstruction);
        super.onSaveInstanceState(outState);
    }
}
