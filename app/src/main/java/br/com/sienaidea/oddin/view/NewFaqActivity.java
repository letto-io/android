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

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Faq;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewFaqActivity extends AppCompatActivity {
    private TextInputLayout mTextInputLayoutQuestion, mTextInputLayoutAnswer;
    private EditText mEditTextQuestion, mEditTextAnswer;
    private View mRootLayout;
    private Instruction mInstruction;
    private Faq mFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_faq);

        mRootLayout = findViewById(R.id.root);
        mTextInputLayoutQuestion = (TextInputLayout) findViewById(R.id.til_question);
        mTextInputLayoutAnswer = (TextInputLayout) findViewById(R.id.til_answer);
        mEditTextQuestion = (EditText) findViewById(R.id.et_question);
        mEditTextAnswer = (EditText) findViewById(R.id.et_answer);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mEditTextQuestion.setText(savedInstanceState.getString("mEditTextQuestion"));
            mEditTextAnswer.setText(savedInstanceState.getString("mEditTextAnswer"));
        } else {
            if (getIntent().getExtras() != null) {
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
                if (mInstruction == null) {
                    Toast.makeText(getApplicationContext(), R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_new_faq);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.new_faq);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void newFaq() {
        if (!validate()) {
            return;
        } else {
            DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
            if (mDetectConnection.existConnection()) {
                mFaq = new Faq();
                mFaq.setQuestion(mEditTextQuestion.getText().toString());
                mFaq.setAnswer(mEditTextAnswer.getText().toString());

                Preference preference = new Preference();

                Call<Faq> request = Retrofit.getInstance().createInstructionFAQs(preference.getToken(getApplicationContext()), mInstruction.getLecture().getId(), mFaq);
                request.enqueue(new Callback<Faq>() {
                    @Override
                    public void onResponse(Call<Faq> call, Response<Faq> response) {
                        if (response.isSuccessful()) {
                            mFaq = response.body();
                            onRequestSuccess();
                        } else onRequestFailure();
                    }

                    @Override
                    public void onFailure(Call<Faq> call, Throwable t) {
                        onRequestFailure();
                    }
                });

            } else {
                Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snake_try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newFaq();
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
        intentResult.putExtra(Faq.TAG, mFaq);
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
            newFaq();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Instruction.TAG, mInstruction);
        outState.putString("mEditTextQuestion", mEditTextQuestion.getText().toString());
        outState.putString("mEditTextAnswer", mEditTextAnswer.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
