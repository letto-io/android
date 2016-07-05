package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.model.Presentation;

public class DoubtOpenFragment extends DoubtFragment {

    public static final String TAG = DoubtOpenFragment.class.getName();
    public static final String OPEN = "ABERTAS";

    public static DoubtOpenFragment newInstance(List<Doubt> list, Discipline discipline, Presentation presentation) {

        DoubtOpenFragment fragment = new DoubtOpenFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Doubt.NAME, (ArrayList<Doubt>) list);
        args.putParcelable(Discipline.NAME, discipline);
        args.putParcelable(Presentation.NAME, presentation);
        fragment.setArguments(args);

        return fragment;
    }
}
