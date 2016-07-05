package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.model.Participant;

public class ParticipantsOfflineFragment extends ParticipantsFragment {

    public static final String TAG = ParticipantsOfflineFragment.class.getName();
    public static final String OFFLINE = "OFFLINE";

    public static ParticipantsOfflineFragment newInstance(List<Participant> list) {

        ParticipantsOfflineFragment fragment = new ParticipantsOfflineFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Participant.NAME, (ArrayList<Participant>) list);
        fragment.setArguments(args);

        return fragment;
    }
}
