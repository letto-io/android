package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Alternative;
import br.com.sienaidea.oddin.retrofitModel.Survey;

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class AlternativeAdapter extends RecyclerView.Adapter<AlternativeAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Survey mSurvey;
    private Context mContext;

    public AlternativeAdapter(Context context, Survey survey) {
        this.mContext = context;
        this.mSurvey = survey;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_alternative, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mSurvey.getMy_vote() != 0) {
            holder.rbAlternative.setEnabled(false);
            if (mSurvey.getMy_vote() == mSurvey.getAlternatives().get(position).getId()) {
                holder.rbAlternative.setChecked(true);
                holder.rbAlternative.setEnabled(true);
            } else {
                holder.rbAlternative.setChecked(false);
                holder.rbAlternative.setEnabled(false);
            }
        } else holder.rbAlternative.setEnabled(true);
        holder.rbAlternative.setText(mSurvey.getAlternatives().get(position).getDescription());
        holder.tvChoices.setText(mContext.getString(R.string.adapter_choices, String.valueOf(mSurvey.getAlternatives().get(position).getChoice_count())));
    }

    @Override
    public int getItemCount() {
        return mSurvey.getAlternatives().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChoices;
        private RadioButton rbAlternative;

        public ViewHolder(View itemView) {
            super(itemView);
            tvChoices = (TextView) itemView.findViewById(R.id.tv_choices);
            rbAlternative = (RadioButton) itemView.findViewById(R.id.rb_alternative);
        }
    }
}
