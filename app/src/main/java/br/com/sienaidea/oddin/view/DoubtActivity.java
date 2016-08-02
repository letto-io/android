package br.com.sienaidea.oddin.view;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterViewPager;
import br.com.sienaidea.oddin.fragment.DoubtClosedFragment;
import br.com.sienaidea.oddin.fragment.DoubtOpenFragment;
import br.com.sienaidea.oddin.fragment.DoubtFragment;
import br.com.sienaidea.oddin.fragment.DoubtRankingFragment;
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.retrofitModel.Person;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.provider.SearchableProvider;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.DateUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DoubtActivity extends AppCompatActivity {
    private static String URL_GET_DOUBTS, URL_POST_LIKE, URL_DELETE_LIKE, URL_POST_UNDERSTAND, URL_REMOVE_UNDERSTAND, URL_POST_CHANGE_STATUS;
    private static String TAB_POSITION = "TAB_POSITION";
    static final int NEW_DOUBT_REQUEST = 1;
    private int mSelectedTabPosition;
    private Doubt mDoubt;
    private Person mPerson;
    private Presentation mPresentation;
    private Discipline mDiscipline;
    private List<Doubt> mList = new ArrayList<>();

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private DoubtFragment mDoubtFragment;
    private DoubtOpenFragment mDoubtOpenFragment;
    private DoubtClosedFragment mDoubtClosedFragment;
    private DoubtRankingFragment mDoubtRankingFragment;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AdapterViewPager mAdapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt);

        mTabLayout = (TabLayout) findViewById(R.id.tab_doubts);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.xml.selector));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList(Doubt.ARRAYLIST);
            mPresentation = savedInstanceState.getParcelable(Presentation.NAME);
            mDiscipline = savedInstanceState.getParcelable(Discipline.NAME);
            mDoubt = savedInstanceState.getParcelable(Doubt.NAME);
            mViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION));
            setupViewPager(mViewPager);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Presentation.NAME) != null && getIntent().getParcelableExtra(Discipline.NAME) != null) {
                mPresentation = getIntent().getParcelableExtra(Presentation.NAME);
                mDiscipline = getIntent().getParcelableExtra(Discipline.NAME);
                //URL_GET_DOUBTS = "controller/instruction/" + mPresentation.getInstruction_id() + "/presentation/" + mPresentation.getId() + "/doubt";
                getDoubts();
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_doubt);
        mToolbar.setTitle(mPresentation.getSubject());
        mToolbar.setSubtitle(mDiscipline.getNome());
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), NewDoubtActivity.class);
                intent.putExtra(Presentation.NAME, mPresentation);
                startActivityForResult(intent, NEW_DOUBT_REQUEST);
            }
        });

        if (!(mDiscipline.getProfile() == Discipline.TEACHER)) {
            fab.setVisibility(View.VISIBLE);
//            if (!(mPresentation.getStatus() == Presentation.FINISHED)) {
//                fab.setVisibility(View.VISIBLE);
//            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapterViewPager = new AdapterViewPager(fragmentManager);

        mDoubtFragment = DoubtFragment.newInstance(getListDoubt(), mDiscipline, mPresentation);
        mAdapterViewPager.addFragment(mDoubtFragment, DoubtFragment.ALL);

        mDoubtOpenFragment = DoubtOpenFragment.newInstance(getListOpen(), mDiscipline, mPresentation);
        mAdapterViewPager.addFragment(mDoubtOpenFragment, DoubtOpenFragment.OPEN);

        mDoubtClosedFragment = DoubtClosedFragment.newInstance(getListClose(), mDiscipline, mPresentation);
        mAdapterViewPager.addFragment(mDoubtClosedFragment, DoubtClosedFragment.CLOSED);

        mDoubtRankingFragment = DoubtRankingFragment.newInstance(getListDoubt(), mDiscipline, mPresentation);
        mAdapterViewPager.addFragment(mDoubtRankingFragment, DoubtRankingFragment.RANKING);

        viewPager.setAdapter(mAdapterViewPager);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void getDoubts() {
        BossClient.get(URL_GET_DOUBTS, null, CookieUtil.getCookie(getApplicationContext()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject doubts = response.getJSONObject("doubts");
                    Log.d("DOUBTS", doubts.toString());

                    Iterator keys = doubts.keys();

                    JSONArray jsonArray = new JSONArray();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        jsonArray.put(doubts.get(key));
                    }

                    mList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        String tempTimeFormat = DateUtil.getTimeFormat(jsonArray.getJSONObject(i).getString("createdat"));
                        mDoubt = new Doubt();
                        mDoubt.setId(jsonArray.getJSONObject(i).getInt("id"));
                        mDoubt.setLike(jsonArray.getJSONObject(i).getBoolean("like"));
                        mDoubt.setLikes(jsonArray.getJSONObject(i).getInt("likes"));
                        mDoubt.setContributions(jsonArray.getJSONObject(i).getInt("contributions"));
                        mDoubt.setCreatedat(jsonArray.getJSONObject(i).getString("createdat"));
                        mDoubt.setTime(tempTimeFormat);
                        mDoubt.setAnonymous(jsonArray.getJSONObject(i).getBoolean("anonymous"));
                        mDoubt.setText(jsonArray.getJSONObject(i).getString("text"));
                        mDoubt.setStatus(jsonArray.getJSONObject(i).getInt("status"));
                        mDoubt.setPresentation_id(jsonArray.getJSONObject(i).getInt("presentationid"));

                        mPerson = new Person();
                        //mPerson.setId(jsonArray.getJSONObject(i).getJSONObject("person").getString("id"));
                        mPerson.setName(jsonArray.getJSONObject(i).getJSONObject("person").getString("name"));

                        mDoubt.setPerson(mPerson);

                        addList(mDoubt);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSelectedTabPosition = mTabLayout.getSelectedTabPosition();
                setupViewPager(mViewPager);
                mViewPager.setCurrentItem(mSelectedTabPosition);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //doubtFragment.swipeRefreshStop();
            }
        });
    }

    public void like(final int position, Doubt doubt) {
        URL_POST_LIKE = "controller/instruction/" + mDiscipline.getInstruction_id() + "/presentation/" + doubt.getPresentation_id() + "/doubt/" + doubt.getId() + "/like";

        BossClient.post(URL_POST_LIKE, CookieUtil.getCookie(getApplicationContext()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mDoubtFragment.notifyLike(position, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401) {
                    Toast.makeText(getApplication(), "Você não pode ranquear sua propria dúvida!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeLike(final int position, Doubt doubt) {
        URL_DELETE_LIKE = "controller/instruction/" + mDiscipline.getInstruction_id() + "/presentation/" + doubt.getPresentation_id() + "/doubt/" + doubt.getId() + "/like";

        BossClient.delete(URL_DELETE_LIKE, CookieUtil.getCookie(getApplicationContext()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mDoubtFragment.notifyLike(position, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplication(), "Não foi possível completar sua requisição", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void understand(final int position, Doubt doubt) {
        URL_POST_UNDERSTAND = "controller/instruction/" + mDiscipline.getInstruction_id() + "/presentation/" + doubt.getPresentation_id() + "/doubt/" + doubt.getId() + "/understand";

        BossClient.post(URL_POST_UNDERSTAND, CookieUtil.getCookie(getApplicationContext()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mDoubtFragment.notifyUnderstand(position, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                switch (statusCode) {
                    case 401:
                        Toast.makeText(getApplication(), "401 - Inautorizado", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplication(), "Não foi possível completar sua requisição", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeUnderstand(final int position, Doubt doubt) {
        URL_REMOVE_UNDERSTAND = "controller/instruction/" + mDiscipline.getInstruction_id() + "/presentation/" + doubt.getPresentation_id() + "/doubt/" + doubt.getId() + "/understand";

        BossClient.post(URL_REMOVE_UNDERSTAND, CookieUtil.getCookie(getApplicationContext()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mDoubtFragment.notifyUnderstand(position, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplication(), R.string.error_could_not_complete_your_request, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeStatus(final int position, final Doubt doubt, final int status) {
        URL_POST_CHANGE_STATUS = "controller/instruction/" + mDiscipline.getInstruction_id() + "/presentation/" + doubt.getPresentation_id() + "/doubt/" + doubt.getId() + "/change-status";

        HttpEntity entity = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", status);

            entity = new StringEntity(jsonObject.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        BossClient.post(getApplicationContext(), URL_POST_CHANGE_STATUS, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                DoubtFragment fragment = (DoubtFragment) mAdapterViewPager.getItem(mViewPager.getCurrentItem());

                if (fragment instanceof DoubtOpenFragment) {
                    doubt.setStatus(status);
                    fragment.removeItem(position);
                    mDoubtClosedFragment.addItemPosition(0, doubt);
                    mDoubtFragment.notifyDataSetChanged();
                } else if (fragment instanceof DoubtClosedFragment) {
                    doubt.setStatus(status);
                    mDoubtOpenFragment.addItemPosition(0, doubt);
                    fragment.removeItem(position);
                    mDoubtFragment.notifyDataSetChanged();
                } else {

                    fragment.notifyLock(position, status);
                    mDoubtRankingFragment.notifyDataSetChanged();

                    if (status == Doubt.CLOSED) {
                        doubt.setStatus(status);
                        mDoubtClosedFragment.addItemPosition(0, doubt);
                    } else {
                        mDoubtClosedFragment.removeItem(doubt);
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplication(), "Não foi possivel completar sua requisição", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void changeStatusDoubt(final int position, Doubt doubt, final int status) {
        DetectConnection mDetectConnection = new DetectConnection(getApplicationContext());

        if (mDetectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            JSONArray json = new JSONArray();
            json.put(status);

            Gson gson = new Gson();

            Call<Void> call = service.changeStatusDoubt(CookieUtil.getCookie(getApplicationContext()),
                    String.valueOf(mDiscipline.getInstruction_id()),
                    String.valueOf(mPresentation.getId()),
                    String.valueOf(doubt.getId()),
                    gson.toJson(json));

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Requisição não completada, tente novamente! ", Toast.LENGTH_LONG).show();
                        return;
                    }
                    mDoubtFragment.notifyLock(position, status);
                    getDoubts();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Falha na requisição ao servidor!", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Sem Internet!", Toast.LENGTH_LONG).show();
        }
    }

    private List<Doubt> getListDoubt() {
        return mList;
    }

    private List<Doubt> getListOpen() {
        List<Doubt> listAux = new ArrayList<>();

        for (Doubt doubt : mList) {
            if ((doubt.getStatus() == 0) || (doubt.getStatus() == 1)) {
                listAux.add(doubt);
            }
        }
        return listAux;
    }

    public List<Doubt> getListClose() {
        List<Doubt> listaAux = new ArrayList<>();

        for (Doubt doubt : mList) {
            if (doubt.getStatus() == 2) {
                listaAux.add(doubt);
            }
        }
        return listaAux;
    }

    private void addList(Doubt doubt) {
        mList.add(doubt);
    }

    public Discipline getDiscipline() {
        return mDiscipline;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_doubts, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Pesquisar...");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_attachment) {
            Intent intent = new Intent(this, PresentationDetailsActivity.class);
            intent.putExtra(Presentation.NAME, mPresentation);
            intent.putExtra(Discipline.NAME, mDiscipline);
            startActivity(intent);
        } else if (id == R.id.action_remove_sugestions) {
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);

            searchRecentSuggestions.clearHistory();

            Toast.makeText(this, "Históricos removidos", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Doubt.ARRAYLIST, (ArrayList<Doubt>) mList);
        outState.putParcelable(Presentation.NAME, mPresentation);
        outState.putParcelable(Discipline.NAME, mDiscipline);
        outState.putParcelable(Doubt.NAME, mDoubt);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_DOUBT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Doubt doubt = data.getParcelableExtra(Doubt.NAME);

                Toast.makeText(getApplication(), "Nova dúvida adicionada...", Toast.LENGTH_SHORT).show();
                mDoubtFragment.addItemPosition(0, doubt);
                mDoubtOpenFragment.addItemPosition(0, doubt);
            }
        }
    }

    @Override
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putParcelableArrayListExtra(Doubt.NAME, (ArrayList<Doubt>) mList);
            intent.putExtra(Presentation.NAME, mPresentation);
            intent.putExtra(Discipline.NAME, mDiscipline);
        }
        super.startActivity(intent);
    }
}
