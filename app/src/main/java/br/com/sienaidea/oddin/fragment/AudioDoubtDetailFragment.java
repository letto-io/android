package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AudioAdapter;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListenerHack;
import br.com.sienaidea.oddin.model.Contribution;
import br.com.sienaidea.oddin.retrofitModel.Answer;
import br.com.sienaidea.oddin.retrofitModel.Material;
import br.com.sienaidea.oddin.view.DoubtDetailsActivity;

public class AudioDoubtDetailFragment extends Fragment implements RecyclerViewOnClickListenerHack, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Answer> mList;
    private AudioAdapter mAudioAdapter;
    private Context mContext;
    private int mProfile;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public static AudioDoubtDetailFragment newInstance(List<Answer> list, int profile) {

        AudioDoubtDetailFragment fragment = new AudioDoubtDetailFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Answer.TAG, (ArrayList<Answer>) list);
        args.putInt("profile", profile);
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

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((DoubtDetailsActivity) getActivity()).fabHide();
                } else {
                    ((DoubtDetailsActivity) getActivity()).fabShow();
                }
            }
        });

        mList = getArguments().getParcelableArrayList(Answer.TAG);
        mProfile = getArguments().getInt("profile");

        if (mList != null) {
            mAudioAdapter = new AudioAdapter(mContext, mList, mProfile);

            mRecyclerView.setAdapter(mAudioAdapter);
            notifyDataSetChanged();
        }

        return view;
    }

    public void notifyDataSetChanged() {
        mAudioAdapter.notifyDataSetChanged();
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

    public void downloadFinished(Material material, String url) {
        for (Answer answer : mList) {
            int index = answer.getMaterials().indexOf(material);
            if (index != -1) {
                answer.getMaterials().get(index).setDownloaded(true);
                answer.getMaterials().get(index).setUrl(url);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClickListener(View view, int position) {

    }

    @Override
    public void onClickListener(View view, int position, String option) {

    }

    @Override
    public void onClickListener(View view, final int position, boolean isUnderstand) {
        final Material material = mAudioAdapter.getMaterial(position);
        if (!material.isDownloaded()) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(R.string.dialog_download_material);
            builder.setNegativeButton(R.string.dialog_cancel, null);
            builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DoubtDetailsActivity) getActivity()).getMaterial(material);
                }
            });
            builder.show();
        }
    }

    public void addItemPosition(int position, Material material) {
        Answer answer = new Answer();
        List<Material> materials = new ArrayList<>();
        materials.add(material);
        answer.setMaterials(materials);
        mList.add(position, answer);
        notifyDataSetChanged();
    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerHack RecyclerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context c, final RecyclerView recyclerView, RecyclerViewOnClickListenerHack rvoclh) {
            mContext = c;
            RecyclerViewOnClickListenerHack = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View itemView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    boolean flag = false;

                    if (itemView instanceof RelativeLayout) {

                        float x = ((RelativeLayout) itemView).getChildAt(1).getX();
                        float w = ((RelativeLayout) itemView).getChildAt(1).getWidth();
                        float h = ((RelativeLayout) itemView).getChildAt(1).getHeight();

                        Rect rect = new Rect();

                        ((RelativeLayout) itemView).getChildAt(1).getGlobalVisibleRect(rect);

                        float y = rect.top;
                        if (e.getX() >= x && e.getX() <= w + x && e.getRawY() >= y && e.getRawY() <= h + y) {
                            flag = true;
                        }
                    }

                    if (RecyclerViewOnClickListenerHack != null && itemView != null) {
                        RecyclerViewOnClickListenerHack.onClickListener(itemView, recyclerView.getChildAdapterPosition(itemView), flag);
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
