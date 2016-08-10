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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.LectureFragment;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Person;
import br.com.sienaidea.oddin.retrofitModel.User;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.DateUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LectureActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<Instruction> mListInstruction = new ArrayList<>();
    private String userName, userEmail;
    private View mRootLayout;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private LectureFragment mLectureFragment;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_discipline);
        mToolbar.setTitle("Disciplinas");
        setSupportActionBar(mToolbar);

        //acessando o sharedPreferences para mostrar os dados do usuario no Drawer (name and email)
        Preference preference = new Preference();
        userName = preference.getUserName(getApplicationContext());
        userEmail = preference.getUserEmail(getApplicationContext());

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
            mListInstruction = savedInstanceState.getParcelableArrayList(Instruction.TAG);
            userName = savedInstanceState.getString(Person.NAME);
            userEmail = savedInstanceState.getString(User.EMAIL);
        } else {
            getInstructions();
        }

        //verifica se existe realmente o nome e email já salvo e coloca no drawer
        if (userName != null) {
            userNameTextView.setText(userName);
        } else {
            userNameTextView.setText("user name");
        }
        if (userEmail != null) {
            userEmailTextView.setText(userEmail);
        } else {
            userEmailTextView.setText("email@email.com");
        }


    }

    private void getInstructions() {
        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            final HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Preference preference = new Preference();
            final String auth_token_string = preference.getToken(getApplicationContext());

            Call<List<Instruction>> request = service.Instructions(auth_token_string);

            request.enqueue(new Callback<List<Instruction>>() {
                @Override
                public void onResponse(Call<List<Instruction>> call, Response<List<Instruction>> response) {
                    if (response.isSuccessful()) {
                        mListInstruction.clear();
                        mListInstruction = response.body();
                        onRequestSuccess();
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Instruction>> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getInstructions();
                        }
                    }).show();
        }
    }

    private void onRequestSuccess() {
        mLectureFragment = (LectureFragment) fragmentManager.findFragmentByTag(LectureFragment.TAG);
        if (mLectureFragment != null) {
            //refresh fragment
            mLectureFragment.notifyDataSetChanged();
        } else {
            //create fragment
            mLectureFragment = new LectureFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_container, mLectureFragment, LectureFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    private void onRequestFailure(int statusCode) {
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

    public List<Instruction> getList() {
        return mListInstruction;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Instruction.TAG, (ArrayList<Instruction>) mListInstruction);
        outState.putString(Person.NAME, userName);
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
            startActivity(new Intent(LectureActivity.this, LoginActivity.class));
            finish();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
