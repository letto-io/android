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

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class AlternativeAdapter extends RecyclerView.Adapter<AlternativeAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Alternative> mList;
    private Context mContext;

    public AlternativeAdapter(Context context, List<Alternative> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_alternative, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.rbAlternative.setText(mList.get(position).getDescription());
        holder.tvChoices.setText(String.valueOf(mList.get(position).getChoice_count()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
