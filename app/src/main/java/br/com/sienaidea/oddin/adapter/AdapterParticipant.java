package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.retrofitModel.Person;

public class AdapterParticipant extends RecyclerView.Adapter<AdapterParticipant.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Person> mList;

    public AdapterParticipant(Context context, List<Person> list) {
        this.mList = list;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_participant, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Person person = mList.get(position);
        holder.getName().setText(person.getName());

        // TODO: 04/08/2016

//        if (participant.isOnline()) {
//            holder.ivParticipantOnline.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivParticipantOnline;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_person_name);
            ivParticipantOnline = (ImageView) itemView.findViewById(R.id.iv_participant_online);
        }

        public TextView getName() {
            return tvName;
        }

        public ImageView getIvParticipantOnline() {
            return ivParticipantOnline;
        }
    }
}
