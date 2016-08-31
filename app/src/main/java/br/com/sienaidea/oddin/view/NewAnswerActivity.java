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
import br.com.sienaidea.oddin.retrofitModel.Answer;
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

public class NewAnswerActivity extends AppCompatActivity {
    private EditText mEditTextAnswer;
    private View mRootLayout;
    private Question mQuestion;
    private Answer mAnswer;
    private Presentation mPresentation;
    private TextInputLayout mTextInputLayoutAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_answer);

        mRootLayout = findViewById(R.id.root);
        mTextInputLayoutAnswer = (TextInputLayout) findViewById(R.id.til_answer);
        mEditTextAnswer = (EditText) findViewById(R.id.et_answer);

        if (savedInstanceState != null) {
            mQuestion = savedInstanceState.getParcelable(Question.TAG);
            mPresentation = savedInstanceState.getParcelable(Presentation.TAG);
            mEditTextAnswer.setText(savedInstanceState.getString("mEditTextAnswer"));
        } else {
            if (getIntent().getExtras() != null) {
                mPresentation = getIntent().getParcelableExtra(Presentation.TAG);
                mQuestion = getIntent().getParcelableExtra(Question.TAG);

                if (mPresentation == null) {
                    Toast.makeText(getApplicationContext(), "Falha ao iniciar!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_new_answer);
        mToolbar.setTitle("Responder");
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void newAnswer() {
        if (!validate()) {
            return;
        } else {
            DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
            if (mDetectConnection.existConnection()) {
                // Retrofit setup
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(HttpApi.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

                mAnswer = new Answer();
                mAnswer.setText(mEditTextAnswer.getText().toString());

                Preference preference = new Preference();
                final String auth_token_string = preference.getToken(getApplicationContext());

                Call<Answer> call = service.createAnswer(auth_token_string, mQuestion.getId(), mAnswer);

                call.enqueue(new Callback<Answer>() {
                    @Override
                    public void onResponse(Call<Answer> call, Response<Answer> response) {
                        if (response.isSuccessful()) {
                            mAnswer = response.body();
                            onRequestSuccess();
                        } else {
                            onRequestFailure(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Answer> call, Throwable t) {
                        onRequestFailure(401);
                    }
                });

            } else {
                Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snake_try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newAnswer();
                            }
                        }).show();
            }
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEditTextAnswer.getText().toString().trim())) {
            mTextInputLayoutAnswer.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutAnswer.setError(null);
        }

        return valid;
    }

    private void onRequestSuccess() {
        Intent intentResult = new Intent();
        intentResult.putExtra(Answer.TAG, mAnswer);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void onRequestFailure(int statusCode) {
        Toast.makeText(getApplicationContext(), "Requisição não completada, tente novamente! ", Toast.LENGTH_LONG).show();
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
            newAnswer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mEditTextAnswer", mEditTextAnswer.getText().toString());
        outState.putParcelable(Question.TAG, mQuestion);
        outState.putParcelable(Presentation.NAME, mPresentation);
        super.onSaveInstanceState(outState);
    }
}
