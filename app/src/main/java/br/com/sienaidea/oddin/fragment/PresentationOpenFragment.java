package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.retrofitModel.Presentation;

public class PresentationOpenFragment extends PresentationFragment {

    public static final String OPEN = "ABERTAS";

    public static PresentationOpenFragment newInstance(List<Presentation> list, Discipline discipline) {

        PresentationOpenFragment fragment = new PresentationOpenFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Presentation.NAME, (ArrayList<Presentation>) list);
        args.putParcelable(Discipline.NAME, discipline);
        fragment.setArguments(args);

        return fragment;
    }
}
