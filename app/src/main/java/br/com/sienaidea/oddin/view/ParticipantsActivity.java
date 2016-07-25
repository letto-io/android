package br.com.sienaidea.oddin.view;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterViewPager;
import br.com.sienaidea.oddin.fragment.ParticipantsFragment;
import br.com.sienaidea.oddin.fragment.ParticipantsOfflineFragment;
import br.com.sienaidea.oddin.fragment.ParticipantsOnlineFragment;
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Participant;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.util.CookieUtil;
import cz.msebera.android.httpclient.Header;

public class ParticipantsActivity extends AppCompatActivity {
    private static String URL_GET_PARTICIPANTS;
    private static String TAB_POSITION = "TAB_POSITION";

    private List<Participant> mList = new ArrayList<>();
    private Discipline mDiscipline;
    private Participant mParticipant;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ParticipantsFragment mParticipantsFragment;
    private ParticipantsOnlineFragment mParticipantsOnlineFragment;
    private ParticipantsOfflineFragment mParticipantsOfflineFragment;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AdapterViewPager mAdapterViewPager;
    private int mSelectedTabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        mTabLayout = (TabLayout) findViewById(R.id.tab_participants);
        mViewPager = (ViewPager) findViewById(R.id.vp_participants);

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList("mList");
            mDiscipline = savedInstanceState.getParcelable(Discipline.NAME);
            mViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION));
            setupViewPager(mViewPager);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Discipline.NAME) != null) {
                mDiscipline = getIntent().getParcelableExtra(Discipline.NAME);
                URL_GET_PARTICIPANTS = "controller/instruction/" + mDiscipline.getInstruction_id() + "/participants";

                getParticipants();
            } else {
                Toast.makeText(this, R.string.toast_fails_to_start, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_participants);
        mToolbar.setTitle(mDiscipline.getNome());
        mToolbar.setSubtitle("Participantes");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewPager(final ViewPager viewPager) {
        mAdapterViewPager = new AdapterViewPager(fragmentManager);

        mParticipantsFragment = ParticipantsFragment.newInstance(getListParticipants());
        mParticipantsOnlineFragment = ParticipantsOnlineFragment.newInstance(getListParticipantsOnline());
        mParticipantsOfflineFragment = ParticipantsOfflineFragment.newInstance(getListParticipantsOffline());

        mAdapterViewPager.addFragment(mParticipantsFragment, ParticipantsFragment.ALL);
        mAdapterViewPager.addFragment(mParticipantsOnlineFragment, ParticipantsOnlineFragment.ONLINE);
        mAdapterViewPager.addFragment(mParticipantsOfflineFragment, ParticipantsOfflineFragment.OFFLINE);

        viewPager.setAdapter(mAdapterViewPager);

        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.xml.selector));
        mTabLayout.setupWithViewPager(viewPager);
    }

    public void getParticipants() {
        BossClient.get(URL_GET_PARTICIPANTS, null, CookieUtil.getCookie(this), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray participants = response.getJSONArray("participants");

                    mList.clear();

                    for (int i = 0; i < participants.length(); i++) {
                        mParticipant = new Participant();

                        mParticipant.setName(participants.getJSONObject(i).getString("name"));
                        mParticipant.setProfile(participants.getJSONObject(i).getInt("profile"));
                        mParticipant.setOnline(participants.getJSONObject(i).getBoolean("online"));

                        addItemList(mParticipant);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSelectedTabPosition = mTabLayout.getSelectedTabPosition();
                setupViewPager(mViewPager);
                mViewPager.setCurrentItem(mSelectedTabPosition);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void addItemList(Participant participant) {
        mList.add(participant);
    }

    private List<Participant> getListParticipants() {
        return mList;
    }

    private List<Participant> getListParticipantsOnline() {
        List<Participant> listAux = new ArrayList<>();

        for (Participant participant : mList) {
            if (participant.isOnline()) {
                listAux.add(participant);
            }
        }

        return listAux;
    }

    private List<Participant> getListParticipantsOffline() {
        List<Participant> listAux = new ArrayList<>();

        for (Participant participant : mList) {
            if (!participant.isOnline()) {
                listAux.add(participant);
            }
        }

        return listAux;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Discipline.NAME, mDiscipline);
        outState.putParcelableArrayList("mList", (ArrayList<Participant>) mList);
        outState.putInt(TAB_POSITION, mTabLayout.getSelectedTabPosition());
        super.onSaveInstanceState(outState);
    }
}
