package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterLecture;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListenerOnLongPressListener;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.view.LectureActivity;
import br.com.sienaidea.oddin.view.PresentationActivity;

public class LectureFragment extends Fragment implements RecyclerViewOnClickListenerOnLongPressListener, View.OnClickListener {
    private AdapterLecture mAdapterLecture;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Instruction> mList = new ArrayList<>();
    private Context mContext;

    public static final String TAG = LectureFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mContext, mRecyclerView, this));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mList = ((LectureActivity) getActivity()).getList();
        AdapterLecture adapter = new AdapterLecture(mContext, mList);
        mRecyclerView.setAdapter(adapter);

        mAdapterLecture = (AdapterLecture) mRecyclerView.getAdapter();
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

    public void notifyDataSetChanged() {
        mAdapterLecture.notifyDataSetChanged();

        if (mList.isEmpty())
            setEmpty(true);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onClickListener(int position) {
        Instruction instruction = mAdapterLecture.getInstruction(position);
        Intent intent = new Intent(mContext, PresentationActivity.class);
        intent.putExtra(Instruction.TAG, instruction);
        mContext.startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(int position) {
    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerOnLongPressListener mRecyclerViewOnClickListenerOnLongPressListener;

        public RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, RecyclerViewOnClickListenerOnLongPressListener listener) {
            mContext = context;
            mRecyclerViewOnClickListenerOnLongPressListener = listener;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (itemView != null && mRecyclerViewOnClickListenerOnLongPressListener != null) {
                        mRecyclerViewOnClickListenerOnLongPressListener.onLongPressClickListener(recyclerView.getChildAdapterPosition(itemView));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (itemView != null && mRecyclerViewOnClickListenerOnLongPressListener != null) {
                        mRecyclerViewOnClickListenerOnLongPressListener.onClickListener(recyclerView.getChildAdapterPosition(itemView));
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
