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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Answer;
import br.com.sienaidea.oddin.retrofitModel.Material;
import br.com.sienaidea.oddin.view.DoubtDetailsActivity;

public class AdapterMaterialDoubt extends RecyclerView.Adapter<AdapterMaterialDoubt.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Answer> mList;
    private int mProfile;
    private Context mContext;

    public AdapterMaterialDoubt(Context context, List<Answer> list, int profile) {
        mContext = context;
        this.mList = list;
        this.mProfile = profile;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_material_contribution, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final List<Material> materials = mList.get(position).getMaterials();

        for (final Material material : materials) {
            String mime = material.getMime();
            if (mime != null && mime.equalsIgnoreCase(Constants.MIME_TYPE_PDF)) {
                holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_pdf_box, 0, 0, 0);
            } else if (mime != null && mime.equalsIgnoreCase(Constants.MIME_TYPE_IMAGE_JPEG) || mime.equalsIgnoreCase(Constants.MIME_TYPE_IMAGE_PNG)) {
                holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image, 0, 0, 0);
            } else if (mime != null && mime.equalsIgnoreCase(Constants.MIME_TYPE_VIDEO)) {
                holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_movie, 0, 0, 0);
            } else if (mime != null && mime.equalsIgnoreCase(Constants.MIME_TYPE_TEXT)) {
                holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_document_box, 0, 0, 0);
            } else if (mime.equalsIgnoreCase(Constants.MIME_TYPE_DOCX)) {
                holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_word_box, 0, 0, 0);
            }

            holder.tvName.setText(material.getName());

            if (mProfile == Constants.INSTRUCTOR) {
                holder.ivAccept.setEnabled(false);
                holder.ivAccept.setClickable(false);
            } else {
                holder.ivAccept.setVisibility(View.VISIBLE);
                holder.ivAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        builder.setNegativeButton(R.string.dialog_cancel, null);
                        if (material.isAccepted()) {
//                        builder.setMessage(R.string.dialog_delete_accept_answer);
//                        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ((DoubtDetailsActivity) mContext).acceptAnswer(answer);
//                            }
//                        });
//                        builder.show();
                        } else {
                            builder.setMessage(R.string.dialog_accept_answer);
                            builder.setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((DoubtDetailsActivity) mContext).acceptAnswer(mList.get(position));
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Material getMaterial(int position) {
        return mList.get(position).getMaterials().get(0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivAccept;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_material_name);
            ivAccept = (ImageView) itemView.findViewById(R.id.iv_accepted);
        }
    }
}
