package br.com.sienaidea.oddin.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterDoubt;
import br.com.sienaidea.oddin.adapter.AdapterPresentation;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListenerHack;
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.provider.SearchableProvider;

public class SearchableActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack, View.OnClickListener {
    private List<Doubt> mListDoubt = new ArrayList<>();
    private List<Doubt> mListAuxDoubt = new ArrayList<>();
    private AdapterDoubt mAdapterDoubt;

    private boolean isDoubt = false;

    private List<Presentation> mListPresentation = new ArrayList<>();
    private List<Presentation> mListAuxPresentation = new ArrayList<>();
    private AdapterPresentation mAdapterPresentation;

    private Discipline mDiscipline;
    private Presentation mPresentation;

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    public static String TAG = SearchableActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, mRecyclerView, this));

        mToolbar = (Toolbar) findViewById(R.id.tb_searchable);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        hendleSearch(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        hendleSearch(intent);
    }

    public void hendleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mToolbar.setTitle(query);

            //set query suggestions
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(query, null);

            mPresentation = intent.getParcelableExtra(Presentation.TAG);
            mDiscipline = intent.getParcelableExtra(Discipline.NAME);

            mListDoubt = intent.getParcelableArrayListExtra(Doubt.NAME);
            if (mListDoubt != null) {
                mToolbar.setSubtitle(mPresentation.getSubject());
                if (!mListDoubt.isEmpty()) {
                    filterDoubts(query);
                } else {
                    //else setEmpty(true);
                }
            } else {
                mListPresentation = intent.getParcelableArrayListExtra(Presentation.ARRAYLIST);
                if (mListPresentation != null) {
                    mToolbar.setSubtitle(mDiscipline.getNome());
                    if (!mListPresentation.isEmpty()) {
                        filterPresentations(query);
                    }
                }
            }
        }
    }
/*
    private void setEmpty(boolean isEmpty) {
        if (isEmpty) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }
    */

    public void filterDoubts(String query) {
//        isDoubt = true;
//        mListAuxDoubt.clear();
//
//        for (Doubt doubt : mListDoubt) {
//            if (doubt.getText().toLowerCase().contains(query.toLowerCase())) {
//                mListAuxDoubt.add(doubt);
//            }
//        }
//
//        if (mListAuxDoubt.isEmpty()){
//            Toast.makeText(this, "Nenhum resultado...", Toast.LENGTH_SHORT).show();
//        }
//
//        mAdapterDoubt = new AdapterDoubt(this, mListAuxDoubt, mDiscipline.getProfile());
//        mRecyclerView.setAdapter(mAdapterDoubt);
    }

    public void filterPresentations(String query) {
        mListAuxPresentation.clear();

        for (Presentation presentation : mListPresentation) {
            if (presentation.getSubject().toLowerCase().contains(query.toLowerCase())) {
                mListAuxPresentation.add(presentation);
            }
        }

        if (mListAuxPresentation.isEmpty()){
            Toast.makeText(this, "Nenhum resultado...", Toast.LENGTH_SHORT).show();
        }

        mAdapterPresentation = new AdapterPresentation(this, mListAuxPresentation);
        mRecyclerView.setAdapter(mAdapterPresentation);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.action_searchable_activity);

            searchView = (SearchView) MenuItemCompat.getActionView( item );

        searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );
        searchView.setQueryHint( getResources().getString(R.string.search_hint) );

        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    public void onClickListener(View view, int position) {
        if (isDoubt) {
            Intent intent = new Intent(SearchableActivity.this, DoubtDetailsActivity.class);
            intent.putExtra(Doubt.NAME, mAdapterDoubt.getDoubtAdapter(position));
            intent.putExtra(Discipline.NAME, mDiscipline);
            intent.putExtra(Presentation.TAG, mPresentation);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SearchableActivity.this, DoubtActivity.class);
            intent.putExtra(Discipline.NAME, mDiscipline);
            Presentation presentation = mAdapterPresentation.getPresentation(position);
            intent.putExtra(Presentation.TAG, presentation);
            startActivity(intent);
        }
    }

    @Override
    public void onClickListener(View view, int position, String option) {
    }

    @Override
    public void onClickListener(View view, int position, boolean option) {
    }

    @Override
    public void onClick(View v) {
    }


    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context c, final RecyclerView recyclerView, RecyclerViewOnClickListenerHack rvoclh) {
            mContext = c;
            mRecyclerViewOnClickListenerHack = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (itemView != null && mRecyclerViewOnClickListenerHack != null) {
                        mRecyclerViewOnClickListenerHack.onClickListener(itemView, recyclerView.getChildAdapterPosition(itemView));
                    }
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {
        }
    }
}
