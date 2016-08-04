package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.retrofitModel.Person;

public class ParticipantsOnlineFragment extends ParticipantsFragment {

    public static final String TAG = ParticipantsOnlineFragment.class.getName();
    public static final String ONLINE = "ONLINE";

    public static ParticipantsOnlineFragment newInstance(List<Person> list) {

        ParticipantsOnlineFragment fragment = new ParticipantsOnlineFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Person.TAG, (ArrayList<Person>) list);
        fragment.setArguments(args);

        return fragment;
    }
}
