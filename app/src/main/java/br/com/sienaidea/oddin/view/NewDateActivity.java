package br.com.sienaidea.oddin.view;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Date;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Notice;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDateActivity extends AppCompatActivity {
    private TextInputLayout mTextInputLayoutDate, mTextInputLayoutSubject, mTextInputLayoutDescription;
    private EditText mEditTextDate, mEditTextSubject, mEditTextDescription;
    private View mRootLayout;
    private Instruction mInstruction;
    private Date mDate;
    private int mYear, mMonth, mDay;
    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_date);

        mRootLayout = findViewById(R.id.root);
        mTextInputLayoutDate = (TextInputLayout) findViewById(R.id.til_date);
        mTextInputLayoutSubject = (TextInputLayout) findViewById(R.id.til_subject);
        mTextInputLayoutDescription = (TextInputLayout) findViewById(R.id.til_description);
        mEditTextDate = (EditText) findViewById(R.id.et_date);
        mEditTextSubject = (EditText) findViewById(R.id.et_subject);
        mEditTextDescription = (EditText) findViewById(R.id.et_description);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mEditTextDate.setText(savedInstanceState.getString("mEditTextDate"));
            mEditTextSubject.setText(savedInstanceState.getString("mEditTextSubject"));
            mEditTextDescription.setText(savedInstanceState.getString("mEditTextDescription"));
        } else {
            if (getIntent().getExtras() != null) {
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
                if (mInstruction == null) {
                    Toast.makeText(getApplicationContext(), R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_new_date);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.new_date);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initDatePicker();
    }

    private void initDatePicker() {
        // Get Current Date
        myCalendar = Calendar.getInstance();
        mYear = myCalendar.get(Calendar.YEAR);
        mMonth = myCalendar.get(Calendar.MONTH);
        mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mEditTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        mEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    private void newDate() {
        if (!validate()) {
            return;
        } else {
            DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
            if (mDetectConnection.existConnection()) {
                mDate = new Date();
                mDate.setDate(mEditTextDate.getText().toString());
                mDate.setSubject(mEditTextSubject.getText().toString());
                mDate.setText(mEditTextDescription.getText().toString());

                Preference preference = new Preference();

                Call<Date> request = Retrofit.getInstance().createInstructionDate(preference.getToken(getApplicationContext()), mInstruction.getLecture().getId(), mDate);
                request.enqueue(new Callback<Date>() {
                    @Override
                    public void onResponse(Call<Date> call, Response<Date> response) {
                        if (response.isSuccessful()) {
                            mDate = response.body();
                            onRequestSuccess();
                        } else onRequestFailure();
                    }

                    @Override
                    public void onFailure(Call<Date> call, Throwable t) {
                        onRequestFailure();
                    }
                });

            } else {
                Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snake_try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newDate();
                            }
                        }).show();
            }
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEditTextDate.getText().toString().trim())) {
            mTextInputLayoutDate.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutDate.setError(null);
        }

        if (TextUtils.isEmpty(mEditTextSubject.getText().toString().trim())) {
            mTextInputLayoutSubject.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutSubject.setError(null);
        }

        if (TextUtils.isEmpty(mEditTextDescription.getText().toString().trim())) {
            mTextInputLayoutDescription.setError(getResources().getString(R.string.error_field_required));
            valid = false;
        } else {
            mTextInputLayoutDescription.setError(null);
        }
        return valid;
    }

    private void onRequestSuccess() {
        Intent intentResult = new Intent();
        intentResult.putExtra(Date.TAG, mDate);
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
            newDate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Instruction.TAG, mInstruction);
        outState.putString("mEditTextDate", mEditTextDate.getText().toString());
        outState.putString("mEditTextDescription", mEditTextDescription.getText().toString());
        outState.putString("mEditTextDescription", mEditTextDescription.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
