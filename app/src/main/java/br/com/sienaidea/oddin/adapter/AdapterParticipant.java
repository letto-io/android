package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
    private Context mContext;

    public AdapterParticipant(Context context, List<Person> list) {
        this.mContext = context;
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
        holder.tvName.setText(person.getName());

        if (person.isOnline()) {
            Drawable mDrawable = colorize(R.drawable.ic_account_circle_white, R.color.colorAccent);
            holder.tvName.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
        }else {
            holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_account_circle, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private Drawable colorize(int resource, int color) {
        Drawable mDrawable = ContextCompat.getDrawable(mContext, resource);

        int mColor = ContextCompat.getColor(mContext, color);        //copy it in a new one
        Drawable willBeWhite = mDrawable.getConstantState().newDrawable();
        willBeWhite.clearColorFilter();
        //set the color filter, you can use also Mode.SRC_ATOP
        willBeWhite.mutate().setColorFilter(mColor, PorterDuff.Mode.MULTIPLY);
        return willBeWhite;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_person_name);
        }
    }
}
