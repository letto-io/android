package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterContribution;
import br.com.sienaidea.oddin.retrofitModel.Answer;
import br.com.sienaidea.oddin.retrofitModel.Person;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.view.DoubtDetailsActivity;

public class FragmentDoubtDetailText extends Fragment {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Answer> mList;
    private AdapterContribution mAdapter;
    private Context mContext;
    private int mProfile;
    private int mPersonId;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public static FragmentDoubtDetailText newInstance(List<Answer> list, int profile, int personId) {

        FragmentDoubtDetailText fragment = new FragmentDoubtDetailText();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Answer.TAG, (ArrayList<Answer>) list);
        args.putInt("profile", profile);
        args.putInt(Person.TAG, personId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0) {
//                    ((DoubtDetailsActivity) getActivity()).fabHide();
//                } else {
//                    ((DoubtDetailsActivity) getActivity()).fabShow();
//                }
//            }
//        });

        mList = getArguments().getParcelableArrayList(Answer.TAG);
        mProfile = getArguments().getInt("profile");
        mPersonId = getArguments().getInt(Person.TAG);

        if (mList != null) {
            boolean isQuestionOwner;

            Preference preference = new Preference();
            if (mPersonId == preference.getUserId(mContext)) {
                isQuestionOwner = true;
            } else isQuestionOwner = false;

            mAdapter = new AdapterContribution(mContext, mList, mProfile, isQuestionOwner);

            mRecyclerView.setAdapter(mAdapter);
            notifyDataSetChanged();
        }

//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                ((DoubtDetailsActivity) getActivity()).getContentDoubt();
//            }
//        });

        return view;
    }

    public void swipeRefreshStop() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        if (mList.isEmpty())
            setEmpty(true);
        else setEmpty(false);
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

    public void addItem(Answer answer) {
        mList.add(0, answer);
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);
        notifyDataSetChanged();
    }
}
