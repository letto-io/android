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
import br.com.sienaidea.oddin.retrofitModel.Date;
import br.com.sienaidea.oddin.util.DateUtil;

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Date> mList;

    public DateAdapter(Context context, List<Date> mList) {
        this.mList = mList;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDate.setText(DateUtil.getDateUFCFormat(mList.get(position).getDate()));
        holder.tvSubject.setText(mList.get(position).getSubject());
        holder.tvText.setText(mList.get(position).getText());

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
        private TextView tvDate, tvSubject, tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvSubject = (TextView) itemView.findViewById(R.id.tv_subject);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }
}
