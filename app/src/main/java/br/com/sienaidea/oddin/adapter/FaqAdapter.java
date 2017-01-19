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

    private static ItemClickListener sClickListener;

    public FaqAdapter(Context context, List<Faq> list) {
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
        holder.tvNumber.setText(String.valueOf(mList.get(position).getId()));
        holder.tvQuestion.setText(mList.get(position).getQuestion());
        holder.tvAnswer.setText(mList.get(position).getAnswer());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * metodo chamado pela activity que implementa o click, para desacoplar o adapter
     *
     * @param itemClickListener
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        sClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNumber, tvQuestion, tvAnswer;
        private View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvQuestion = (TextView) itemView.findViewById(R.id.tv_question);
            tvAnswer = (TextView) itemView.findViewById(R.id.tv_answer);
            divider = itemView.findViewById(R.id.vw_divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //ativo o onclick da activity
            //verifica se é null, caso a activity não implementa o onclick, para não setar atoa
            if (sClickListener != null)
                sClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
