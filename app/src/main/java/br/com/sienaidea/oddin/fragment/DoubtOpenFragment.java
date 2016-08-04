package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Question;

public class DoubtOpenFragment extends DoubtFragment {

    public static final String TAG = DoubtOpenFragment.class.getName();
    public static final String OPEN = "ABERTAS";

    public static DoubtOpenFragment newInstance(List<Question> list, Presentation presentation) {

        DoubtOpenFragment fragment = new DoubtOpenFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Question.TAG, (ArrayList<Question>) list);
        args.putParcelable(Presentation.TAG, presentation);
        fragment.setArguments(args);

        return fragment;
    }
}
