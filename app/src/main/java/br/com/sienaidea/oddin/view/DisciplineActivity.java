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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.DisciplineFragment;
import br.com.sienaidea.oddin.retrofitModel.Lecture;
import br.com.sienaidea.oddin.retrofitModel.Session;
import br.com.sienaidea.oddin.retrofitModel.User;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DisciplineActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<Lecture> mList = new ArrayList<>();
    private String userName, userEmail;
    private View mRootLayout;
    private Session mSession;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private DisciplineFragment mDisciplineFragment;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discipline);

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
            mList = savedInstanceState.getParcelableArrayList(Lecture.TAG);
            userEmail = savedInstanceState.getString(User.EMAIL);
        } else {
            userEmail = getIntent().getStringExtra(User.EMAIL);
            mSession = getIntent().getParcelableExtra(Session.TAG);

            getLectures();
        }

        userNameTextView.setText("User Name");
        if (userEmail != null) {
            userEmailTextView.setText(userEmail);
        } else {
            userEmailTextView.setText("email@email.com");
        }
    }

    public void getLectures() {
        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Call<List<Lecture>> request = service.Lectures(mSession.getToken());

            request.enqueue(new Callback<List<Lecture>>() {
                @Override
                public void onResponse(Call<List<Lecture>> call, Response<List<Lecture>> response) {
                    if (response.isSuccessful()) {
                        mList.clear();
                        mList = response.body();
                        onRequestSuccess();
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Lecture>> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getLectures();
                        }
                    }).show();
        }
    }

    private void onRequestSuccess() {
        mDisciplineFragment = (DisciplineFragment) fragmentManager.findFragmentByTag(DisciplineFragment.TAG);
        if (mDisciplineFragment != null) {
            //refresh fragment
            mDisciplineFragment.notifyDataSetChanged();
        } else {
            //create fragment
            mDisciplineFragment = new DisciplineFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_container, mDisciplineFragment, DisciplineFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    private void onRequestFailure(int statusCode) {
        mDisciplineFragment.swipeRefreshStop();
        if (statusCode == 401) {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            Toast.makeText(getApplicationContext(), R.string.error_session_expired, Toast.LENGTH_LONG).show();
            finish();
        } else {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            Toast.makeText(getApplicationContext(), R.string.error_session_expired, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public List<Lecture> getList() {
        return mList;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Lecture.TAG, (ArrayList<Lecture>) mList);
        outState.putString(User.EMAIL, userEmail);
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
            //TODO clear session
            startActivity(new Intent(DisciplineActivity.this, LoginActivity.class));
            finish();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
