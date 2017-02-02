package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Survey;
import br.com.sienaidea.oddin.util.DateUtil;

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Survey> mList;

    public SurveyAdapter(Context context, List<Survey> list) {
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

        try {
            YoYo.with(Techniques.ZoomIn)
                    .duration(400)
                    .playOn(holder.itemView);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvQuestion, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvQuestion = (TextView) itemView.findViewById(R.id.tv_question);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
