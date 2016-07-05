package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.view.ActDiscipline;
import br.com.sienaidea.oddin.view.ActPresentation;
import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterDiscipline;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListenerHack;
import br.com.sienaidea.oddin.model.Discipline;

public class DisciplineFragment extends Fragment implements RecyclerViewOnClickListenerHack, View.OnClickListener {
    private AdapterDiscipline mAdapterDiscipline;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Discipline> mList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;

    public static final String TAG = DisciplineFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mContext, mRecyclerView, this));

        mList = ((ActDiscipline) getActivity()).getListDiscipline();

        AdapterDiscipline adapter = new AdapterDiscipline(mContext, mList);
        mRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((ActDiscipline) getActivity()).loadListDiscipline();
            }
        });

        mAdapterDiscipline = (AdapterDiscipline) mRecyclerView.getAdapter();

        return view;
    }

    private void setEmpty(boolean isEmpty) {
        if (isEmpty) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void swipeRefreshStop() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void notifyDataSetChanged() {
        mAdapterDiscipline.notifyDataSetChanged();

        if (mList.isEmpty())
            setEmpty(true);
    }

    @Override
    public void onClickListener(View view, int position) {
        Discipline discipline = mAdapterDiscipline.getDiscipline(position);
        Intent intent = new Intent(mContext, ActPresentation.class);
        intent.putExtra(Discipline.NAME, discipline);
        mContext.startActivity(intent);
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
                    super.onLongPress(e);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (itemView != null && mRecyclerViewOnClickListenerHack != null) {
                        mRecyclerViewOnClickListenerHack.onClickListener(itemView,
                                recyclerView.getChildAdapterPosition(itemView));
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
