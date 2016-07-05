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
import br.com.sienaidea.oddin.model.Discipline;

public class AdapterDiscipline extends RecyclerView.Adapter<AdapterDiscipline.MyViewHolder> {
    private List<Discipline> mList;
    private LayoutInflater mLayoutInflater;

    public AdapterDiscipline(Context c, List<Discipline> listDiscipline) {
        mList = listDiscipline;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_discipline, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvNome.setText(mList.get(position).getNome());
        myViewHolder.tvTurma.setText("Turma: " + mList.get(position).getTurma());
        myViewHolder.tvDataInicio.setText("Início: " + mList.get(position).getDataInicio());

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

    public Discipline getDiscipline(int position) {
        return mList.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNome;
        public TextView tvTurma;
        public TextView tvDataInicio;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvNome = (TextView) itemView.findViewById(R.id.tv_nome);
            tvTurma = (TextView) itemView.findViewById(R.id.tv_turma);
            tvDataInicio = (TextView) itemView.findViewById(R.id.tv_dataInicio);
        }
    }
}