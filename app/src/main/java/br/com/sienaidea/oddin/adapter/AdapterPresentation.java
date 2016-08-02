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
import br.com.sienaidea.oddin.retrofitModel.Presentation;

public class AdapterPresentation extends RecyclerView.Adapter<AdapterPresentation.MyViewHolder> {
    private List<Presentation> mList;
    private LayoutInflater mLayoutInflater;

    public AdapterPresentation(Context context, List<Presentation> listDiscipline) {
        mList = listDiscipline;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_presentation, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvTheme.setText(mList.get(position).getSubject());
       // myViewHolder.tvDate.setText(mList.get(position).get);

        try {
            YoYo.with(Techniques.ZoomIn)
                    .duration(400)
                    .playOn(myViewHolder.itemView);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Presentation getPresentation(int position) {
        return mList.get(position);
    }

    public void addItemPosition(int position, Presentation presentation) {
        mList.add(position, presentation);
        notifyItemInserted(position);
    }

    public void removeItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTheme;
        public TextView tvDate;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTheme = (TextView) itemView.findViewById(R.id.tv_theme);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
