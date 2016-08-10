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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterViewPager;
import br.com.sienaidea.oddin.fragment.DoubtClosedFragment;
import br.com.sienaidea.oddin.fragment.DoubtOpenFragment;
import br.com.sienaidea.oddin.fragment.DoubtFragment;
import br.com.sienaidea.oddin.fragment.DoubtRankingFragment;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.provider.SearchableProvider;
import br.com.sienaidea.oddin.retrofitModel.Profile;
import br.com.sienaidea.oddin.retrofitModel.Question;
import br.com.sienaidea.oddin.retrofitModel.ResponseVote;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DoubtActivity extends AppCompatActivity {
    private static String TAB_POSITION = "TAB_POSITION";
    static final int NEW_DOUBT_REQUEST = 1;
    private int mSelectedTabPosition;
    private Presentation mPresentation;
    private Instruction mInstruction;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private DoubtFragment mDoubtFragment;
    private DoubtOpenFragment mDoubtOpenFragment;
    private DoubtClosedFragment mDoubtClosedFragment;
    private DoubtRankingFragment mDoubtRankingFragment;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AdapterViewPager mAdapterViewPager;

    private View mRootLayout;
    private List<Question> mList = new ArrayList<>();
    private Profile mProfile = new Profile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt);

        Preference preference = new Preference();
        mProfile.setProfile(preference.getUserProfile(getApplicationContext()));

        mTabLayout = (TabLayout) findViewById(R.id.tab_doubts);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.xml.selector));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));

        mRootLayout = findViewById(R.id.root_question);

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList(Question.TAG);
            mPresentation = savedInstanceState.getParcelable(Presentation.NAME);
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION));
            setupViewPager(mViewPager);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Presentation.TAG) != null) {
                mPresentation = getIntent().getParcelableExtra(Presentation.TAG);
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
//                if (mProfile.getProfile() == -1) {
//                    onRequestFailure(401);
//                }
                getQuestions();
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_doubt);
        mToolbar.setTitle(mPresentation.getSubject());
        mToolbar.setSubtitle(mInstruction.getLecture().getName());
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (mProfile.getProfile() != Constants.INSTRUCTOR) {
            if (mPresentation.getStatus() != Presentation.FINISHED) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplication(), NewDoubtActivity.class);
                        intent.putExtra(Presentation.TAG, mPresentation);
                        startActivityForResult(intent, NEW_DOUBT_REQUEST);
                    }
                });
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapterViewPager = new AdapterViewPager(fragmentManager);

        mDoubtFragment = DoubtFragment.newInstance(getListQuestions(), mPresentation);
        mAdapterViewPager.addFragment(mDoubtFragment, DoubtFragment.ALL);

        mDoubtOpenFragment = DoubtOpenFragment.newInstance(getListOpen(), mPresentation);
        mAdapterViewPager.addFragment(mDoubtOpenFragment, DoubtOpenFragment.OPEN);

        mDoubtClosedFragment = DoubtClosedFragment.newInstance(getListClose(), mPresentation);
        mAdapterViewPager.addFragment(mDoubtClosedFragment, DoubtClosedFragment.CLOSED);

        mDoubtRankingFragment = DoubtRankingFragment.newInstance(getListRanking(), mPresentation);
        mAdapterViewPager.addFragment(mDoubtRankingFragment, DoubtRankingFragment.RANKING);

        viewPager.setAdapter(mAdapterViewPager);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void getQuestions() {
        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            final HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Preference preference = new Preference();
            final String auth_token_string = preference.getToken(getApplicationContext());

            Call<List<Question>> request = service.Questions(auth_token_string, mPresentation.getId());

            request.enqueue(new Callback<List<Question>>() {
                @Override
                public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                    if (response.isSuccessful()) {
                        mList.clear();
                        mList = response.body();
                        onRequestSuccess();
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Question>> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getQuestions();
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

    public void voteQuestion(final Question question) {
        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            final HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Preference preference = new Preference();
            final String auth_token_string = preference.getToken(getApplicationContext());

            Call<ResponseVote> request;

            if (question.getMy_vote() != 0) {
                request = service.DownVoteQuestion(auth_token_string, question.getId());
            } else {
                request = service.UpVoteQuestion(auth_token_string, question.getId());
            }

            request.enqueue(new Callback<ResponseVote>() {
                @Override
                public void onResponse(Call<ResponseVote> call, Response<ResponseVote> response) {
                    if (response.isSuccessful()) {
                        if (response.body().isUp()) {
                            question.setMy_vote(1);
                            question.setUpvotes(question.getUpvotes() + 1);
                        } else {
                            question.setMy_vote(0);
                            question.setUpvotes(question.getUpvotes() - 1);
                        }
                        fragmentNotifyItemChanged(question);
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseVote> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getQuestions();
                        }
                    }).show();
        }
    }

    private void fragmentNotifyItemChanged(Question question) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                mDoubtFragment.notifyItemChanged(question);
                break;
            case 1:
                mDoubtOpenFragment.notifyItemChanged(question);
                break;
            case 2:
                mDoubtClosedFragment.notifyItemChanged(question);
                break;
            case 3:
                mDoubtRankingFragment.notifyItemChanged(question);
                break;
        }
    }

    private List<Question> getListQuestions() {
        return mList;
    }

    private List<Question> getListOpen() {
        List<Question> listAux = new ArrayList<>();

        for (Question question : mList) {
            if (!question.isAnswer()) {
                listAux.add(question);
            }
        }
        return listAux;
    }

    private List<Question> getListClose() {
        List<Question> listAux = new ArrayList<>();

        for (Question question : mList) {
            if (question.isAnswer()) {
                listAux.add(question);
            }
        }
        return listAux;
    }

    private List<Question> getListRanking() {
        List<Question> listAux = mList;

        Collections.sort(listAux, new Comparator() {
            public int compare(Object o1, Object o2) {
                Question q1 = (Question) o1;
                Question q2 = (Question) o2;
                return q1.getUpvotes() > q2.getUpvotes() ? -1 : (q1.getUpvotes() < q2.getUpvotes() ? +1 : 0);
            }
        });
        return listAux;
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
            intent.putExtra(Presentation.TAG, mPresentation);
            intent.putExtra(Instruction.TAG, mInstruction);
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
        outState.putParcelableArrayList(Question.TAG, (ArrayList<Question>) mList);
        outState.putParcelable(Presentation.TAG, mPresentation);
        outState.putParcelable(Instruction.TAG, mInstruction);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_DOUBT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Question question = data.getParcelableExtra(Question.TAG);
                mDoubtFragment.addItemPosition(0, question);
                mDoubtOpenFragment.addItemPosition(0, question);
                // TODO: 09/08/2016 fazer o ranking
                //mDoubtRankingFragment.addItem(question);
            }
        }
    }

    @Override
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putParcelableArrayListExtra(Question.TAG, (ArrayList<Question>) mList);
            intent.putExtra(Presentation.TAG, mPresentation);
            intent.putExtra(Instruction.TAG, mInstruction);
        }
        super.startActivity(intent);
    }
}
