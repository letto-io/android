package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Presentation;

public class PresentationClosedFragment extends PresentationFragment {

    public static final String CLOSED = "FECHADAS";

    public static PresentationClosedFragment newInstance(List<Presentation> list, Discipline discipline) {

        PresentationClosedFragment fragment = new PresentationClosedFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Presentation.NAME, (ArrayList<Presentation>) list);
        args.putParcelable(Discipline.NAME, discipline);
        fragment.setArguments(args);

        return fragment;
    }
}
