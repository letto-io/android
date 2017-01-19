package br.com.sienaidea.oddin.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.retrofitModel.Person;

public class ParticipantsOfflineFragment extends ParticipantsFragment {

    public static final String TAG = ParticipantsOfflineFragment.class.getName();

    public static ParticipantsOfflineFragment newInstance(List<Person> list) {

        ParticipantsOfflineFragment fragment = new ParticipantsOfflineFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(Person.TAG, (ArrayList<Person>) list);
        fragment.setArguments(args);

        return fragment;
    }
}
