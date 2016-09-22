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
import br.com.sienaidea.oddin.adapter.NoticeAdapter;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Notice;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends AppCompatActivity {
    private static final int ACTION_NEW_NOTICE = 12;
    private Instruction mInstruction;
    private List<Notice> mList;
    private RecyclerView mRecyclerView;
    private NoticeAdapter mNoticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mList = savedInstanceState.getParcelableArrayList(Notice.TAG);
            onRequestSuccess();
        } else {
            mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
            if (mInstruction != null) {
                getNotices();
            } else {
                finish();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_notice);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.notices);
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
                    Intent intent = new Intent(getApplicationContext(), NewNoticeActivity.class);
                    intent.putExtra(Instruction.TAG, mInstruction);
                    startActivityForResult(intent, ACTION_NEW_NOTICE);
                }
            });
        } else fab.setVisibility(View.GONE);
    }

    private void getNotices() {
        Preference preference = new Preference();
        Call<List<Notice>> request = Retrofit.getInstance().getInstructionNotices(preference.getToken(getApplicationContext()), mInstruction.getId());
        request.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                if (response.isSuccessful()) {
                    mList = response.body();
                    onRequestSuccess();
                }
            }

            @Override
            public void onFailure(Call<List<Notice>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.toast_request_not_completed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRequestSuccess() {
        mNoticeAdapter = new NoticeAdapter(this, mList);
        mRecyclerView.setAdapter(mNoticeAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_NEW_NOTICE) {
                mList.add((Notice) data.getParcelableExtra(Notice.TAG));
                mNoticeAdapter.notifyDataSetChanged();
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
        outState.putParcelableArrayList(Notice.TAG, (ArrayList<Notice>) mList);
        super.onSaveInstanceState(outState);
    }
}
