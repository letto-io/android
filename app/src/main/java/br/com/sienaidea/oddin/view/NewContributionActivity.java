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
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewContributionActivity extends AppCompatActivity {
    private static String URL_POST_CONTRIBUTION;
    private EditText mEtTextContribution;
    private View mRootLayout;
    private Doubt mDoubt;
    private Presentation mPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contribution);

        mEtTextContribution = (EditText) findViewById(R.id.et_text_contribution);
        mRootLayout = findViewById(R.id.root);

        if (savedInstanceState != null) {
            mDoubt = savedInstanceState.getParcelable(Doubt.NAME);
            mPresentation = savedInstanceState.getParcelable(Presentation.NAME);
            mEtTextContribution.setText(savedInstanceState.getString("mEtTextContribution"));
        } else {
            if (getIntent().getExtras() != null) {
                mDoubt = getIntent().getParcelableExtra(Doubt.NAME);
                mPresentation = getIntent().getParcelableExtra(Presentation.NAME);

                //URL_POST_CONTRIBUTION = "controller/instruction/"+mPresentation.getInstruction_id()+"/presentation/"+mPresentation.getId()+"/doubt/"+mDoubt.getId()+"/contribution";

                if (mDoubt == null || mPresentation == null) {
                    Toast.makeText(getApplicationContext(), "Falha ao iniciar!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_new_contribution);
        mToolbar.setTitle("Responder");
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void newContribution() {
        DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());

        if (mDetectConnection.existConnection()) {
            String text = mEtTextContribution.getText().toString();
            // text = text.replace("\"", "");

            // Check for a valid text.
            if (TextUtils.isEmpty(text)) {
                mEtTextContribution.setError(getString(R.string.error_field_required));
                mEtTextContribution.requestFocus();
            } else {
                sendRetrofit(text);
                //sendAsyncHttp(text);
            }

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newContribution();
                        }
                    }).show();
        }
    }

    private void sendRetrofit(String text) {
        //.addConverterFactory(GsonConverterFactory.create())
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpApi.API_URL)
                .build();
        HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

        RequestBody textBody = RequestBody.create(MediaType.parse("text/plain"), text);


        Call<Void> call = service.postTextContributionMultiPart(CookieUtil.getCookie(getApplicationContext()),
                String.valueOf(mPresentation.getId()), //instruction id e não presentation id
                String.valueOf(mPresentation.getId()),
                String.valueOf(mDoubt.getId()),
                textBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Requisição não completada, tente novamente! ", Toast.LENGTH_LONG).show();
                    return;
                }

                //mPresentation = response.body();
                Toast.makeText(getApplicationContext(), "Enviado!", Toast.LENGTH_LONG).show();

                Intent intentResult = new Intent();
                //intentResult.putExtra(Presentation.NAME, "coloco o retorno aqui");
                setResult(RESULT_OK, intentResult);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falha na requisição ao servidor!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendAsyncHttp(String text) {
        HttpEntity entity = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", text);

            entity = new StringEntity(jsonObject.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Intent intentResult = new Intent();

        BossClient.post(getApplicationContext(), URL_POST_CONTRIBUTION, entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mEtTextContribution.setText("");

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
            newContribution();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mEtTextContribution", mEtTextContribution.getText().toString());
        outState.putParcelable(Doubt.NAME, mDoubt);
        outState.putParcelable(Presentation.NAME, mPresentation);
        super.onSaveInstanceState(outState);
    }
}
