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
import br.com.sienaidea.oddin.retrofitModel.Notice;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewNoticeActivity extends AppCompatActivity {
    private TextInputLayout mTextInputLayoutSubject, mTextInputLayoutNotice;
    private EditText mEditTextSubject, mEditTextNotice;
    private View mRootLayout;
    private Instruction mInstruction;
    private Notice mNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice);

        mRootLayout = findViewById(R.id.root);
        mTextInputLayoutSubject = (TextInputLayout) findViewById(R.id.til_subject);
        mTextInputLayoutNotice = (TextInputLayout) findViewById(R.id.til_notice);
        mEditTextSubject = (EditText) findViewById(R.id.et_subject);
        mEditTextNotice = (EditText) findViewById(R.id.et_notice);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mEditTextSubject.setText(savedInstanceState.getString("mEditTextSubject"));
            mEditTextNotice.setText(savedInstanceState.getString("mEditTextNotice"));
        } else {
            if (getIntent().getExtras() != null) {
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
                if (mInstruction == null) {
                    Toast.makeText(getApplicationContext(), R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_new_notice);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.new_notice);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void newNotice() {
        if (!validate()) {
            return;
        } else {
            DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
            if (mDetectConnection.existConnection()) {
                mNotice = new Notice();
                mNotice.setSubject(mEditTextSubject.getText().toString());
                mNotice.setText(mEditTextNotice.getText().toString());

                Preference preference = new Preference();

                Call<Notice> request = Retrofit.getInstance().createInstructionNotices(preference.getToken(getApplicationContext()), mInstruction.getLecture().getId(), mNotice);
                request.enqueue(new Callback<Notice>() {
                    @Override
                    public void onResponse(Call<Notice> call, Response<Notice> response) {
                        if (response.isSuccessful()) {
                            mNotice = response.body();
                            onRequestSuccess();
                        } else onRequestFailure();
                    }

                    @Override
                    public void onFailure(Call<Notice> call, Throwable t) {
                        onRequestFailure();
                    }
                });

            } else {
                Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snake_try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newNotice();
                            }
                        }).show();
            }
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEditTextSubject.getText().toString().trim())) {
            mTextInputLayoutSubject.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutSubject.setError(null);
        }

        if (TextUtils.isEmpty(mEditTextNotice.getText().toString().trim())) {
            mTextInputLayoutNotice.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutNotice.setError(null);
        }
        return valid;
    }

    private void onRequestSuccess() {
        Intent intentResult = new Intent();
        intentResult.putExtra(Notice.TAG, mNotice);
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
            newNotice();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Instruction.TAG, mInstruction);
        outState.putString("mEditTextSubject", mEditTextSubject.getText().toString());
        outState.putString("mEditTextNotice", mEditTextNotice.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
