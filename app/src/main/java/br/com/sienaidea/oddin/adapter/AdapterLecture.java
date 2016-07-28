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
import br.com.sienaidea.oddin.retrofitModel.Lecture;

public class AdapterLecture extends RecyclerView.Adapter<AdapterLecture.MyViewHolder> {
    private List<Lecture> mList;
    private LayoutInflater mLayoutInflater;

    public AdapterLecture(Context context, List<Lecture> list) {
        mList = list;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_lecture, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvNome.setText(mList.get(position).getName());

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

    public Lecture getLecture(int position) {
        return mList.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNome;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvNome = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}