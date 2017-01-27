package br.com.sienaidea.oddin.view;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.MaterialPresentationFragment;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Material;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Profile;
import br.com.sienaidea.oddin.retrofitModel.ResponseConfirmMaterial;
import br.com.sienaidea.oddin.retrofitModel.ResponseCredentialsMaterial;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.DetectConnection;
import br.com.sienaidea.oddin.util.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static br.com.sienaidea.oddin.R.string.toast_fails_to_start;

public class PresentationDetailsActivity extends AppCompatActivity {
    private static int REQUEST_CODE_MATERIAL = 7812;

    private static final int REQUEST_PERMISSIONS_UPLOAD = 21;
    private static final int REQUEST_PERMISSIONS_DOWNLOAD = 12;

    private Material mMaterial = new Material();

    private List<Material> mList = new ArrayList<>();
    private Presentation mPresentation;

    private File mTempFile;
    private String mFileName;
    private Uri returnUri;
    private byte[] mBytes;
    private ResponseCredentialsMaterial mCredentialsMaterial;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MaterialPresentationFragment mMaterialPresentationFragment;

    //new
    private FloatingActionButton mFab;
    private Profile mProfile = new Profile();
    private View mRootLayout;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation_details);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mRootLayout = findViewById(R.id.root_lecture_detail);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Preference preference = new Preference();
        mProfile.setProfile(preference.getUserProfile(getApplicationContext()));

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList(Material.TAG);
            mPresentation = savedInstanceState.getParcelable(Presentation.TAG);
            setupFab();
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Presentation.TAG) != null && getIntent().getParcelableExtra(Instruction.TAG) != null) {
                mPresentation = getIntent().getParcelableExtra(Presentation.TAG);
                setupFab();
                getMaterials();
            } else {
                Toast.makeText(this, toast_fails_to_start, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_presentation_details);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle(mPresentation.getSubject());

    }

    private void setupFab() {
        if (mProfile.getProfile() == Constants.INSTRUCTOR) {
            mFab.setVisibility(View.VISIBLE);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //se uma das duas permissões não estiverem liberadas
                    if (ContextCompat.checkSelfPermission(PresentationDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(PresentationDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        //verifica se já foi recusado a permissão de escrita
                        if (ActivityCompat.shouldShowRequestPermissionRationale(PresentationDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            callDialog("É preciso a permission WRITE_EXTERNAL_STORAGE para SALVAR o conteudo em seu aparelho.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_UPLOAD);
                            return;
                        }

                        //verifica se já foi recusado a permissão de leitura
                        if (ActivityCompat.shouldShowRequestPermissionRationale(PresentationDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            callDialog("É preciso a permission READ_EXTERNAL_STORAGE para LER o conteudo em seu aparelho.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_UPLOAD);
                            return;
                        }

                        //caso nenhuma das duas permissões nunca estiverem sido negadas, será solicitado aqui
                        ActivityCompat.requestPermissions(PresentationDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_UPLOAD);

                    } else {
                        openFileManager();
                    }
                }
            });
        }
    }

    private void openFileManager() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.file_manager_info)),
                    REQUEST_CODE_MATERIAL);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void callDialog(String message, final String[] permissions, final int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(PresentationDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(PresentationDetailsActivity.this, permissions, requestCode);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i("TAG", "onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_PERMISSIONS_DOWNLOAD:
                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //getMaterialContent(mPositionFragment, mMaterialFragment);
                        return;
                    } else if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //getMaterialContent(mPositionFragment, mMaterialFragment);
                        return;
                    }

                }
                break;
            case REQUEST_PERMISSIONS_UPLOAD:
                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        openFileManager();
                        return;
                    } else if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        openFileManager();
                        return;
                    }

                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_MATERIAL) {
                /*
                * Get the file's content URI from the incoming Intent,
                * then query the server app to get the file's display name
                * and size.
                */
                returnUri = result.getData();
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(returnUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    mBytes = FileUtils.readBytes(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                attemptUploadMateril();
            }
        }
    }

    private void attemptUploadMateril() {

        final EditText inputName = new EditText(PresentationDetailsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        inputName.setLayoutParams(lp);

        inputName.setText(FileUtils.getFileName(getApplicationContext(), returnUri));

        AlertDialog.Builder builder = new AlertDialog.Builder(PresentationDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setView(inputName);
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.setPositiveButton(R.string.dialog_send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(inputName.getText())) {
                    mFileName = inputName.getText().toString();
                }
                getCredentials();
            }
        });
        builder.show();
    }

    private void getCredentials() {
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

            Call<ResponseCredentialsMaterial> request = service.createPresentationMaterial(auth_token_string, mPresentation.getId());

            request.enqueue(new Callback<ResponseCredentialsMaterial>() {
                @Override
                public void onResponse(Call<ResponseCredentialsMaterial> call, Response<ResponseCredentialsMaterial> response) {
                    if (response.isSuccessful()) {
                        mCredentialsMaterial = response.body();
                        uploadFile();
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseCredentialsMaterial> call, Throwable t) {
                    onRequestFailure(500);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCredentials();
                        }
                    }).show();
        }
    }

    private void uploadFile() {
        mTempFile = createTempFile();

        if (mTempFile != null) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(mCredentialsMaterial.getUrl())
                    .build();

            // Service setup
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            // Prepare the HTTP request
            RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.getMimeType(getApplicationContext(), returnUri)), mTempFile);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", mTempFile.getName(), requestFile);

            mMaterial.setName(mTempFile.getName());
            mMaterial.setId(mCredentialsMaterial.getId());
            mMaterial.setMime(FileUtils.getMimeType(getApplicationContext(), returnUri));

            // add another part within the multipart request (credenciais para upload Amazon)
            RequestBody key = RequestBody.create(MediaType.parse(Constants.MULTPART_FORM_DATA), mCredentialsMaterial.getFields().getKey());
            RequestBody policy = RequestBody.create(MediaType.parse(Constants.MULTPART_FORM_DATA), mCredentialsMaterial.getFields().getPolicy());
            RequestBody x_amz_credential = RequestBody.create(MediaType.parse(Constants.MULTPART_FORM_DATA), mCredentialsMaterial.getFields().getX_amz_credential());
            RequestBody x_amz_algorithm = RequestBody.create(MediaType.parse(Constants.MULTPART_FORM_DATA), mCredentialsMaterial.getFields().getX_amz_algorithm());
            RequestBody x_amz_date = RequestBody.create(MediaType.parse(Constants.MULTPART_FORM_DATA), mCredentialsMaterial.getFields().getX_amz_date());
            RequestBody x_amz_signature = RequestBody.create(MediaType.parse(Constants.MULTPART_FORM_DATA), mCredentialsMaterial.getFields().getX_amz_signature());

            Call<Void> call = service.sendMaterial(key, policy, x_amz_credential, x_amz_algorithm, x_amz_date, x_amz_signature, body);

            // Asynchronously execute HTTP request
            call.enqueue(new Callback<Void>() {
                /**
                 * onResponse is called when any kind of response has been received.
                 */
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    // http response status code + headers
                    //System.out.println("Response status code: " + response.code());

                    // isSuccess is true if response code => 200 and <= 300
                    if (!response.isSuccessful()) {
                        onRequestFailure(response.code());
                        return;
                    }
                    confirmUpload();
                }

                /**
                 * onFailure gets called when the HTTP request didn't get through.
                 * For instance if the URL is invalid / host not reachable
                 */
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    onRequestFailure(500);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "não foi possivel gerar o arquivo temporário", Toast.LENGTH_LONG).show();
        }
    }

    private File createTempFile() {

        File root = Environment.getExternalStorageDirectory();

        File dirOddin = new File(root.getAbsolutePath() + "/Oddin");
        if (!dirOddin.exists()) {
            dirOddin.mkdir();
        }

        File dirDiscipline = new File(dirOddin.getAbsolutePath() + "/temporarios");
        if (!dirDiscipline.exists()) {
            dirDiscipline.mkdir();
        }

        final File file = new File(dirDiscipline.getAbsolutePath(), mFileName);
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(mBytes);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }

    private void confirmUpload() {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpApi.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

        Preference preference = new Preference();
        final String auth_token_string = preference.getToken(getApplicationContext());

        Call<ResponseConfirmMaterial> call = service.confirmMaterial(auth_token_string, mMaterial.getId(), mMaterial);

        // Asynchronously execute HTTP request
        call.enqueue(new Callback<ResponseConfirmMaterial>() {
            /**
             * onResponse is called when any kind of response has been received.
             */
            @Override
            public void onResponse(Call<ResponseConfirmMaterial> call, Response<ResponseConfirmMaterial> response) {
                if (response.isSuccessful()) {
                    mMaterial.setUrl(response.body().getUrl());
                    mMaterialPresentationFragment.addItemPosition(0, mMaterial);
                }
            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Call<ResponseConfirmMaterial> call, Throwable t) {
            }
        });
    }

    public void getMaterials() {
        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Preference preference = new Preference();
            String auth_token_string = preference.getToken(getApplicationContext());

            Call<List<Material>> request = service.PresentationMaterials(auth_token_string, mPresentation.getId());

            request.enqueue(new Callback<List<Material>>() {
                @Override
                public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                    if (response.isSuccessful()) {
                        mList.clear();
                        mList = response.body();
                        onRequestSuccess();
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Material>> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snake_try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMaterials();
                        }
                    }).show();
        }
    }

    public void deleteMaterial(final int position, Material material) {
        DetectConnection detectConnection = new DetectConnection(getApplicationContext());
        if (detectConnection.existConnection()) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Preference preference = new Preference();
            String auth_token_string = preference.getToken(getApplicationContext());

            Call<Void> request = service.deleteMaterial(auth_token_string, material.getId());
            request.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        mMaterialPresentationFragment.removeItem(position);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                }
            });
        }
    }

    public void getMaterial(final Material material) {
        DetectConnection detectConnection = new DetectConnection(this);
        if (detectConnection.existConnection()) {
            // Retrofit setup
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpApi.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Service setup
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            Preference preference = new Preference();
            String auth_token_string = preference.getToken(getApplicationContext());

            Call<ResponseConfirmMaterial> request = service.getMaterial(auth_token_string, material.getId());

            request.enqueue(new Callback<ResponseConfirmMaterial>() {
                @Override
                public void onResponse(Call<ResponseConfirmMaterial> call, Response<ResponseConfirmMaterial> response) {
                    if (response.isSuccessful()) {
                        startDownload(Uri.parse(response.body().getUrl()), material);
                    } else {
                        onRequestFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseConfirmMaterial> call, Throwable t) {
                    onRequestFailure(401);
                }
            });

        } else {
            Snackbar.make(mRootLayout, R.string.snake_no_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    private void startDownload(Uri uri, Material material) {
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(material.getName());
        request.setMimeType(material.getMime());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Long DownloadReference = downloadmanager.enqueue(request);
    }

    private void onRequestSuccess() {
        mMaterialPresentationFragment = (MaterialPresentationFragment) fragmentManager.findFragmentByTag(mMaterialPresentationFragment.TAG);
        if (mMaterialPresentationFragment != null) {
            mMaterialPresentationFragment.notifyDataSetChanged();
        } else {
            mMaterialPresentationFragment = mMaterialPresentationFragment.newInstance(getListMaterial());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_presentation_details, mMaterialPresentationFragment, mMaterialPresentationFragment.TAG);
            fragmentTransaction.commit();
        }
        mProgressBar.setVisibility(GONE);
    }

    private void onRequestFailure(int statusCode) {
        if (statusCode == 401) {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            finish();
        } else {
        }
    }

    public List<Material> getListMaterial() {
        return mList;
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
        outState.putParcelableArrayList(Material.TAG, (ArrayList<Material>) mList);
        outState.putParcelable(Presentation.TAG, mPresentation);
        super.onSaveInstanceState(outState);
    }
}
