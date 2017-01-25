package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.AlternativeFragment;
import br.com.sienaidea.oddin.retrofitModel.Alternative;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Survey;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import br.com.sienaidea.oddin.util.DateUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyDetailsActivity extends AppCompatActivity {
    private Survey mSurvey;
    private Instruction mInstruction;
    private AlternativeFragment mAlternativeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvDate = (TextView) findViewById(R.id.tv_date);
        TextView tvDescription = (TextView) findViewById(R.id.tv_description);

        if (getIntent().getExtras() != null) {
            mSurvey = getIntent().getParcelableExtra(Survey.TAG);
            mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
        } else {
            Toast.makeText(this, getString(R.string.toast_fails_to_start), Toast.LENGTH_SHORT).show();
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_survey_details);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.surveys);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTitle.setText(mSurvey.getTitle());
        tvDescription.setText(mSurvey.getQuestion());
        tvDate.setText(DateUtil.getDateUFCFormat(mSurvey.getCreated_at()));

        showChoices();
    }

    private void showChoices() {
        mAlternativeFragment = (AlternativeFragment) getSupportFragmentManager().findFragmentByTag(AlternativeFragment.TAG);
        if (mAlternativeFragment != null) {
            mAlternativeFragment.notifyDataSetChanged();
        } else {
            mAlternativeFragment = AlternativeFragment.newInstance(mSurvey);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_survey_details, mAlternativeFragment, AlternativeFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void chooseAlternative(final int position, final Survey survey) {
        Preference preference = new Preference();

        Call<Survey> request = Retrofit.getInstance().chooseAlternative(preference.getToken(getApplicationContext()), survey.getAlternatives().get(position).getId());
        request.enqueue(new Callback<Survey>() {
            @Override
            public void onResponse(Call<Survey> call, Response<Survey> response) {
                if (response.isSuccessful()){
                    survey.setMy_vote(response.body().getMy_vote());
                    survey.setAlternatives(response.body().getAlternatives());
                    mAlternativeFragment.notifyDataSetChanged();

                    Intent intent = new Intent();
                    intent.putExtra(Survey.TAG, survey);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                }
            }

            @Override
            public void onFailure(Call<Survey> call, Throwable t) {

            }
        });
    }


}
