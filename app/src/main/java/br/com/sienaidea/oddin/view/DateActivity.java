package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import br.com.sienaidea.oddin.adapter.DateAdapter;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Date;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateActivity extends AppCompatActivity {
    private static final int ACTION_NEW_DATE = 13;
    private Instruction mInstruction;
    private List<Date> mList;
    private RecyclerView mRecyclerView;
    private DateAdapter mDateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mList = savedInstanceState.getParcelableArrayList(Date.TAG);
            onRequestSuccess();
        } else {
            mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
            if (mInstruction != null) {
                getNotices();
            } else {
                finish();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_date);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.dates);
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
                    Intent intent = new Intent(getApplicationContext(), NewDateActivity.class);
                    intent.putExtra(Instruction.TAG, mInstruction);
                    startActivityForResult(intent, ACTION_NEW_DATE);
                }
            });
        } else fab.setVisibility(View.GONE);
    }

    private void getNotices() {
        Preference preference = new Preference();
        Call<List<Date>> request = Retrofit.getInstance().getInstructionDates(preference.getToken(getApplicationContext()), mInstruction.getId());
        request.enqueue(new Callback<List<Date>>() {
            @Override
            public void onResponse(Call<List<Date>> call, Response<List<Date>> response) {
                if (response.isSuccessful()) {
                    mList = response.body();
                    onRequestSuccess();
                }
            }

            @Override
            public void onFailure(Call<List<Date>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.toast_request_not_completed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRequestSuccess() {
        mDateAdapter = new DateAdapter(this, mList);
        mRecyclerView.setAdapter(mDateAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_NEW_DATE) {
                mList.add((Date) data.getParcelableExtra(Date.TAG));
                mDateAdapter.notifyDataSetChanged();
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
        outState.putParcelableArrayList(Date.TAG, (ArrayList<Date>) mList);
        super.onSaveInstanceState(outState);
    }
}
