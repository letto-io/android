package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Material;

public class AdapterMaterial extends RecyclerView.Adapter<AdapterMaterial.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Material> mList;

    public AdapterMaterial(Context context, List<Material> mList) {
        this.mList = mList;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_material, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Material material = mList.get(position);

        String mime = material.getMime();
        if (mime != null) {
            if (mime.equalsIgnoreCase(Constants.MIME_TYPE_PDF)) {
                holder.getName().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_pdf_box, 0, 0, 0);
            } else if (mime.equalsIgnoreCase(Constants.MIME_TYPE_IMAGE)) {
                holder.getName().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image, 0, 0, 0);
            } else if (mime.equalsIgnoreCase(Constants.MIME_TYPE_VIDEO)) {
                holder.getName().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_movie, 0, 0, 0);
            } else if (mime.equalsIgnoreCase(Constants.MIME_TYPE_TEXT)) {
                holder.getName().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_document_box, 0, 0, 0);
            }
        }

        holder.getName().setText(material.getName());
    }

    public void addItemPosition(int position, Material material) {
        mList.add(position, material);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Material getMaterial(int position) {
        return mList.get(position);
    }

    public void downloadFinished(int position, Uri uri) {
        //mList.get(position).setUri(uri);
        notifyItemChanged(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView Name;

        public MyViewHolder(View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.tv_material_name);
        }

        public TextView getName() {
            return Name;
        }
    }
}
