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
import br.com.sienaidea.oddin.model.Doubt;

public class AdapterDoubt extends RecyclerView.Adapter<AdapterDoubt.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Doubt> mList;
    private int mProfile;
    private Context mContext;

    public AdapterDoubt(Context c, List<Doubt> doubtList, int profile) {
        mContext = c;
        mProfile = profile;
        mList = doubtList;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_doubt_card, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        Doubt mDoubt = mList.get(position);

        myViewHolder.tvPersonName.setText(mDoubt.getPerson().getName());
        myViewHolder.tvText.setText(mDoubt.getText());
        myViewHolder.tvTime.setText(mDoubt.getTime());

        myViewHolder.tvLike.setText(mDoubt.getLikes() + "");

        if (mProfile == 2) {
            myViewHolder.ivLock.setVisibility(View.VISIBLE);
            //myViewHolder.tvLike.setClickable(false);
            myViewHolder.tvLike.setEnabled(false);
            //myViewHolder.tvUnderstand.setVisibility(View.GONE);
        } else {
            myViewHolder.ivLock.setVisibility(View.GONE);
            if (mDoubt.getStatus() == 2) {
                myViewHolder.ivClosed.setVisibility(View.VISIBLE);
            }
        }

        if (mDoubt.getContributions() > 0) {
            myViewHolder.ivComment.setVisibility(View.VISIBLE);
        } else myViewHolder.ivLock.setEnabled(false);

        if (mDoubt.getStatus() == 2) {
            myViewHolder.ivLock.setImageResource(R.drawable.ic_lock_outline);
        }

        if (mDoubt.isLike()) {
            Drawable mDrawable = colorize(R.drawable.ic_chevron_up_white, R.color.colorAccent);
            myViewHolder.tvLike.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
        }

        /*
        if (mDoubt.isUnderstand()) {
            myViewHolder.tvUnderstand.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox_marked_outline, 0, 0, 0);
        } else {
            myViewHolder.tvUnderstand.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox_blank_outline, 0, 0, 0);
        }
        */
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Doubt getDoubtAdapter(int position) {
        return mList.get(position);
    }

    public void updateList(List<Doubt> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addItemPosition(int position, Doubt doubt) {
        mList.add(position, doubt);
        notifyItemInserted(position);
    }

    public void addItem(Doubt doubt) {
        mList.add(doubt);
    }

    public void like(int position) {
        mList.get(position).setLike(true);
        mList.get(position).like();
        notifyItemChanged(position);
    }

    public void unLike(int position) {
        mList.get(position).setLike(false);
        mList.get(position).removeLike();
        notifyItemChanged(position);
    }

    public void understand(int position) {
        mList.get(position).setUnderstand(true);
        notifyItemChanged(position);
    }

    public void removeUnderstand(int position) {
        mList.get(position).setUnderstand(false);
        notifyItemChanged(position);
    }

    public void changeStatus(int position, int status) {
        mList.get(position).setStatus(status);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void notifyItemChanged(Doubt doubt){
        notifyItemChanged(mList.indexOf(doubt));
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
        public TextView tvText, tvLikes, tvPersonName, tvTime, tvUnderstand, tvLike;
        public ImageView ivLock, ivComment, ivClosed;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            //tvLikes = (TextView) itemView.findViewById(R.id.tv_likes);
            tvLike = (TextView) itemView.findViewById(R.id.iv_like);
            //tvUnderstand = (TextView) itemView.findViewById(R.id.iv_understand);

            ivLock = (ImageView) itemView.findViewById(R.id.iv_lock);
            ivComment = (ImageView) itemView.findViewById(R.id.iv_comment);
            ivClosed = (ImageView) itemView.findViewById(R.id.iv_closed);
        }
    }
}
