package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterPresentation;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListenerOnLongPressListener;
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.view.DoubtActivity;
import br.com.sienaidea.oddin.view.PresentationActivity;

public class PresentationFragment extends Fragment implements RecyclerViewOnClickListenerOnLongPressListener, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Presentation> mListPresentation;
    private AdapterPresentation mAdapterPresentation;
    private Context mContext;
    private Instruction mInstruction;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String TAG = PresentationFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public static PresentationFragment newInstance(List<Presentation> list, Instruction instruction) {

        PresentationFragment fragment = new PresentationFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Presentation.TAG, (ArrayList<Presentation>) list);
        args.putParcelable(Instruction.TAG, instruction);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);

        mEmptyView = (TextView) view.findViewById(R.id.empty_view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mContext, mRecyclerView, this));

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mListPresentation = getArguments().getParcelableArrayList(Presentation.TAG);
        mInstruction = getArguments().getParcelable(Instruction.TAG);

        // TODO: 09/08/2016 verificar o profile pra depois fazer o scroll
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((PresentationActivity) getActivity()).fabHide();
                } else {
                    ((PresentationActivity) getActivity()).fabShow();
                }
            }
        });

        if (mListPresentation != null) {
            mAdapterPresentation = new AdapterPresentation(mContext, mListPresentation);
            mRecyclerView.setAdapter(mAdapterPresentation);
            notifyDataSetChanged();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((PresentationActivity) getActivity()).getPresentations();
            }
        });

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

    private void checkState() {
        if (mListPresentation.isEmpty())
            setEmpty(true);
        else setEmpty(false);
    }

    public void swipeRefreshStop() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void notifyDataSetChanged() {
        mAdapterPresentation.notifyDataSetChanged();
        checkState();
    }

    public void addItemPosition(int position, Presentation presentation) {
        mAdapterPresentation.addItemPosition(position, presentation);
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, position);
        checkState();
    }

    public void removeItem(int position) {
        mAdapterPresentation.removeItem(position);
        checkState();
    }

    @Override
    public void onClickListener(int position) {
        Presentation presentation = mAdapterPresentation.getPresentation(position);

        Intent intent = new Intent(mContext, DoubtActivity.class);
        intent.putExtra(Instruction.TAG, mInstruction);
        intent.putExtra(Presentation.TAG, presentation);
        mContext.startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(final int position) {

        final Presentation presentation = mAdapterPresentation.getPresentation(position);

        //TODO verificar o profile também
        if (mListPresentation.get(position).getStatus() == Presentation.OPEN) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((PresentationActivity) getActivity()).closePresentation(position, presentation);
                }
            });
            builder.setNegativeButton(R.string.dialog_cancel, null);
            builder.setTitle(presentation.getSubject());
            builder.setMessage(R.string.dialog_close_presentation);
            builder.show();
        }
    }

    @Override
    public void onClick(View v) {
    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerOnLongPressListener mRecyclerViewOnClickListenerOnLongPressListener;

        public RecyclerViewTouchListener(Context c, final RecyclerView recyclerView, RecyclerViewOnClickListenerOnLongPressListener rvoclh) {
            mContext = c;
            mRecyclerViewOnClickListenerOnLongPressListener = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (mRecyclerViewOnClickListenerOnLongPressListener != null && itemView != null) {
                        mRecyclerViewOnClickListenerOnLongPressListener.onLongPressClickListener(
                                recyclerView.getChildAdapterPosition(itemView));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (mRecyclerViewOnClickListenerOnLongPressListener != null && itemView != null) {
                        mRecyclerViewOnClickListenerOnLongPressListener.onClickListener(
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
