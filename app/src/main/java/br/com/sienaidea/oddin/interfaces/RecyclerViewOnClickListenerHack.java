package br.com.sienaidea.oddin.interfaces;

import android.view.View;

public interface RecyclerViewOnClickListenerHack {
    void onClickListener(View view, int position);
    void onClickListener(View view, int position, String option);
    void onClickListener(View view, int position, boolean option);
}
