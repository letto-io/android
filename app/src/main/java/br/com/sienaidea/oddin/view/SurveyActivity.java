package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.SurveyFragment;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Survey;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyActivity extends AppCompatActivity {
    private static final int ACTION_NEW_SURVEY = 648;
    public static final int SURVEY_DETAIL_REQUEST = 458;
    private Instruction mInstruction;
    private List<Survey> mList = new ArrayList<>();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private SurveyFragment mSurveyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mList = savedInstanceState.getParcelableArrayList(Survey.TAG);
            onRequestSuccess();
        } else {
            mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
            if (mInstruction != null) {
                getFAQs();
            } else {
                finish();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_survey);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.surveys);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Preference preference = new Preference();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (preference.getUserProfile(getApplicationContext()) == Constants.INSTRUCTOR) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), NewSurveyActivity.class);
                    intent.putExtra(Instruction.TAG, mInstruction);
                    startActivityForResult(intent, ACTION_NEW_SURVEY);
                }
            });
        } else fab.setVisibility(View.GONE);
    }

    private void getFAQs() {
        Preference preference = new Preference();
        Call<List<Survey>> request = Retrofit.getInstance().getInstructionSurveys(preference.getToken(getApplicationContext()), mInstruction.getId());
        request.enqueue(new Callback<List<Survey>>() {
            @Override
            public void onResponse(Call<List<Survey>> call, Response<List<Survey>> response) {
                if (response.isSuccessful()) {
                    mList = response.body();
                    onRequestSuccess();
                }
            }

            @Override
            public void onFailure(Call<List<Survey>> call, Throwable t) {
                onRequestFailure();
            }
        });
    }

    public void deleteSurvey(final int position, Survey survey) {
        Preference preference = new Preference();
        final Call<Void> request = Retrofit.getInstance().deleteSurvey(preference.getToken(getApplicationContext()), survey.getId());
        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mList.remove(position);
                    mSurveyFragment.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // TODO: 1/20/2017
            }
        });

    }

    private void onRequestSuccess() {
        mSurveyFragment = (SurveyFragment) fragmentManager.findFragmentByTag(SurveyFragment.TAG);
        if (mSurveyFragment != null) {
            mSurveyFragment.notifyDataSetChanged();
        } else {
            mSurveyFragment = SurveyFragment.newInstance(mList);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_survey, mSurveyFragment, SurveyFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    private void onRequestFailure() {

    }

    public void showSurveyDetails(Survey survey) {
        Intent intent = new Intent(this, SurveyDetailsActivity.class);
        intent.putExtra(Instruction.TAG, mInstruction);
        intent.putExtra(Survey.TAG, survey);

        startActivityForResult(intent, SurveyActivity.SURVEY_DETAIL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_NEW_SURVEY) {
                mList.add((Survey) data.getParcelableExtra(Survey.TAG));
                mSurveyFragment.notifyDataSetChanged();
                Toast.makeText(this, R.string.toast_new_notice_added, Toast.LENGTH_SHORT).show();
            } else if (requestCode == SURVEY_DETAIL_REQUEST) {
                Survey survey = data.getParcelableExtra(Survey.TAG);
                int position = data.getIntExtra("position", 0);
                mList.get(position).setMy_vote(survey.getMy_vote() );
                mList.get(position).setAlternatives(survey.getAlternatives());
                mSurveyFragment.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Instruction.TAG, mInstruction);
        outState.putParcelableArrayList(Survey.TAG, (ArrayList<Survey>) mList);
        super.onSaveInstanceState(outState);
    }

    public Instruction getInstruction() {
        return mInstruction;
    }

}
