package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.FaqAdapter;
import br.com.sienaidea.oddin.fragment.FaqFragment;
import br.com.sienaidea.oddin.fragment.MaterialDisciplineFragment;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Faq;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Notice;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQActivity extends AppCompatActivity {
    private static final int ACTION_NEW_FAQ = 123;
    private Instruction mInstruction;
    private List<Faq> mList = new ArrayList<>();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FaqFragment mFaqFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mList = savedInstanceState.getParcelableArrayList(Faq.TAG);
            onRequestSuccess();
        } else {
            mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
            if (mInstruction != null) {
                getFAQs();
            } else {
                finish();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_faq);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.faqs);
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
                    Intent intent = new Intent(getApplicationContext(), NewFaqActivity.class);
                    intent.putExtra(Instruction.TAG, mInstruction);
                    startActivityForResult(intent, ACTION_NEW_FAQ);
                }
            });
        } else fab.setVisibility(View.GONE);
    }

    private void getFAQs() {
        Preference preference = new Preference();
        Call<List<Faq>> request = Retrofit.getInstance().getInstructionFAQs(preference.getToken(getApplicationContext()), mInstruction.getId());
        request.enqueue(new Callback<List<Faq>>() {
            @Override
            public void onResponse(Call<List<Faq>> call, Response<List<Faq>> response) {
                if (response.isSuccessful()) {
                    mList = response.body();
                    onRequestSuccess();
                }
            }

            @Override
            public void onFailure(Call<List<Faq>> call, Throwable t) {
                onRequestFailure();
            }
        });
    }

    public void deleteFaq(final int position, Faq faq) {
        Preference preference = new Preference();
        final Call<Void> request = Retrofit.getInstance().deleteFAQ(preference.getToken(getApplicationContext()), faq.getId());
        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mList.remove(position);
                    mFaqFragment.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // TODO: 1/20/2017
            }
        });

    }

    private void onRequestSuccess() {
        mFaqFragment = (FaqFragment) fragmentManager.findFragmentByTag(FaqFragment.TAG);
        if (mFaqFragment != null) {
            mFaqFragment.notifyDataSetChanged();
        } else {
            mFaqFragment = FaqFragment.newInstance(mList);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_faq, mFaqFragment, FaqFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    private void onRequestFailure() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_NEW_FAQ) {
                mList.add((Faq) data.getParcelableExtra(Faq.TAG));
                mFaqFragment.notifyDataSetChanged();
                Toast.makeText(this, R.string.toast_new_notice_added, Toast.LENGTH_SHORT).show();
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
        outState.putParcelableArrayList(Notice.TAG, (ArrayList<Faq>) mList);
        super.onSaveInstanceState(outState);
    }
}
