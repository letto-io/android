package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.Contribution;

public class AdapterContribution extends RecyclerView.Adapter<AdapterContribution.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Contribution> mList;
    private Context mContext;
    private int mProfile;

    public AdapterContribution(Context context, List<Contribution> list, int profile) {
        this.mContext = context;
        this.mList = list;
        this.mProfile = profile;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_contribution, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contribution contribution = mList.get(position);
        holder.getTvPersonName().setText(contribution.getPersonName());
        holder.getTvText().setText(contribution.getText());
        holder.getTvCreatedat().setText(contribution.getCreatedat());

        if (mProfile == 2) {
            holder.ivUnderstand.setVisibility(View.GONE);
        } else {
            holder.ivUnderstand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage("Marcar que esta resposta sanou sua duvida?");
                    builder.setNegativeButton(R.string.dialog_cancel, null);
                    builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: chamar metodo understand aqui:
                            //((ActDoubtDetails) getActivity()).attemptGetMaterialContent(position, material);
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPersonName;
        private TextView tvText;
        private TextView tvCreatedat;
        private ImageView ivUnderstand;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            tvCreatedat = (TextView) itemView.findViewById(R.id.tv_createdat);
            ivUnderstand = (ImageView) itemView.findViewById(R.id.iv_understand);
        }

        public TextView getTvPersonName() {
            return tvPersonName;
        }

        public TextView getTvText() {
            return tvText;
        }

        public TextView getTvCreatedat() {
            return tvCreatedat;
        }
    }
}
