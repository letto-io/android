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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Question;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewDoubtActivity extends AppCompatActivity {
    private CheckBox mCheckBoxAnonymous;
    private TextInputLayout mTextInputLayoutQuestion;
    private EditText mEditTextQuestion;
    private Presentation mPresentation;
    private Question mQuestion;
    private View mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_doubt);

        mRootLayout = findViewById(R.id.root);
        mTextInputLayoutQuestion = (TextInputLayout) findViewById(R.id.til_question);
        mEditTextQuestion = (EditText) findViewById(R.id.et_question);
        mCheckBoxAnonymous = (CheckBox) findViewById(R.id.chk_anonimous);


        if (savedInstanceState != null) {
            mEditTextQuestion.setText(savedInstanceState.getString("mEdtDoubt"));
            mCheckBoxAnonymous.setChecked(savedInstanceState.getBoolean("checked"));
            mPresentation = savedInstanceState.getParcelable(Presentation.TAG);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Presentation.TAG) != null) {
                mPresentation = getIntent().getParcelableExtra(Presentation.TAG);
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_new_question);
        mToolbar.setTitle(R.string.new_doubt);
        mToolbar.setSubtitle(mPresentation.getSubject());
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void sendNewQuestion() {
        if (!validate()) {
            return;
        } else {
            DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
            if (mDetectConnection.existConnection()) {
                mQuestion = new Question();
                mQuestion.setText(mEditTextQuestion.getText().toString());
                mQuestion.setAnonymous(mCheckBoxAnonymous.isChecked());
                // Retrofit setup
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(HttpApi.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Service setup
                final HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

                Preference preference = new Preference();
                final String auth_token_string = preference.getToken(getApplicationContext());

                Call<Question> request = service.NewQuestion(auth_token_string, mPresentation.getId(), mQuestion);

                request.enqueue(new Callback<Question>() {
                    @Override
                    public void onResponse(Call<Question> call, Response<Question> response) {
                        if (response.isSuccessful()) {
                            mQuestion = response.body();
                            onRequestSuccess();
                        } else {
                            onRequestFailure(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Question> call, Throwable t) {
                        onRequestFailure(401);
                    }
                });
            } else {
                Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snake_try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendNewQuestion();
                            }
                        }).show();
            }
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEditTextQuestion.getText().toString().trim())) {
            mTextInputLayoutQuestion.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutQuestion.setError(null);
        }

        return valid;
    }

    private void onRequestSuccess() {
        Toast.makeText(getApplication(), R.string.sent, Toast.LENGTH_LONG).show();
        Intent intentResult = new Intent();
        intentResult.putExtra(Question.TAG, mQuestion);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void onRequestFailure(int statusCode) {
        Toast.makeText(getApplicationContext(), R.string.toast_request_not_completed, Toast.LENGTH_LONG).show();
        //TODO
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
        }
        if (id == R.id.action_send) {
            sendNewQuestion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mEdtDoubt", mEditTextQuestion.toString());
        outState.putBoolean("checked", mCheckBoxAnonymous.isChecked());
        outState.putParcelable(Presentation.TAG, mPresentation);
        super.onSaveInstanceState(outState);
    }
}
