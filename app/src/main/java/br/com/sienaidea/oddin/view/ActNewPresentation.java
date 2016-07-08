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
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Presentation;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActNewPresentation extends AppCompatActivity {
    private EditText mEditTextTheme;
    private Presentation mPresentation;
    private Discipline mDiscipline;
    private View mRootLayout;
    private TextInputLayout mTextInputLayoutPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_new_presentation);

        mTextInputLayoutPresentation = (TextInputLayout) findViewById(R.id.til_presentation);
        mEditTextTheme = (EditText) findViewById(R.id.input_theme);
        mRootLayout = findViewById(R.id.root);

        if (savedInstanceState != null) {
            mEditTextTheme.setText(savedInstanceState.getString("mEtTheme"));
            mDiscipline = savedInstanceState.getParcelable(Discipline.NAME);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Discipline.NAME) != null) {
                mDiscipline = getIntent().getParcelableExtra(Discipline.NAME);
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_new_presentation);
        mToolbar.setTitle("Nova Apresentação");
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void newPresentation() {
        if (!validate()){
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

            mPresentation = new Presentation(mEditTextTheme.getText().toString());

            Call<Presentation> call = service.postPresentation(CookieUtil.getCookie(getApplicationContext()),
                    String.valueOf(mDiscipline.getInstruction_id()),
                    mPresentation);

            call.enqueue(new Callback<Presentation>() {
                @Override
                public void onResponse(Call<Presentation> call, Response<Presentation> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Requisição não completada, tente novamente! ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    mPresentation = response.body();

                    Intent intentResult = new Intent();

                    intentResult.putExtra(Presentation.NAME, mPresentation);
                    setResult(RESULT_OK, intentResult);
                    finish();
                }

                @Override
                public void onFailure(Call<Presentation> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Falha na requisição ao servidor!", Toast.LENGTH_LONG).show();
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

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEditTextTheme.getText().toString())) {
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
        outState.putParcelable(Discipline.NAME, mDiscipline);
        super.onSaveInstanceState(outState);
    }
}
