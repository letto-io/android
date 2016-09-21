package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Notice;
import br.com.sienaidea.oddin.util.DateUtil;

/**
 * Created by Siena Idea on 21/09/2016.
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Notice> mList;

    public NoticeAdapter(Context context, List<Notice> mList) {
        this.mList = mList;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvSubject.setText(mList.get(position).getSubject());
        holder.tvCreatedAt.setText(DateUtil.getDateUFCFormat(mList.get(position).getCreated_at()));
        holder.tvText.setText(mList.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject, tvCreatedAt, tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSubject = (TextView) itemView.findViewById(R.id.tv_subject);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tv_createdat);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }
}
