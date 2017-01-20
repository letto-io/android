package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Alternative;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Survey;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewSurveyActivity extends AppCompatActivity {
    private TextInputLayout mTextInputLayoutTitle, mTextInputLayoutQuestion, mTextInputLayoutAlternative;
    private EditText mEditTextTitle, mEditTextQuestion, mEditTextAlternative ;
    private View mRootLayout;
    private Instruction mInstruction;
    private Survey mSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        mRootLayout = findViewById(R.id.root);
        mTextInputLayoutTitle = (TextInputLayout) findViewById(R.id.til_title);
        mTextInputLayoutQuestion = (TextInputLayout) findViewById(R.id.til_question);
        mTextInputLayoutAlternative = (TextInputLayout) findViewById(R.id.til_alternative);

        mEditTextTitle = (EditText) findViewById(R.id.et_title);
        mEditTextQuestion = (EditText) findViewById(R.id.et_question);
        mEditTextAlternative = (EditText) findViewById(R.id.et_alternative);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mEditTextTitle.setText(savedInstanceState.getString("mEditTextTitle"));
            mEditTextQuestion.setText(savedInstanceState.getString("mEditTextQuestion"));
            mEditTextAlternative.setText(savedInstanceState.getString("mEditTextAlternative"));
        } else {
            if (getIntent().getExtras() != null) {
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
                if (mInstruction == null) {
                    Toast.makeText(getApplicationContext(), R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_new_survey);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.new_survey);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void newSurvey() {
        if (!validate()) {
            return;
        } else {
            DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
            if (mDetectConnection.existConnection()) {
                mSurvey = new Survey();
                mSurvey.setTitle(mEditTextQuestion.getText().toString());
                mSurvey.setQuestion(mEditTextQuestion.getText().toString());

                Alternative alternative = new Alternative();
                alternative.setDescription(mEditTextAlternative.getText().toString());

                List<Alternative> alternatives = new ArrayList<>();
                alternatives.add(alternative);

                mSurvey.setAlternatives(alternatives);

                Preference preference = new Preference();

                Call<Survey> request = Retrofit.getInstance().createInstructionSurveys(preference.getToken(getApplicationContext()), mInstruction.getLecture().getId(), mSurvey);
                request.enqueue(new Callback<Survey>() {
                    @Override
                    public void onResponse(Call<Survey> call, Response<Survey> response) {
                        if (response.isSuccessful()) {
                            mSurvey = response.body();
                            onRequestSuccess();
                        } else onRequestFailure();
                    }

                    @Override
                    public void onFailure(Call<Survey> call, Throwable t) {
                        onRequestFailure();
                    }
                });

            } else {
                Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snake_try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newSurvey();
                            }
                        }).show();
            }
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEditTextTitle.getText().toString().trim())) {
            mTextInputLayoutTitle.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutTitle.setError(null);
        }
        return valid;
    }

    private void onRequestSuccess() {
        Intent intentResult = new Intent();
        intentResult.putExtra(Survey.TAG, mSurvey);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void onRequestFailure() {
        Toast.makeText(getApplicationContext(), R.string.toast_request_not_completed, Toast.LENGTH_LONG).show();
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
            newSurvey();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Instruction.TAG, mInstruction);
        outState.putString("mEditTextQuestion", mEditTextQuestion.getText().toString());
        outState.putString("mEditTextTitle", mEditTextTitle.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
