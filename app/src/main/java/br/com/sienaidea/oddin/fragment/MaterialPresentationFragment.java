package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterMaterial;
import br.com.sienaidea.oddin.interfaces.RecyclerViewOnClickListener;
import br.com.sienaidea.oddin.model.Material;
import br.com.sienaidea.oddin.view.PresentationDetailsActivity;

public class MaterialPresentationFragment extends Fragment implements RecyclerViewOnClickListener, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Material> mList;
    private AdapterMaterial mAdapter;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String TAG = MaterialPresentationFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public static MaterialPresentationFragment newInstance(List<Material> list) {

        MaterialPresentationFragment fragment = new MaterialPresentationFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Material.TAG, (ArrayList<Material>) list);
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

        mList = getArguments().getParcelableArrayList(Material.TAG);
        if (mList != null) {
            mAdapter = new AdapterMaterial(mContext, mList);
            mRecyclerView.setAdapter(mAdapter);
            notifyDataSetChanged();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((PresentationDetailsActivity) getActivity()).loadMaterial();
            }
        });

        return view;
    }

    public void swipeRefreshStop() {
        mSwipeRefreshLayout.setRefreshing(false);
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

        final Material material = mAdapter.getMaterial(position);

        if (!material.isDownloaded()) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setMessage("Deseja fazer download de: " + material.getName() + " ?");
            builder.setNegativeButton(R.string.dialog_cancel, null);
            builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((PresentationDetailsActivity) getActivity()).attemptGetMaterialContent(position, material);
                }
            });
            builder.show();
        } else {
            Intent newIntent = new Intent();
            newIntent.setDataAndType(material.getUri(), material.getMime());
            newIntent.setAction(Intent.ACTION_VIEW);
            try {
                startActivity(newIntent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void downloadFinished(int position, Uri uri) {
        mAdapter.downloadFinished(position, uri);
    }

    @Override
    public void onClick(View v) {

    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListener mRecyclerViewOnClickListener;

        public RecyclerViewTouchListener(Context c, final RecyclerView recyclerView, RecyclerViewOnClickListener rvoclh) {
            mContext = c;
            mRecyclerViewOnClickListener = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
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
