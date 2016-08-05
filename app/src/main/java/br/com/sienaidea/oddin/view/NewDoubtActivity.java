package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.support.design.widget.Snackbar;
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
    private CheckBox mChkAnonymous;
    private EditText mEdtDoubt;
    private Presentation mPresentation;
    private Question mQuestion;
    private View mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_doubt);

        mEdtDoubt = (EditText) findViewById(R.id.edt_doubt);
        mChkAnonymous = (CheckBox) findViewById(R.id.chk_anonimous);
        mRootLayout = findViewById(R.id.root);

        if (savedInstanceState != null) {
            mEdtDoubt.setText(savedInstanceState.getString("mEdtDoubt"));
            mChkAnonymous.setChecked(savedInstanceState.getBoolean("checked"));
            mPresentation = savedInstanceState.getParcelable(Presentation.TAG);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Presentation.TAG) != null) {
                mPresentation = getIntent().getParcelableExtra(Presentation.TAG);
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_new_doubt);
        mToolbar.setTitle("Nova dúvida");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

//    private void attemptNewDoubt() {
//        DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());
//
//        if (mDetectConnection.existConnection()) {
//            if (TextUtils.isEmpty(mEdtDoubt.getText().toString())) {
//                mEdtDoubt.setError(getString(R.string.error_field_required));
//                mEdtDoubt.requestFocus();
//            } else {
//                HttpEntity entity = null;
//
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("text", mEdtDoubt.getText().toString());
//                    jsonObject.put("anonymous", mChkAnonymous.isChecked());
//
//                    entity = new StringEntity(jsonObject.toString(), "UTF-8");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                final Intent intentResult = new Intent();
//
//                BossClient.post(getApplicationContext(), URL_POST_DOUBTS, entity, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        mEdtDoubt.setText("");
//                        mChkAnonymous.setChecked(false);
//
//                        Doubt mDoubt = new Doubt();
//
//                        try {
//                            String tempTimeFormat = DateUtil.getTimeFormat(response.getString("created_at"));
//                            mDoubt.setId(response.getInt("id"));
//                            mDoubt.setLike(false);
//                            mDoubt.setLikes(0);
//                            mDoubt.setContributions(0);
//                            mDoubt.setCreatedat(response.getString("created_at"));
//                            mDoubt.setTime(tempTimeFormat);
//                            mDoubt.setAnonymous(response.getBoolean("anonymous"));
//                            mDoubt.setText(response.getString("text"));
//                            mDoubt.setStatus(response.getInt("status"));
//                            mDoubt.setPresentation_id(response.getInt("presentation_id"));
//
//                            Person mPerson = new Person();
//                            mPerson.setName(response.getJSONObject("person").getString("name"));
//                            //mPerson.setId(response.getString("person_id"));
//
//                            mDoubt.setPerson(mPerson);
//
//                            intentResult.putExtra(Doubt.NAME, mDoubt);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        Toast.makeText(getApplication(), "Enviado...", Toast.LENGTH_LONG).show();
//                        setResult(RESULT_OK, intentResult);
//                        finish();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        Toast.makeText(getApplication(), "Falha ao eviar", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        } else {
//            Toast.makeText(getApplication(), "Sem conexão", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void sendNewQuestion() {
        DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());

        if (mDetectConnection.existConnection()) {
            if (TextUtils.isEmpty(mEdtDoubt.getText().toString().trim())) {
                mEdtDoubt.setError(getString(R.string.error_field_required));
                mEdtDoubt.requestFocus();
            } else {
                mQuestion = new Question();
                mQuestion.setText(mEdtDoubt.getText().toString());
                mQuestion.setAnonymous(mChkAnonymous.isChecked());
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
            }
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

    private void onRequestSuccess() {
        Toast.makeText(getApplication(), "Enviado...", Toast.LENGTH_LONG).show();
        Intent intentResult = new Intent();
        intentResult.putExtra(Question.TAG, mQuestion);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void onRequestFailure(int statusCode) {
        Toast.makeText(getApplicationContext(), "Requisição não completada, tente novamente! ", Toast.LENGTH_LONG).show();
        //TODO
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
            sendNewQuestion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mEdtDoubt", mEdtDoubt.toString());
        outState.putBoolean("checked", mChkAnonymous.isChecked());
        outState.putParcelable(Presentation.TAG, mPresentation);
        super.onSaveInstanceState(outState);
    }
}
