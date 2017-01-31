package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterDoubt;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListenerHack;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Question;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.view.DoubtDetailsActivity;
import br.com.sienaidea.oddin.view.DoubtActivity;

public class DoubtFragment extends Fragment implements RecyclerViewOnClickListenerHack, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Question> mList;
    private Instruction mInstruction;
    private Presentation mPresentation;
    private AdapterDoubt mAdapterDoubt;
    private Context mContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String TAG = DoubtFragment.class.getName();
    public static final String ALL = "TODAS";

    private static String LIKE = "LIKE";
    private static String UNDERSTAND = "UNDERSTAND";
    private static String LOCK = "LOCK";

    public static DoubtFragment newInstance(List<Question> list, Presentation presentation) {

        DoubtFragment fragment = new DoubtFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Question.TAG, (ArrayList<Question>) list);
        args.putParcelable(Presentation.TAG, presentation);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
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
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mContext, mRecyclerView, this));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mList = getArguments().getParcelableArrayList(Question.TAG);
        mPresentation = getArguments().getParcelable(Presentation.TAG);

        if (mList != null) {
            mAdapterDoubt = new AdapterDoubt(mContext, mList);
            mAdapterDoubt.updateList(mList);
            mRecyclerView.setAdapter(mAdapterDoubt);
            if (mList.isEmpty())
                setEmpty(true);
        }
    }

    public void notifyItemChanged(Question question) {
        mAdapterDoubt.notifyItemChanged(question);
    }

    public void notifyDataSetChanged() {
        //mAdapterDoubt.notifyDataSetChanged();
        mRecyclerView.getAdapter().notifyDataSetChanged();

        checkState();
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

    private void checkState() {
        if (mList.isEmpty())
            setEmpty(true);
        else setEmpty(false);
    }

    public void addItemPosition(int position, Question question) {
        mAdapterDoubt.addItemPosition(position, question);
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, position);
        checkState();
    }

    @Override
    public void onClickListener(View view, int position) {
    }

    @Override
    public void onClickListener(View view, final int position, String option) {
        mInstruction = ((DoubtActivity) getActivity()).getInstruction();

        final Question question = mAdapterDoubt.getQuestionAdapter(position);

        if (option != null) {
            Preference preference = new Preference();
            if (!(preference.getUserProfile(mContext) == Constants.INSTRUCTOR)) {
                if (option.equals(LIKE)) {
                    if (question.getMy_vote() == 1) {
                        Toast.makeText(mContext, R.string.toast_voted, Toast.LENGTH_SHORT).show();
                    } else
                        ((DoubtActivity) getActivity()).voteQuestion(question);
                }
            }
        } else {
            Intent intent = new Intent(mContext, DoubtDetailsActivity.class);
            intent.putExtra(Question.TAG, mAdapterDoubt.getQuestionAdapter(position));
            intent.putExtra(Instruction.TAG, mInstruction);
            intent.putExtra(Presentation.TAG, mPresentation);
            startActivity(intent);
        }
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

        public RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, RecyclerViewOnClickListenerHack rvoclh) {
            mContext = context;
            mRecyclerViewOnClickListenerHack = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    String option = null;
                    float x, w, y, h;
                    Rect rect;

                    boolean isLike = false;
                    if (itemView instanceof CardView) {

                        x = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(4).getX();
                        w = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(4).getWidth();
                        h = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(4).getHeight();

                        rect = new Rect();
                        ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(4).getGlobalVisibleRect(rect);
                        y = rect.top;

                        if (e.getX() >= x && e.getX() <= w + x && e.getRawY() >= y && e.getRawY() <= h + y) {
                            isLike = true;
                        }

                        if (isLike)
                            option = LIKE;
                    }

                    if (itemView != null && mRecyclerViewOnClickListenerHack != null) {
                        mRecyclerViewOnClickListenerHack.onClickListener(itemView,
                                recyclerView.getChildAdapterPosition(itemView), option);
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
