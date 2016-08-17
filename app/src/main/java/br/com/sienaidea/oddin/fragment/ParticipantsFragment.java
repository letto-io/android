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

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterParticipant;
import br.com.sienaidea.oddin.retrofitModel.Person;
import br.com.sienaidea.oddin.view.ParticipantsActivity;

public class ParticipantsFragment extends Fragment {
    public static String TAG = ParticipantsFragment.class.getName();
    public static final String ALL = "TODOS";

    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    private List<Person> mList;
    private AdapterParticipant mAdapter;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ParticipantsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public static ParticipantsFragment newInstance(List<Person> list) {

        ParticipantsFragment fragment = new ParticipantsFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Person.TAG, (ArrayList<Person>) list);
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

        mList = getArguments().getParcelableArrayList(Person.TAG);

        if (mList != null) {
            mAdapter = new AdapterParticipant(mContext, mList);

            mRecyclerView.setAdapter(mAdapter);
            notifyDataSetChanged();
        }

//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                ((ParticipantsActivity) getActivity()).getParticipants();
//            }
//        });

        return view;
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();

        if (mList.isEmpty())
            setEmpty(true);

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
}
