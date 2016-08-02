package br.com.sienaidea.oddin.view;

import android.content.Intent;
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

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.retrofitModel.Person;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.util.DateUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewDoubtActivity extends AppCompatActivity {
    private static String URL_POST_DOUBTS;
    private CheckBox mChkAnonymous;
    private EditText mEdtDoubt;
    private Presentation mPresentation;
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
            mPresentation = savedInstanceState.getParcelable(Presentation.NAME);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Presentation.NAME) != null) {
                mPresentation = getIntent().getParcelableExtra(Presentation.NAME);
                //URL_POST_DOUBTS = "controller/instruction/" + mPresentation.getInstruction_id() + "/presentation/" + mPresentation.getId() + "/doubt";
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

    private void attemptNewDoubt() {
        DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());

        if (mDetectConnection.existConnection()) {
            if (TextUtils.isEmpty(mEdtDoubt.getText().toString())) {
                mEdtDoubt.setError(getString(R.string.error_field_required));
                mEdtDoubt.requestFocus();
            } else {
                HttpEntity entity = null;

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("text", mEdtDoubt.getText().toString());
                    jsonObject.put("anonymous", mChkAnonymous.isChecked());

                    entity = new StringEntity(jsonObject.toString(), "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final Intent intentResult = new Intent();

                BossClient.post(getApplicationContext(), URL_POST_DOUBTS, entity, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mEdtDoubt.setText("");
                        mChkAnonymous.setChecked(false);

                        Doubt mDoubt = new Doubt();

                        try {
                            String tempTimeFormat = DateUtil.getTimeFormat(response.getString("created_at"));
                            mDoubt.setId(response.getInt("id"));
                            mDoubt.setLike(false);
                            mDoubt.setLikes(0);
                            mDoubt.setContributions(0);
                            mDoubt.setCreatedat(response.getString("created_at"));
                            mDoubt.setTime(tempTimeFormat);
                            mDoubt.setAnonymous(response.getBoolean("anonymous"));
                            mDoubt.setText(response.getString("text"));
                            mDoubt.setStatus(response.getInt("status"));
                            mDoubt.setPresentation_id(response.getInt("presentation_id"));

                            Person mPerson = new Person();
                            mPerson.setName(response.getJSONObject("person").getString("name"));
                            //mPerson.setId(response.getString("person_id"));

                            mDoubt.setPerson(mPerson);

                            intentResult.putExtra(Doubt.NAME, mDoubt);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplication(), "Enviado...", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK, intentResult);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(getApplication(), "Falha ao eviar", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            Toast.makeText(getApplication(), "Sem conexão", Toast.LENGTH_SHORT).show();
        }
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
            attemptNewDoubt();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mEdtDoubt", mEdtDoubt.toString());
        outState.putBoolean("checked", mChkAnonymous.isChecked());
        outState.putParcelable(Presentation.NAME, mPresentation);
        super.onSaveInstanceState(outState);
    }
}
