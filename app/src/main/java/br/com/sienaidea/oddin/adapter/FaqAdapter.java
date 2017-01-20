package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Faq;
import br.com.sienaidea.oddin.retrofitModel.Notice;
import br.com.sienaidea.oddin.util.DateUtil;

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Faq> mList;
    private Context mContext;

    public FaqAdapter(Context context, List<Faq> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_faq, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvNumber.setText(mContext.getString(R.string.adapter_faq_number, mList.get(position).getId()));
        holder.tvQuestion.setText(mList.get(position).getQuestion());

        if (mList.get(position).isDetailVisible()) {
            holder.tvAnswer.setText(mList.get(position).getAnswer());
            holder.divider.setVisibility(View.VISIBLE);
            holder.tvAnswer.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
            holder.tvAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvQuestion, tvAnswer;
        private View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvQuestion = (TextView) itemView.findViewById(R.id.tv_question);
            tvAnswer = (TextView) itemView.findViewById(R.id.tv_answer);
            divider = itemView.findViewById(R.id.vw_divider);
        }
    }
}
