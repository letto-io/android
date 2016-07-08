package br.com.sienaidea.oddin.view;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.DisciplineFragment;
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.DateUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
import cz.msebera.android.httpclient.Header;

public class ActDiscipline extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String URL_GET_DISCIPLINE = "controller/instruction";

    private List<Discipline> mListDiscipline = new ArrayList<>();
    private Discipline discipline;
    private String userName, userEmail;
    private View mRootLayout;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private DisciplineFragment mDisciplineFragment;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_discipline);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_discipline);
        mToolbar.setTitle("Disciplinas");
        setSupportActionBar(mToolbar);

        mRootLayout = findViewById(R.id.root);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        View header = mNavigationView.getHeaderView(0);

        TextView userNameTextView = (TextView) header.findViewById(R.id.user_name_drawer);
        TextView userEmailTextView = (TextView) header.findViewById(R.id.user_email_drawer);

        if (savedInstanceState != null) {
            mListDiscipline = savedInstanceState.getParcelableArrayList(Discipline.NAME);
            userEmail = savedInstanceState.getString("userEmail");
        } else {
            userEmail = getIntent().getStringExtra("email");

            //create fragment
            mDisciplineFragment = new DisciplineFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_container, mDisciplineFragment, DisciplineFragment.TAG);
            fragmentTransaction.commit();

            loadListDiscipline();
        }

        userNameTextView.setText("User Name");
        if (userEmail != null) {
            userEmailTextView.setText(userEmail);
        } else {
            userEmailTextView.setText("email@email.com");
        }
    }

    public void loadListDiscipline() {
        DetectConnection detectConnection = new DetectConnection(this);

        if (detectConnection.existConnection()) {

            BossClient.get(URL_GET_DISCIPLINE, null, CookieUtil.getCookie(getApplicationContext()), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray lectures = response.getJSONArray("lectures");
                        Log.d("DISCIPLINES", lectures.toString());
                        mListDiscipline.clear();
                        for (int i = 0; i < lectures.length(); i++) {

                            String tempDateFormat = DateUtil.getDateFormat(lectures.getJSONObject(i).getString("startdate") + " 00:00:00");
                            discipline = new Discipline();
                            discipline.setCodigo(lectures.getJSONObject(i).getString("code"));
                            discipline.setNome(lectures.getJSONObject(i).getString("name"));
                            discipline.setDataInicio(tempDateFormat);
                            discipline.setTurma(lectures.getJSONObject(i).getInt("class"));
                            discipline.setCodEvent(lectures.getJSONObject(i).getJSONObject("event").getString("code"));
                            discipline.setProfile(lectures.getJSONObject(i).getInt("profile"));
                            discipline.setInstruction_id(lectures.getJSONObject(i).getInt("id"));

                            addListDiscipline(discipline);
                        }

                        mDisciplineFragment = (DisciplineFragment) fragmentManager.findFragmentByTag(DisciplineFragment.TAG);
                        if (mDisciplineFragment != null) {
                            mDisciplineFragment.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (mDisciplineFragment != null) {
                        mDisciplineFragment.swipeRefreshStop();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    switch (statusCode) {
                        case 401:
                            BossClient.clearCookie(new PersistentCookieStore(getApplicationContext()));
                            startActivity(new Intent(getApplication(), LoginActivity.class));
                            Toast.makeText(getApplicationContext(), R.string.error_session_expired, Toast.LENGTH_LONG).show();
                            finish();
                            break;
                        default:
                            BossClient.clearCookie(new PersistentCookieStore(getApplicationContext()));
                            startActivity(new Intent(getApplication(), LoginActivity.class));
                            Toast.makeText(getApplicationContext(), R.string.error_session_expired, Toast.LENGTH_LONG).show();
                            finish();
                    }

                    mDisciplineFragment.swipeRefreshStop();
                }
            });
        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadListDiscipline();
                        }
                    }).show();
        }
    }

    public List<Discipline> getListDiscipline() {
        return mListDiscipline;
    }

    private void addListDiscipline(Discipline discipline) {
        mListDiscipline.add(discipline);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Discipline.NAME, (ArrayList<Discipline>) mListDiscipline);
        outState.putString("userEmail", userEmail);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        //super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (item.getItemId() == R.id.action_logout) {
            BossClient.clearCookie(new PersistentCookieStore(getApplicationContext()));
            startActivity(new Intent(ActDiscipline.this, LoginActivity.class));
            finish();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
