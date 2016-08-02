package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.retrofitModel.Presentation;

public class DoubtClosedFragment extends DoubtFragment {

    public static final String CLOSED = "FECHADAS";

    public static DoubtClosedFragment newInstance(List<Doubt> list, Discipline discipline, Presentation presentation) {

        DoubtClosedFragment fragment = new DoubtClosedFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Doubt.NAME, (ArrayList<Doubt>) list);
        args.putParcelable(Discipline.NAME, discipline);
        args.putParcelable(Presentation.TAG, presentation);
        fragment.setArguments(args);

        return fragment;
    }
}
