package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.model.Presentation;

public class DoubtRankingFragment extends DoubtFragment {

    public static final String TAG = DoubtRankingFragment.class.getName();
    public static final String RANKING = "RANKING";

    public static DoubtRankingFragment newInstance(List<Doubt> list, Discipline discipline, Presentation presentation) {

        DoubtRankingFragment fragment = new DoubtRankingFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Doubt.NAME, (ArrayList<Doubt>) list);
        args.putParcelable(Discipline.NAME, discipline);
        args.putParcelable(Presentation.NAME, presentation);
        fragment.setArguments(args);

        return fragment;
    }
}
