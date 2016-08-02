package br.com.sienaidea.oddin.view;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterViewPager;
import br.com.sienaidea.oddin.fragment.PresentationClosedFragment;
import br.com.sienaidea.oddin.fragment.PresentationOpenFragment;
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.provider.SearchableProvider;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Lecture;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PresentationActivity extends AppCompatActivity {
    private static String TAB_POSITION = "TAB_POSITION";
    private static String URL_GET_PRESENTATION;
    private static String URL_POST_CLOSE_PRESENTATION;
    private static final int NEW_PRESENTATION_REQUEST = 0;
    private List<Presentation> mList = new ArrayList<>();
    private List<Instruction> mListInstruction = new ArrayList<>();
    private Presentation mPresentation;
    private Discipline mDiscipline;
    private Lecture mLecture;
    private Instruction mInstruction;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private PresentationClosedFragment presentationClosedFragment;
    private PresentationOpenFragment presentationOpenFragment;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AdapterViewPager mAdapterViewPager;
    private int mSelectedTabPosition;
    private View mRootLayout;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        mRootLayout = findViewById(R.id.root_presentation);

        mTabLayout = (TabLayout) findViewById(R.id.tab_presentation);
        mViewPager = (ViewPager) findViewById(R.id.vp_presentation);

        if (savedInstanceState != null) {
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION));
            mList = savedInstanceState.getParcelableArrayList(Presentation.TAG);
            mDiscipline = savedInstanceState.getParcelable(Discipline.NAME);

            setupViewPager(mViewPager);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Instruction.TAG) != null) {
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
                getPresentations();
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_presentation);
        mToolbar.setTitle(mInstruction.getLecture().getName());
        mToolbar.setSubtitle("Aulas");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_presentation);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), NewPresentationActivity.class);
                intent.putExtra(Instruction.TAG, mInstruction);
                startActivityForResult(intent, NEW_PRESENTATION_REQUEST);
            }
        });

        //TODO validate profile here:
//        if (mDiscipline.getProfile() == 2) {
//            fab.setVisibility(View.VISIBLE);
//        }
    }

    public void fabHide(){
        fab.hide();
    }

    public void fabShow(){
        fab.show();
    }

    private void setupViewPager(final ViewPager viewPager) {
        mAdapterViewPager = new AdapterViewPager(fragmentManager);

        presentationOpenFragment = PresentationOpenFragment.newInstance(getListOpen(), mDiscipline);
        presentationClosedFragment = PresentationClosedFragment.newInstance(getListClosed(), mDiscipline);

        mAdapterViewPager.addFragment(presentationOpenFragment, PresentationOpenFragment.OPEN);
        mAdapterViewPager.addFragment(presentationClosedFragment, PresentationClosedFragment.CLOSED);

        viewPager.setAdapter(mAdapterViewPager);

        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.xml.selector));
        mTabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_PRESENTATION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Presentation presentation = data.getParcelableExtra(Presentation.TAG);
                presentationOpenFragment.addItemPosition(0, presentation);
                Toast.makeText(this, R.string.toast_new_presentation_added, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getPresentations(){
        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Preference preference = new Preference();
            String auth_token_string = preference.getToken(getApplicationContext());

            Call<List<Presentation>> request = service.Presentations(auth_token_string, mInstruction.getId());

            request.enqueue(new Callback<List<Presentation>>() {
                @Override
                public void onResponse(Call<List<Presentation>> call, Response<List<Presentation>> response) {
                    if (response.isSuccessful()) {
                        mList.clear();
                        mList = response.body();
                        onRequestSuccess();
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Presentation>> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPresentations();
                        }
                    }).show();
        }
    }

    private void onRequestSuccess() {
        mSelectedTabPosition = mTabLayout.getSelectedTabPosition();
        setupViewPager(mViewPager);
        mViewPager.setCurrentItem(mSelectedTabPosition);
    }

    private void onRequestFailure(int statusCode) {
        if (statusCode == 401) {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            Toast.makeText(getApplicationContext(), R.string.error_session_expired, Toast.LENGTH_LONG).show();
            finish();
        } else {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            Toast.makeText(getApplicationContext(), R.string.error_session_expired, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private List<Presentation> getListOpen() {
        List<Presentation> listAux = new ArrayList<>();
        for (Presentation presentation : mList) {
            if (presentation.getStatus() == Presentation.OPEN) {
                listAux.add(presentation);
            }
        }
        return listAux;
    }

    private List<Presentation> getListClosed() {
        List<Presentation> listAux = new ArrayList<>();
        for (Presentation presentation : mList) {
            if (presentation.getStatus() == Presentation.FINISHED) {
                listAux.add(presentation);
            }
        }
        return listAux;
    }

    public Discipline getDiscipline() {
        return mDiscipline;
    }

    public void closePresentation(final int position, final Presentation presentation) {
        //URL_POST_CLOSE_PRESENTATION = "controller/instruction/" + presentation.getInstruction_id() + "/presentation/" + presentation.getId() + "/close";
        BossClient.post(URL_POST_CLOSE_PRESENTATION, CookieUtil.getCookie(getApplicationContext()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                presentation.setStatus(Presentation.FINISHED);
                presentationOpenFragment.removeItem(position);
                presentationClosedFragment.addItemPosition(0, presentation);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), R.string.error_could_not_complete_your_request, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_presentation, menu);

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
            Intent intent = new Intent(this, DisciplineDetailsActivity.class);
            intent.putExtra(Discipline.NAME, mDiscipline);
            startActivity(intent);
        } else if (id == R.id.action_participants) {
            Intent intent = new Intent(this, ParticipantsActivity.class);
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
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putParcelableArrayListExtra(Presentation.ARRAYLIST, (ArrayList<Presentation>) mList);
            intent.putExtra(Presentation.NAME, mPresentation);
            intent.putExtra(Discipline.NAME, mDiscipline);
        }
        super.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Presentation.TAG, (ArrayList<Presentation>) mList);
        outState.putParcelable(Discipline.NAME, mDiscipline);
        outState.putParcelable(Presentation.NAME, mPresentation);
        outState.putInt(TAB_POSITION, mTabLayout.getSelectedTabPosition());
        super.onSaveInstanceState(outState);
    }
}
