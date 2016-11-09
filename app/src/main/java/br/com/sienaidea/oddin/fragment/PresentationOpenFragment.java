package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Presentation;

public class PresentationOpenFragment extends PresentationFragment {

    public static final String OPEN = "OPEN";

    public static PresentationOpenFragment newInstance(List<Presentation> list, Instruction instruction) {

        PresentationOpenFragment fragment = new PresentationOpenFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Presentation.TAG, (ArrayList<Presentation>) list);
        args.putParcelable(Instruction.TAG, instruction);
        fragment.setArguments(args);

        return fragment;
    }
}
