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
import android.widget.Toast;

import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Answer;
import br.com.sienaidea.oddin.util.DateUtil;
import br.com.sienaidea.oddin.view.DoubtDetailsActivity;

public class AdapterContribution extends RecyclerView.Adapter<AdapterContribution.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Answer> mList;
    private Context mContext;
    private int mProfile;
    private boolean isQuestionOwner;

    public AdapterContribution(Context context, List<Answer> list, int profile, boolean isQuestionOwner) {
        this.mContext = context;
        this.mList = list;
        this.mProfile = profile;
        this.isQuestionOwner = isQuestionOwner;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_contribution_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Answer answer = mList.get(position);

        holder.tvPersonName.setText(answer.getPerson().getName());
        holder.tvText.setText(answer.getText());
        holder.tvCreatedat.setText(DateUtil.getDateUFCFormat(answer.getCreated_at()));
        holder.tvVote.setText(String.valueOf(answer.getUpvotes()));

        if (mProfile == Constants.INSTRUCTOR) {
            holder.ivUpVote.setEnabled(false);
            holder.ivUpVote.setClickable(false);
            holder.ivDownVote.setEnabled(false);
            holder.ivDownVote.setClickable(false);
            holder.ivAccept.setVisibility(View.GONE);
        } else {
            holder.ivUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer.getMy_vote() == Constants.UP_VOTE) {
                        Toast.makeText(mContext, R.string.toast_voted, Toast.LENGTH_SHORT).show();
                    } else {
                        ((DoubtDetailsActivity) mContext).upVote(answer);
                    }
                }
            });

            holder.ivDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer.getMy_vote() == Constants.DOWN_VOTE) {
                        Toast.makeText(mContext, R.string.toast_voted, Toast.LENGTH_SHORT).show();
                    } else {
                        ((DoubtDetailsActivity) mContext).downVote(answer);
                    }
                }
            });

            //somente se a dúvida for da pessoa que a opçao "aceitar" será exibida.
            if (isQuestionOwner) {
                holder.ivAccept.setVisibility(View.VISIBLE);
                holder.ivAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        builder.setNegativeButton(R.string.dialog_cancel, null);
                        if (answer.isAccepted()) {
                            builder.setMessage(R.string.dialog_delete_accept_answer);
                            builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((DoubtDetailsActivity) mContext).deleteAcceptAnswer(answer);
                                }
                            });
                            builder.show();
                        } else {
                            builder.setMessage(R.string.dialog_accept_answer);
                            builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((DoubtDetailsActivity) mContext).acceptAnswer(answer);
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }else {
                holder.ivAccept.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPersonName;
        private TextView tvText, tvVote;
        private TextView tvCreatedat;
        private ImageView ivUpVote, ivDownVote, ivAccept;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            tvCreatedat = (TextView) itemView.findViewById(R.id.tv_createdat);
            ivUpVote = (ImageView) itemView.findViewById(R.id.iv_upvote);
            ivDownVote = (ImageView) itemView.findViewById(R.id.iv_downvote);
            tvVote = (TextView) itemView.findViewById(R.id.tv_vote);
            ivAccept = (ImageView) itemView.findViewById(R.id.iv_accepted);
        }
    }
}
