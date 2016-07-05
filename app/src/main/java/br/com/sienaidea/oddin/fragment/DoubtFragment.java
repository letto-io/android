package br.com.sienaidea.oddin.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.model.Presentation;
import br.com.sienaidea.oddin.view.ActDoubtDetails;
import br.com.sienaidea.oddin.view.ActDoubts;

public class DoubtFragment extends Fragment implements RecyclerViewOnClickListenerHack, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private List<Doubt> mList;
    private Discipline mDiscipline;
    private Presentation mPresentation;
    private AdapterDoubt mAdapterDoubt;
    private Context mContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String TAG = DoubtFragment.class.getName();
    public static final String ALL = "TODAS";

    private static String LIKE = "LIKE";
    private static String UNDERSTAND = "UNDERSTAND";
    private static String LOCK = "LOCK";

    public static DoubtFragment newInstance(List<Doubt> list, Discipline discipline, Presentation presentation) {

        DoubtFragment fragment = new DoubtFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Doubt.NAME, (ArrayList<Doubt>) list);
        args.putParcelable(Discipline.NAME, discipline);
        args.putParcelable(Presentation.NAME, presentation);
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

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((ActDoubts) getActivity()).getDoubts();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mList = getArguments().getParcelableArrayList(Doubt.NAME);
        mDiscipline = getArguments().getParcelable(Discipline.NAME);
        mPresentation = getArguments().getParcelable(Presentation.NAME);

        if (mList != null) {
            mAdapterDoubt = new AdapterDoubt(mContext, mList, mDiscipline.getProfile());
            mAdapterDoubt.updateList(mList);
            mRecyclerView.setAdapter(mAdapterDoubt);
            if (mList.isEmpty())
                setEmpty(true);
        }
    }

    public void swipeRefreshStop() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void notifyDataSetChanged() {
        //mAdapterDoubt.notifyDataSetChanged();
        mRecyclerView.getAdapter().notifyDataSetChanged();

        checkState();
    }

    public void notifyItemChanged(Doubt doubt) {
        mAdapterDoubt.notifyItemChanged(doubt);
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

    public void addItemPosition(int position, Doubt doubt) {
        mAdapterDoubt.addItemPosition(position, doubt);
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, position);
        checkState();
    }

    public void addItem(Doubt doubt) {
        mAdapterDoubt.addItem(doubt);
        checkState();
    }

    public void removeItem(Doubt doubt) {
        mList.remove(doubt);
        notifyDataSetChanged();
    }

    public void notifyLike(int position, Boolean like) {
        if (like) {
            mAdapterDoubt.like(position);
        } else {
            mAdapterDoubt.unLike(position);
        }
    }

    public void notifyUnderstand(int position, Boolean understand) {
       /* if (understand) {
            mAdapterDoubt.understand(position);
        } else {
            mAdapterDoubt.removeUnderstand(position);
        }*/
    }

    public void notifyLock(int position, int status) {
        mAdapterDoubt.changeStatus(position, status);
    }

    public void removeItem(int position) {
        mAdapterDoubt.removeItem(position);
        checkState();
    }

    @Override
    public void onClickListener(View view, int position) {
    }

    @Override
    public void onClickListener(View view, final int position, String option) {
        final Doubt doubt = mAdapterDoubt.getDoubtAdapter(position);
        if (option != null) {
            if (option.equals(LIKE) && mDiscipline.getProfile() == 0) {
                if (mList.get(position).isLike()) {
                    ((ActDoubts) getActivity()).removeLike(position, doubt);
                } else {
                    ((ActDoubts) getActivity()).like(position, doubt);
                }
            } else if (option.equals(UNDERSTAND) && mDiscipline.getProfile() == 0) {
                if (mList.get(position).isUnderstand()) {
                    ((ActDoubts) getActivity()).removeUnderstand(position, doubt);
                } else {
                    ((ActDoubts) getActivity()).understand(position, doubt);
                }
            } else if (option.equals(LOCK) && mDiscipline.getProfile() == 2) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);

                if (mList.get(position).getStatus() == 0) {
                    if (mList.get(position).getContributions() > 0) {
                        builder.setNegativeButton(R.string.dialog_cancel, null);
                        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ActDoubts) getActivity()).changeStatus(position, doubt, 2);
                            }
                        });
                        builder.setMessage(R.string.dialog_close_doubt);
                        builder.show();
                    } else {
                        Toast.makeText(mContext, "Não permitido fechar dúvida sem contribuição!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    builder.setNegativeButton(R.string.dialog_cancel, null);
                    builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((ActDoubts) getActivity()).changeStatus(position, doubt, 0);
                        }
                    });
                    builder.setMessage(R.string.dialog_open_doubt);
                    builder.show();
                }
            }
        } else {
            Intent intent = new Intent(mContext, ActDoubtDetails.class);
            intent.putExtra(Doubt.NAME, mAdapterDoubt.getDoubtAdapter(position));
            intent.putExtra(Discipline.NAME, mDiscipline);
            intent.putExtra(Presentation.NAME, mPresentation);
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
                    String option = null;
                    float x, w, y, h;
                    Rect rect;

                    boolean isLike = false;
                    boolean isUnderstand = false;
                    boolean isLock = false;
                    if (itemView instanceof CardView) {

                        x = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(6).getX();
                        w = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(6).getWidth();
                        h = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(6).getHeight();

                        rect = new Rect();
                        ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(6).getGlobalVisibleRect(rect);
                        y = rect.top;

                        if (e.getX() >= x && e.getX() <= w + x && e.getRawY() >= y && e.getRawY() <= h + y) {
                            isLike = true;
                        }

                        if (isLike) {
                            option = LIKE;
                        } else {
                            x = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getX();
                            w = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getWidth();
                            h = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getHeight();

                            rect = new Rect();
                            ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getGlobalVisibleRect(rect);
                            y = rect.top;

                            if (e.getX() >= x && e.getX() <= w + x && e.getRawY() >= y && e.getRawY() <= h + y) {
                                isUnderstand = true;
                            }
                        }

                        if (isUnderstand) {
                            option = UNDERSTAND;
                        } else {
                            x = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getX();
                            w = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getWidth();
                            h = ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getHeight();

                            rect = new Rect();
                            ((RelativeLayout) ((CardView) itemView).getChildAt(0)).getChildAt(7).getGlobalVisibleRect(rect);
                            y = rect.top;

                            if (e.getX() >= x && e.getX() <= w + x && e.getRawY() >= y && e.getRawY() <= h + y) {
                                isLock = true;
                            }
                        }

                        if (isLock) {
                            option = LOCK;
                        }
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
