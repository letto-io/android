package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import br.com.sienaidea.oddin.adapter.FaqAdapter;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListenerOnLongPressListener;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Faq;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.view.FAQActivity;

public class FaqFragment extends Fragment implements RecyclerViewOnClickListenerOnLongPressListener, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Faq> mList;
    private FaqAdapter mAdapter;
    private Context mContext;

    public static final String TAG = FaqFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public static FaqFragment newInstance(List<Faq> list) {

        FaqFragment fragment = new FaqFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Faq.TAG, (ArrayList<Faq>) list);
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
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mContext, mRecyclerView, this));

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mList = getArguments().getParcelableArrayList(Faq.TAG);
        if (mList != null) {
            mAdapter = new FaqAdapter(mContext, mList);
            mRecyclerView.setAdapter(mAdapter);
            notifyDataSetChanged();
        }

        return view;
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void onClickListener(final int position) {
        if (mList.get(position).isDetailVisible())
            mList.get(position).setDetailVisible(false);
        else mList.get(position).setDetailVisible(true);

        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onLongPressClickListener(final int position) {
        final Faq faq = mList.get(position);

        Preference preference = new Preference();
        if (preference.getUserProfile(mContext) == Constants.INSTRUCTOR) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((FAQActivity) getActivity()).deleteFaq(position, faq);
                }
            });
            builder.setNegativeButton(R.string.dialog_cancel, null);
            builder.setTitle(faq.getQuestion());
            builder.setMessage(R.string.dialog_delete_faq);
            builder.show();
        }
    }

    @Override
    public void onClick(View v) {
    }

    public void addItemPosition(int position, Faq faq) {
        mList.add(position, faq);
        notifyDataSetChanged();
    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerOnLongPressListener mRecyclerViewOnClickListener;

        public RecyclerViewTouchListener(Context c, final RecyclerView recyclerView, RecyclerViewOnClickListenerOnLongPressListener rvoclh) {
            mContext = c;
            mRecyclerViewOnClickListener = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (mRecyclerViewOnClickListener != null && itemView != null) {
                        mRecyclerViewOnClickListener.onLongPressClickListener(
                                recyclerView.getChildAdapterPosition(itemView));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (mRecyclerViewOnClickListener != null && itemView != null) {
                        mRecyclerViewOnClickListener.onClickListener(
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
