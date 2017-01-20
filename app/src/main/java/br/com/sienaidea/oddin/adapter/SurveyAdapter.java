package br.com.sienaidea.oddin.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.AlternativeFragment;
import br.com.sienaidea.oddin.retrofitModel.Alternative;
import br.com.sienaidea.oddin.retrofitModel.Survey;
import br.com.sienaidea.oddin.util.DateUtil;
import br.com.sienaidea.oddin.view.SurveyActivity;

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Survey> mList;
    private AlternativeFragment mAlternativeFragment;
    private Context mContext;

    public SurveyAdapter(Context context, List<Survey> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_survey, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvTitle.setText(mList.get(position).getTitle());
        holder.tvQuestion.setText(mList.get(position).getQuestion());
        holder.tvDate.setText(DateUtil.getDateUFCFormat(mList.get(position).getCreated_at()));


        // TODO: 1/20/2017 create fragment alternatives
        FragmentManager fragmentManager = ((SurveyActivity) mContext).getSupportFragmentManager();

        mAlternativeFragment = (AlternativeFragment) fragmentManager.findFragmentByTag(AlternativeFragment.TAG);
        if (mAlternativeFragment != null) {
            mAlternativeFragment.notifyDataSetChanged();
        } else {
            mAlternativeFragment = AlternativeFragment.newInstance(mList.get(position).getAlternatives());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_alternatives, mAlternativeFragment, AlternativeFragment.TAG);
            fragmentTransaction.commit();
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvQuestion, tvDate;
        private View vDivider, vAlternatives;


        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvQuestion = (TextView) itemView.findViewById(R.id.tv_question);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            vDivider = itemView.findViewById(R.id.vw_divider);
            vAlternatives = itemView.findViewById(R.id.rl_fragment_alternatives);
        }
    }
}
