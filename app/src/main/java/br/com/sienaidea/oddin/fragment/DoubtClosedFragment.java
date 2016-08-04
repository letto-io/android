package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Question;

public class DoubtClosedFragment extends DoubtFragment {

    public static final String CLOSED = "FECHADAS";

    public static DoubtClosedFragment newInstance(List<Question> list, Presentation presentation) {

        DoubtClosedFragment fragment = new DoubtClosedFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Question.TAG, (ArrayList<Question>) list);
        args.putParcelable(Presentation.TAG, presentation);
        fragment.setArguments(args);

        return fragment;
    }
}
