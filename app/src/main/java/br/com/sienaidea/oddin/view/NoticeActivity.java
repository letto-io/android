package br.com.sienaidea.oddin.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.NoticeAdapter;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Notice;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.server.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends AppCompatActivity {
    private Instruction mInstruction;
    private List<Notice> mList;
    private RecyclerView mRecyclerView;
    private NoticeAdapter mNoticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        if (savedInstanceState == null){
            mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
            if (mInstruction != null){
                getNotices();
            }else {
                finish();
            }
        }else {
            // TODO: 21/09/2016 recuperar os dados do savedInstanceState
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_notice);
        toolbar.setTitle(mInstruction.getLecture().getName());
        toolbar.setSubtitle(R.string.notices);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void getNotices(){
        Preference preference = new Preference();
        Call<List<Notice>> request = Retrofit.getInstance().getInstructionNotices(preference.getToken(getApplicationContext()), mInstruction.getId());
        request.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                if (response.isSuccessful()){
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
    
    private void onRequestSuccess(){
        mNoticeAdapter = new NoticeAdapter(this, mList);        
        mRecyclerView.setAdapter(mNoticeAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        // TODO: 21/09/2016 salvar os dados aqui 
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
