package br.com.sienaidea.oddin.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.adapter.AdapterViewPager;
import br.com.sienaidea.oddin.fragment.AudioDoubtDetailFragment;
import br.com.sienaidea.oddin.fragment.FragmentDoubtDetailText;
import br.com.sienaidea.oddin.fragment.MaterialDoubtDetailFragment;
import br.com.sienaidea.oddin.fragment.VideoDoubtDetailFragment;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.model.Contribution;
import br.com.sienaidea.oddin.model.Discipline;
import br.com.sienaidea.oddin.model.Doubt;
import br.com.sienaidea.oddin.model.Material;
import br.com.sienaidea.oddin.model.MaterialDoubt;
import br.com.sienaidea.oddin.model.Presentation;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.FileUtils;
import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActDoubtDetails extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "ActDoubtDetails";
    private static String URL_GET_CONTRIBUTION;
    private static String URL_GET_MATERIAL;

    private static int RECORD_SOUND_ACTION_REQUEST = 345;
    private static int ACTION_VIDEO_CAPTURE_REQUEST = 578;
    private static int ACTION_GET_CONTENT_REQUEST = 499;
    private static int ACTION_POST_TEXT_REQUEST = 753;

    private static final int REQUEST_PERMISSION_DOWNLOAD = 87;
    private static final int REQUEST_PERMISSION_UPLOAD = 78;
    private static final int REQUEST_PERMISSION_RECORD_SOUND = 74;
    private static final int REQUEST_PERMISSION_CAMERA = 85;

    private List<Contribution> mList = new ArrayList<>();
    private List<Material> mListMaterial = new ArrayList<>();
    private Contribution mContribution;
    private MaterialDoubt mMaterial;
    private Doubt mDoubt;
    private Discipline mDiscipline;
    private Presentation mPresentation;
    private FloatingActionButton fab;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentDoubtDetailText fragmentDoubtDetailText;

    private AudioDoubtDetailFragment mAudioDoubtDetailFragment;
    private MaterialDoubtDetailFragment mMaterialDoubtDetailFragment;
    private VideoDoubtDetailFragment mVideoDoubtDetailFragment;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AdapterViewPager mAdapterViewPager;
    private int mSelectedTabPosition;

    private TextView mTvPersonName;
    private TextView mTvText;

    private String mFileName;
    private Uri returnUri;
    private File mTempFile;

    private int[] tabIcons = {
            R.drawable.ic_format_text_selector,
            R.drawable.ic_microphone_selector,
            R.drawable.ic_video_selector,
            R.drawable.ic_paperclip_selector
    };

    private int[] fabIcons = {R.drawable.ic_plus_white,R.drawable.ic_microphone};

    private int mMaxProgress = 100;
    private LinkedList<ProgressType> mProgressTypes;
    private Handler mUiHandler = new Handler();

    private static String mFileNameRecord = null;
    private MediaRecorder mRecorder = null;

    private MediaPlayer mPlayer = null;

    private int mPositionFragment;
    private MaterialDoubt mMaterialFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_doubt_details);

        mTabLayout = (TabLayout) findViewById(R.id.tab_doubt_details);
        mViewPager = (ViewPager) findViewById(R.id.vp_doubt_details);

        mTvPersonName = (TextView) findViewById(R.id.tv_doubt_details_person_name);
        mTvText = (TextView) findViewById(R.id.tv_doubt_details_text);

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList(Contribution.NAME);
            mDoubt = savedInstanceState.getParcelable(Doubt.NAME);
            mDiscipline = savedInstanceState.getParcelable(Discipline.NAME);
            mPresentation = savedInstanceState.getParcelable(Presentation.NAME);
            mTvPersonName.setText(mDoubt.getPerson().getName());
            mTvText.setText(mDoubt.getText());
            setupViewPager(mViewPager);
        } else {
            if (getIntent().getExtras() != null) {
                mDoubt = getIntent().getExtras().getParcelable(Doubt.NAME);
                mDiscipline = getIntent().getExtras().getParcelable(Discipline.NAME);
                mPresentation = getIntent().getExtras().getParcelable(Presentation.NAME);
                mTvPersonName.setText(mDoubt.getPerson().getName());
                mTvText.setText(mDoubt.getText());
                URL_GET_CONTRIBUTION = "controller/instruction/" + mDiscipline.getInstruction_id() + "/presentation/" + mDoubt.getPresentation_id() + "/doubt/" + mDoubt.getId() + "/contribution";
                URL_GET_MATERIAL = "controller/instruction/" + mDiscipline.getInstruction_id() + "/presentation/" + mDoubt.getPresentation_id() + "/doubt/" + mDoubt.getId() + "/contribution/";
                getContentDoubt();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_doubt_details);
        toolbar.setTitle(mDiscipline.getNome());
        toolbar.setSubtitle(mPresentation.getSubject());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgressTypes = new LinkedList<>();
        for (ProgressType type : ProgressType.values()) {
            mProgressTypes.offer(type);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if (mDiscipline.getProfile() == 2) {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    fab.hide(true);
                    animateFab(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(this);

        }

    }

    public void fabHide(){
        fab.hide(true);
    }

    public void fabShow(){
        fab.show(true);
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(getAudioRecordName());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            Toast.makeText(getApplicationContext(), "Gravando...", Toast.LENGTH_LONG).show();
            fab.setShowProgressBackground(true);
            fab.setIndeterminate(true);
            mProgressTypes.offer(ProgressType.INDETERMINATE);

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Falha ao começar a gravar...", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        fab.hideProgress();
        mProgressTypes.offer(ProgressType.HIDDEN);

        final EditText inputName = new EditText(ActDoubtDetails.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputName.setLayoutParams(lp);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(ActDoubtDetails.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Nova contribuição");
        inputName.setText(mFileName);
        builder.setView(inputName);
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.setPositiveButton(R.string.dialog_send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!TextUtils.isEmpty(inputName.getText())) {
                    mFileName = inputName.getText().toString();
                }

                mTempFile = FileUtils.getFileFromPath(mFileNameRecord);
                uploadFile(0, Constants.MIME_TYPE_AUDIO);
            }
        });
        builder.show();
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileNameRecord);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private String getAudioRecordName() {
        mFileNameRecord = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileNameRecord += "/audiorecordoddin.3gp";
        mFileName = "AUDIO_" + new Date().getTime();
        return mFileNameRecord;
    }

    private void increaseProgress(final FloatingActionButton fab, int i) {
        if (i <= mMaxProgress) {
            fab.setProgress(i, false);
            final int progress = ++i;
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    increaseProgress(fab, progress);
                }
            }, 30);
        } else {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.hideProgress();
                }
            }, 200);
            // mProgressTypes.offer(ProgressType.PROGRESS_NO_ANIMATION);
        }
    }

    private enum ProgressType {
        INDETERMINATE, HIDDEN
        //INDETERMINATE, PROGRESS_POSITIVE, PROGRESS_NEGATIVE, HIDDEN, PROGRESS_NO_ANIMATION, PROGRESS_NO_BACKGROUND
    }

    private void animateFab(final int position) {
        if (position == 1){
            // Change FAB color icon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fab.setImageDrawable(getResources().getDrawable(fabIcons[position], null));
            } else {
                fab.setImageDrawable(getResources().getDrawable(fabIcons[position]));
            }

            fab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    fab.setOnClickListener(null);

                    if (mViewPager.getCurrentItem() == 1) {
                        ProgressType type = mProgressTypes.poll();
                        switch (type) {
                            case INDETERMINATE:
                                checkOddinPermission(REQUEST_PERMISSION_RECORD_SOUND);
                                break;
                            case HIDDEN:
                                stopRecording();
                                break;
                        }
                    }
                    return false;
                }
            });

        }else {
            // Change FAB color icon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fab.setImageDrawable(getResources().getDrawable(fabIcons[0], null));
            } else {
                fab.setImageDrawable(getResources().getDrawable(fabIcons[0]));
            }

            fab.setOnClickListener(this);

        }
        fab.show(true);
    }

    public void getContentDoubt() {
        BossClient.get(URL_GET_CONTRIBUTION, null, CookieUtil.getCookie(this), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                try {
                    JSONArray contributions = response.getJSONArray("contributions");
                    Log.d("CONTRIBUTIONS", contributions.toString());

                    mList.clear();
                    mListMaterial.clear();

                    for (int i = 0; i < contributions.length(); i++) {
                        mContribution = new Contribution();
                        mMaterial = new MaterialDoubt();

                        mContribution.setId(contributions.getJSONObject(i).getInt("id"));
                        mContribution.setText(contributions.getJSONObject(i).getString("text"));
                        mContribution.setCreatedat(contributions.getJSONObject(i).getString("createdat"));
                        mContribution.setPersonName(contributions.getJSONObject(i).getJSONObject("person").getString("name"));

                        mMaterial.setId(contributions.getJSONObject(i).getJSONObject("mcmaterial").getInt("id"));
                        mMaterial.setName(contributions.getJSONObject(i).getJSONObject("mcmaterial").getString("name"));
                        mMaterial.setMime(contributions.getJSONObject(i).getJSONObject("mcmaterial").getString("mime"));
                        mMaterial.setContribution_id(contributions.getJSONObject(i).getInt("id"));

                        if (!mContribution.getText().equalsIgnoreCase("null")) {
                            addItemList(mContribution);
                        }
                        addItemList(mMaterial);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSelectedTabPosition = mTabLayout.getSelectedTabPosition();
                setupViewPager(mViewPager);
                mViewPager.setCurrentItem(mSelectedTabPosition);
                mAdapterViewPager.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.toString(), Toast.LENGTH_LONG).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void openFileManager() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType(Constants.MIME_TYPE_PDF);
        intent.setType(Constants.MIME_TYPE_TEXT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, ACTION_GET_CONTENT_REQUEST);
    }

    private void openCamera() {
        startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE), ACTION_VIDEO_CAPTURE_REQUEST);
    }

    private void openRecordAudio() {

        //TODO: tratar quando não tiver gravador de som.
        startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), RECORD_SOUND_ACTION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i("TAG", "onRequestPermissionsResult");
        switch (requestCode) {

            case REQUEST_PERMISSION_DOWNLOAD:
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getMaterialContent(mPositionFragment, mMaterialFragment);
                        return;
                    } else if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getMaterialContent(mPositionFragment, mMaterialFragment);
                        return;
                    }
                }

            case REQUEST_PERMISSION_UPLOAD:
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        openFileManager();
                        return;
                    } else if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        openFileManager();
                        return;
                    }
                }

            case REQUEST_PERMISSION_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                        return;
                    }
                }
                break;

            case REQUEST_PERMISSION_RECORD_SOUND:
                boolean isRecordable = false;
                boolean isWritable = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.RECORD_AUDIO) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        isRecordable = true;
                    } else if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        isWritable = true;
                    }
                }

                if (isRecordable && isWritable) {
                    startRecording();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void attemptGetMaterialContent(int position, MaterialDoubt material) {

        mPositionFragment = position;
        mMaterialFragment = material;

        //se uma das duas permissões não estiverem liberadas
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //verifica se já foi recusado a permissão de escrita
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                callDialog("É preciso a permission WRITE_EXTERNAL_STORAGE para SALVAR o conteudo em seu aparelho.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_DOWNLOAD);
                return;
            }

            //verifica se já foi recusado a permissão de leitura
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                callDialog("É preciso a permission READ_EXTERNAL_STORAGE para LER o conteudo em seu aparelho.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_DOWNLOAD);
                return;
            }

            //caso nenhuma das duas permissões nunca estiverem sido negadas, será solicitado aqui
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_DOWNLOAD);

        } else {
            //e por fim, caso já tenha permiçoes, faça download
            getMaterialContent(mPositionFragment, mMaterialFragment);
        }

    }

    public void getMaterialContent(final int position, final MaterialDoubt material) {
        BossClient.get(URL_GET_MATERIAL + material.getContribution_id() + "/materials/" + material.getId(), null, CookieUtil.getCookie(getApplicationContext()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (FileUtils.isExternalStorageWritable()) {
                    File root = Environment.getExternalStorageDirectory();

                    File dirOddin = new File(root.getAbsolutePath() + "/Oddin");
                    if (!dirOddin.exists()) {
                        dirOddin.mkdir();
                    }

                    File dirDiscipline = new File(dirOddin.getAbsolutePath() + "/" + mDiscipline.getNome());
                    if (!dirDiscipline.exists()) {
                        dirDiscipline.mkdir();
                    }

                    final File file = new File(dirDiscipline.getAbsolutePath(), material.getName());
                    try {

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(responseBody);
                        fileOutputStream.close();

                        Fragment fragment = mAdapterViewPager.getItem(mViewPager.getCurrentItem());

                        material.setDownloaded(true);

                        if (fragment instanceof AudioDoubtDetailFragment) {
                            mAudioDoubtDetailFragment.downloadFinished(position, Uri.parse("file://" + file.getPath()));
                        } else {
                            if (fragment instanceof MaterialDoubtDetailFragment) {
                                mMaterialDoubtDetailFragment.downloadFinished(position, Uri.parse("file://" + file.getPath()));
                            } else if (fragment instanceof VideoDoubtDetailFragment) {
                                mVideoDoubtDetailFragment.downloadFinished(position, Uri.parse("file://" + file.getPath()));
                            }

                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(ActDoubtDetails.this, R.style.AppCompatAlertDialogStyle);
                            builder.setMessage("Material salvo em: " + file.getAbsolutePath());
                            builder.setPositiveButton("OK", null);
                            builder.setNegativeButton("ABRIR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent newIntent = new Intent();
                                    newIntent.setDataAndType(Uri.parse("file://" + file.getPath()), material.getMime());
                                    newIntent.setAction(Intent.ACTION_VIEW);
                                    try {
                                        startActivity(newIntent);
                                    } catch (android.content.ActivityNotFoundException e) {
                                        Toast.makeText(getApplicationContext(), "No handler for this type of file.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Não foi possível salvar o arquivo.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Não foi possível fazer a requisição no servidor, tente novamente.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupViewPager(final ViewPager viewPager) {

        mAdapterViewPager = new AdapterViewPager(fragmentManager);

        fragmentDoubtDetailText = FragmentDoubtDetailText.newInstance(getList(), mDiscipline.getProfile());

        mAudioDoubtDetailFragment = AudioDoubtDetailFragment.newInstance(getAudio(), mDiscipline.getProfile());
        mVideoDoubtDetailFragment = VideoDoubtDetailFragment.newInstance(getVideo(), mDiscipline.getProfile());
        mMaterialDoubtDetailFragment = MaterialDoubtDetailFragment.newInstance(getAttachment(), mDiscipline.getProfile());

        mAdapterViewPager.addFragment(fragmentDoubtDetailText, "");
        mAdapterViewPager.addFragment(mAudioDoubtDetailFragment, "");
        mAdapterViewPager.addFragment(mVideoDoubtDetailFragment, "");
        mAdapterViewPager.addFragment(mMaterialDoubtDetailFragment, "");

        viewPager.setAdapter(mAdapterViewPager);

        mTabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabIcons.length; i++) {
            if (mTabLayout.getTabAt(i) != null)
                mTabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    private List<Contribution> getList() {
        return mList;
    }

    private List<Material> getListMaterial() {
        return mListMaterial;
    }

    private void addItemList(Contribution contribution) {
        mList.add(contribution);
    }

    private void addItemList(Material material) {
        mListMaterial.add(material);
    }

    private List<Material> getAudio() {
        List<Material> listAux = new ArrayList<>();

        for (Material material : mListMaterial) {
            if (material.getMime().equalsIgnoreCase("audio/3gpp")) {
                listAux.add(material);
            }
        }
        return listAux;
    }

    private List<Material> getVideo() {
        List<Material> listAux = new ArrayList<>();

        for (Material material : mListMaterial) {
            if (material.getMime().equalsIgnoreCase("video/mp4")) {
                listAux.add(material);
            }
        }
        return listAux;
    }

    private List<Material> getAttachment() {
        List<Material> listAux = new ArrayList<>();

        for (Material material : mListMaterial) {
            if (material.getMime().equalsIgnoreCase("application/pdf")) {
                listAux.add(material);
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
        outState.putParcelableArrayList(Contribution.NAME, (ArrayList<Contribution>) mList);
        outState.putParcelable(Discipline.NAME, mDiscipline);
        outState.putParcelable(Presentation.NAME, mPresentation);
        outState.putParcelable(Doubt.NAME, mDoubt);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            int position = mViewPager.getCurrentItem();

            switch (position) {
                case 0:
                    Intent intent = new Intent(ActDoubtDetails.this, ActNewContribution.class);
                    intent.putExtra(Presentation.NAME, mPresentation);
                    intent.putExtra(Doubt.NAME, mDoubt);
                    startActivityForResult(intent, ACTION_POST_TEXT_REQUEST);
                    break;
                case 1:
                    Toast.makeText(this, "Pressione por alguns segundos para começar a gravar e depois para finalizar", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    checkOddinPermission(REQUEST_PERMISSION_CAMERA);
                    break;
                case 3:
                    checkOddinPermission(REQUEST_PERMISSION_UPLOAD);
                    break;
            }
        }
    }

    private void checkOddinPermission(int requestCode) {

        switch (requestCode) {
            case REQUEST_PERMISSION_UPLOAD:
                //se uma das duas permissões não estiverem liberadas
                if (ContextCompat.checkSelfPermission(ActDoubtDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ActDoubtDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    //verifica se já foi recusado a permissão de escrita
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ActDoubtDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        callDialog("É preciso a permission WRITE_EXTERNAL_STORAGE para acessar o conteudo em seu aparelho.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        return;
                    }

                    //verifica se já foi recusado a permissão de leitura
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ActDoubtDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        callDialog("É preciso a permission READ_EXTERNAL_STORAGE para acessar o conteudo em seu aparelho.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                        return;
                    }

                    //caso nenhuma das duas permissões nunca estiverem sido negadas, será solicitado aqui
                    ActivityCompat.requestPermissions(ActDoubtDetails.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);

                } else
                    openFileManager();
                break;

            case REQUEST_PERMISSION_CAMERA:
                //verifica se já foi liberado
                if (ContextCompat.checkSelfPermission(ActDoubtDetails.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    //verifica se já foi recusado
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ActDoubtDetails.this, Manifest.permission.CAMERA)) {
                        callDialog("É preciso acessar a câmera em seu aparelho.", new String[]{Manifest.permission.CAMERA}, requestCode);
                        return;
                    }

                    //caso for a primeira vez que precisa do acesso, será solicitado aqui
                    ActivityCompat.requestPermissions(ActDoubtDetails.this, new String[]{Manifest.permission.CAMERA}, requestCode);

                } else
                    openCamera();
                break;

            case REQUEST_PERMISSION_RECORD_SOUND:
                //verifica se já foi liberado
                if (ContextCompat.checkSelfPermission(ActDoubtDetails.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ActDoubtDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    //verifica se já foi recusado
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ActDoubtDetails.this, Manifest.permission.RECORD_AUDIO)) {
                        callDialog("É preciso acessar o gravador de áudio em seu aparelho.", new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
                    } else
                        //caso for a primeira vez que precisa do acesso, será solicitado aqui
                        ActivityCompat.requestPermissions(ActDoubtDetails.this, new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(ActDoubtDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        callDialog("É preciso acesso para gravar em seu aparelho.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    } else
                        //caso for a primeira vez que precisa do acesso, será solicitado aqui
                        ActivityCompat.requestPermissions(ActDoubtDetails.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                } else
                    startRecording();
                break;

            case REQUEST_PERMISSION_DOWNLOAD:
                //TODO download aqui
                break;
        }
    }

    private void callDialog(String message, final String[] permissions, final int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActDoubtDetails.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(ActDoubtDetails.this, permissions, requestCode);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.show();

    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            final EditText inputName = new EditText(ActDoubtDetails.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            inputName.setLayoutParams(lp);

            if (requestCode == ACTION_POST_TEXT_REQUEST) {
                getContentDoubt();
            } else if (requestCode == ACTION_GET_CONTENT_REQUEST) {

                /*
                * Get the file's content URI from the incoming Intent,
                * then query the server app to get the file's display name
                * and size.
                * */
                Log.d(LOG_TAG, data.getData().getPath());
                returnUri = data.getData();
                final String mimeType = getContentResolver().getType(returnUri);

                final Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                /*
                * Get the column indexes of the data in the Cursor,
                * move to the first row in the Cursor, get the data,
                * and display it.
                */

                if (returnCursor != null) {
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    //int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    mFileName = returnCursor.getString(nameIndex);
                    //String size = Long.toString(returnCursor.getLong(sizeIndex));
                } else mFileName = returnUri.getLastPathSegment();


                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(ActDoubtDetails.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Novo anexo");
                inputName.setText(mFileName);
                builder.setView(inputName);
                builder.setNegativeButton("CANCELAR", null);
                builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(inputName.getText())) {
                            mFileName = inputName.getText().toString();
                        }
                        uploadFile(requestCode, mimeType);
                    }
                });
                builder.show();


            } else if (requestCode == ACTION_VIDEO_CAPTURE_REQUEST) {
                /*
                * Get the file's content URI from the incoming Intent,
                * then query the server app to get the file's display name
                * and size.
                * */
                returnUri = data.getData();
                Log.d(LOG_TAG, data.getData().getPath());
                final String mimeType = getContentResolver().getType(returnUri);

                final Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                /*
                * Get the column indexes of the data in the Cursor,
                * move to the first row in the Cursor, get the data,
                * and display it.
                */

                if (returnCursor != null) {
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    //int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    mFileName = returnCursor.getString(nameIndex);
                    //String size = Long.toString(returnCursor.getLong(sizeIndex));
                } else mFileName = returnUri.getLastPathSegment();

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ActDoubtDetails.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Novo video");
                inputName.setText(mFileName);
                builder.setView(inputName);
                builder.setNegativeButton("CANCELAR", null);
                builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(inputName.getText())) {
                            mFileName = inputName.getText().toString();
                        }
                        uploadFile(requestCode, mimeType);
                    }
                });
                builder.show();

            }
        }
    }


    private void uploadFile(final int requestCode, String mimeType) {

        if (requestCode == ACTION_GET_CONTENT_REQUEST || requestCode == ACTION_VIDEO_CAPTURE_REQUEST) {
            mTempFile = FileUtils.createTempFile(returnUri, mFileName, getApplicationContext(), getContentResolver());
        }

        if (mTempFile != null) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            // Prepare the HTTP request
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), mTempFile);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", mTempFile.getName(), requestFile);

            Call<Void> call = service.postFileContribution(CookieUtil.getCookie(getApplicationContext()),
                    String.valueOf(mPresentation.getInstruction_id()),
                    String.valueOf(mPresentation.getId()),
                    String.valueOf(mDoubt.getId()),
                    body);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Requisição não completada, tente novamente! ", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Enviado!", Toast.LENGTH_LONG).show();
                    /*
                    if (requestCode == RECORD_SOUND_ACTION_REQUEST) {
                        mAudioDoubtDetailFragment.notifyDataSetChanged();
                    } else if (requestCode == ACTION_GET_CONTENT_REQUEST) {
                        mMaterialDoubtDetailFragment.notifyDataSetChanged();
                    } else if (requestCode == ACTION_VIDEO_CAPTURE_REQUEST) {
                        mVideoDoubtDetailFragment.notifyDataSetChanged();
                    }*/
                    getContentDoubt();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Falha na requisição ao servidor!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "não foi possível gerar o arquivo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
