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
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Lecture;
import br.com.sienaidea.oddin.util.DateUtil;

public class AdapterLecture extends RecyclerView.Adapter<AdapterLecture.MyViewHolder> {
    private List<Instruction> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public AdapterLecture(Context context, List<Instruction> list) {
        mContext = context;
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
        myViewHolder.tvName.setText(mList.get(position).getLecture().getName());
        myViewHolder.tvClass.setText(mContext.getString(R.string.adapter_class, mList.get(position).getClass_number()));
        myViewHolder.tvStartDate.setText(mContext.getString(R.string.adapter_start_date, DateUtil.getDateStringDDMMYYYY(mList.get(position).getStart_date())));

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

    public Instruction getInstruction(int position) {
        return mList.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvClass, tvStartDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvStartDate = (TextView) itemView.findViewById(R.id.tv_start_date);
            tvClass = (TextView) itemView.findViewById(R.id.tv_class);
        }
    }
}