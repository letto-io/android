package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Presentation;

public class PresentationClosedFragment extends PresentationFragment {

    public static final String CLOSED = "FECHADAS";

    public static PresentationClosedFragment newInstance(List<Presentation> list, Instruction instruction) {

        PresentationClosedFragment fragment = new PresentationClosedFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Presentation.TAG, (ArrayList<Presentation>) list);
        args.putParcelable(Instruction.TAG, instruction);
        fragment.setArguments(args);

        return fragment;
    }
}
