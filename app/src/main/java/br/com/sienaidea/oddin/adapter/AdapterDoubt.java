package br.com.sienaidea.oddin.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Question;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.DateUtil;

public class AdapterDoubt extends RecyclerView.Adapter<AdapterDoubt.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Question> mList;
    private Context mContext;

    public AdapterDoubt(Context context, List<Question> doubtList) {
        mContext = context;
        mList = doubtList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_doubt_card, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Question mQuestion = mList.get(position);

        if (!mQuestion.isAnonymous()) {
            holder.tvPersonName.setText(mQuestion.getPerson().getName());
        }
        holder.tvText.setText(mQuestion.getText());
        holder.tvTime.setText(DateUtil.getTimeUFCFormat(mQuestion.getCreated_at()));
        holder.btnLike.setText(String.valueOf(mQuestion.getUpvotes()));

        if (mQuestion.getMy_vote() == 1){
            Drawable mDrawable = colorize(R.drawable.ic_chevron_up_white, R.color.colorAccent);
            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
        }else  holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_up, 0, 0, 0);

        Preference preference = new Preference();
        if (preference.getUserProfile(mContext) == Constants.INSTRUCTOR) {
            holder.btnLike.setClickable(false);
        } else {
            holder.btnLike.setClickable(true);
        }

        try {
            YoYo.with(Techniques.ZoomIn)
                    .duration(400)
                    .playOn(holder.itemView);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Question getQuestionAdapter(int position) {
        return mList.get(position);
    }

    public void updateList(List<Question> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addItemPosition(int position, Question question) {
        mList.add(position, question);
        notifyItemInserted(position);
    }

    public void notifyItemChanged(Question doubt) {
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
        public TextView tvText, tvPersonName, tvTime;
        public Button btnLike;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            btnLike = (Button) itemView.findViewById(R.id.btn_like);
        }
    }
}
