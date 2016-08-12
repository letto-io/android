package br.com.sienaidea.oddin.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.sienaidea.oddin.R;
import br.com.sienaidea.oddin.fragment.MaterialDisciplineFragment;
import br.com.sienaidea.oddin.model.Constants;
import br.com.sienaidea.oddin.retrofitModel.Material;
import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.retrofitModel.Presentation;
import br.com.sienaidea.oddin.retrofitModel.Profile;
import br.com.sienaidea.oddin.retrofitModel.ResponseConfirmMaterial;
import br.com.sienaidea.oddin.retrofitModel.ResponseCredentialsMaterial;
import br.com.sienaidea.oddin.server.BossClient;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.server.Preference;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.DetectConnection;
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

import static br.com.sienaidea.oddin.R.string.toast_fails_to_start;

public class LectureDetailsActivity extends AppCompatActivity {
    private static String URL_GET_MATERIAL;
    private static int REQUEST_CODE_MATERIAL = 2;
    private static final int REQUEST_PERMISSIONS_UPLOAD = 21;
    private static final int REQUEST_PERMISSIONS_DOWNLOAD = 12;

    private List<Material> mList = new ArrayList<>();
    //private Discipline mDiscipline;

    private File mTempFile;
    private byte[] mBytes;
    private String mFileName, mimeType;
    private Uri returnUri;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MaterialDisciplineFragment mMaterialDisciplineFragment;

    private int mPositionFragment;
    private Material mMaterialFragment;

    //new
    private FloatingActionButton mFab;
    private Instruction mInstruction;
    private Profile mProfile;
    private View mRootLayout;
    private ResponseCredentialsMaterial mCredentialsMaterial;
    private Material mMaterial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mRootLayout = findViewById(R.id.root_lecture_detail);

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList("mList");
            mInstruction = savedInstanceState.getParcelable(Instruction.TAG);
            mProfile = savedInstanceState.getParcelable(Profile.TAG);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getParcelableExtra(Instruction.TAG) != null) {
                mInstruction = getIntent().getParcelableExtra(Instruction.TAG);
                mProfile = getIntent().getParcelableExtra(Profile.TAG);
                setupFab();
                getMaterials();
            } else {
                Toast.makeText(this, toast_fails_to_start, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_discipline_details);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle(mInstruction.getLecture().getName());
    }

    private void setupFab() {
        if (mProfile.getProfile() == Constants.INSTRUCTOR) {
            mFab.setVisibility(View.VISIBLE);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //se uma das duas permissões não estiverem liberadas
                    if (ContextCompat.checkSelfPermission(LectureDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(LectureDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        //verifica se já foi recusado a permissão de escrita
                        if (ActivityCompat.shouldShowRequestPermissionRationale(LectureDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            callDialog("É preciso a permission WRITE_EXTERNAL_STORAGE para SALVAR o conteudo em seu aparelho.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_UPLOAD);
                            return;
                        }

                        //verifica se já foi recusado a permissão de leitura
                        if (ActivityCompat.shouldShowRequestPermissionRationale(LectureDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            callDialog("É preciso a permission READ_EXTERNAL_STORAGE para LER o conteudo em seu aparelho.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_UPLOAD);
                            return;
                        }

                        //caso nenhuma das duas permissões nunca estiverem sido negadas, será solicitado aqui
                        ActivityCompat.requestPermissions(LectureDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_UPLOAD);

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
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_MATERIAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                    Log.d("DEBUG", "proxima linha após a Thread");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                attemptUploadMateril();
            }
        }
    }

    private boolean checkUriPermissionGranted(Uri uri) {
        if ((checkUriPermission(uri, 0, 0, Intent.FLAG_GRANT_READ_URI_PERMISSION) == PackageManager.PERMISSION_GRANTED) &&
                (checkUriPermission(uri, 0, 0, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) == PackageManager.PERMISSION_GRANTED)) {

            attemptUploadMateril();
            return true;

        } else
            return false;
    }

    private void attemptUploadMateril() {

        final EditText inputName = new EditText(LectureDetailsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        inputName.setLayoutParams(lp);

        inputName.setText(FileUtils.getFileName(getApplicationContext(), returnUri));

        AlertDialog.Builder builder = new AlertDialog.Builder(LectureDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Novo Material");
        builder.setView(inputName);
        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
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

            Call<ResponseCredentialsMaterial> request = service.createInstructionMaterial(auth_token_string, mInstruction.getId());

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
                    onRequestFailure(401);
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
            //.addConverterFactory(GsonConverterFactory.create())

            // Service setup
            HttpApi.HttpBinService service = retrofit.create(HttpApi.HttpBinService.class);

            // Prepare the HTTP request
            RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.getMimeType(getApplicationContext(), returnUri)), mTempFile);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", mTempFile.getName(), requestFile);


            mMaterial.setName(mTempFile.getName());
            mMaterial.setId(mCredentialsMaterial.getId());
            mMaterial.setMime(FileUtils.getMimeType(getApplicationContext(), returnUri));

            // add another part within the multipart request
            String keyString = "8a5d46f9-d83d-4acb-8ea0-f7f70b013ec6/${filename}";
            String policyString = "eyJleHBpcmF0aW9uIjoiMjAxNi0wOC0xMlQxNDo1NzowOVoiLCJjb25kaXRpb25zIjpbeyJidWNrZXQiOiJvc2puZXhmZXZsZmx2aCJ9LFsic3RhcnRzLXdpdGgiLCIka2V5IiwiOGE1ZDQ2ZjktZDgzZC00YWNiLThlYTAtZjdmNzBiMDEzZWM2LyJdLHsieC1hbXotY3JlZGVudGlhbCI6IkFLSUFJNFVUTEE1Q1BSSk9PRzRRLzIwMTYwODEyL3VzLXdlc3QtMi9zMy9hd3M0X3JlcXVlc3QifSx7IngtYW16LWFsZ29yaXRobSI6IkFXUzQtSE1BQy1TSEEyNTYifSx7IngtYW16LWRhdGUiOiIyMDE2MDgxMlQxMzU3MDlaIn1dfQ==";
            String x_amz_credentialString = "AKIAI4UTLA5CPRJOOG4Q/20160812/us-west-2/s3/aws4_request";
            String x_amz_algorithmString = "AWS4-HMAC-SHA256";
            String x_amz_dateString = "20160812T135709Z";
            String x_amz_signatureString = "2e7788fb2e650dc10fdb7eedffe28c6058e5acfdeda3362139f44f8f677a914a";

            RequestBody key = RequestBody.create(MediaType.parse("multipart/form-data"), keyString);
            RequestBody policy = RequestBody.create(MediaType.parse("multipart/form-data"), policyString);
            RequestBody x_amz_credential = RequestBody.create(MediaType.parse("multipart/form-data"), x_amz_credentialString);
            RequestBody x_amz_algorithm = RequestBody.create(MediaType.parse("multipart/form-data"), x_amz_algorithmString);
            RequestBody x_amz_date = RequestBody.create(MediaType.parse("multipart/form-data"), x_amz_dateString);
            RequestBody x_amz_signature = RequestBody.create(MediaType.parse("multipart/form-data"), x_amz_signatureString);

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
                        // print response body if unsuccessful
                        Toast.makeText(getApplicationContext(), "Não foi possível completar a requisição (Amazon) no servidor: Cód:" + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Enviado!", Toast.LENGTH_LONG).show();
                    confirmUpload();
                }

                /**
                 * onFailure gets called when the HTTP request didn't get through.
                 * For instance if the URL is invalid / host not reachable
                 */
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Falha na requisição à Amazon!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "não foi possivel gerar o arquivo temporário", Toast.LENGTH_LONG).show();
        }
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

                }
            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Call<ResponseConfirmMaterial> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falha na requisição à Amazon!", Toast.LENGTH_LONG).show();
            }
        });
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

        /*
        try {

            File tempFile = new File(getCacheDir(), mFileName);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(mBytes);
            return tempFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        */


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

            Call<List<Material>> request = service.InstructionMaterials(auth_token_string, mInstruction.getId());

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

    private void onRequestSuccess() {
        mMaterialDisciplineFragment = (MaterialDisciplineFragment) fragmentManager.findFragmentByTag(MaterialDisciplineFragment.TAG);
        if (mMaterialDisciplineFragment != null) {
            mMaterialDisciplineFragment.notifyDataSetChanged();
        } else {
            mMaterialDisciplineFragment = MaterialDisciplineFragment.newInstance(getListMaterial());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.rl_fragment_discipline_details, mMaterialDisciplineFragment, MaterialDisciplineFragment.TAG);
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

    public void attemptGetMaterialContent(int position, Material material) {

        mPositionFragment = position;
        mMaterialFragment = material;

        //se uma das duas permissões não estiverem liberadas
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //verifica se já foi recusado a permissão de escrita
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                callDialog("É preciso permissão para SALVAR o conteudo em seu aparelho.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_DOWNLOAD);
                return;
            }

            //verifica se já foi recusado a permissão de leitura
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                callDialog("É preciso permissão para LER o conteudo em seu aparelho.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_DOWNLOAD);
                return;
            }

            //caso nenhuma das duas permissões nunca estiverem sido negadas, será solicitado aqui
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_DOWNLOAD);

        } else {
            //e por fim, caso já tenha permiçoes, faça download
            //getMaterialContent(mPositionFragment, mMaterialFragment);
        }

    }

//    private void getMaterialContent(final int position, final Material material) {
//        BossClient.get(URL_GET_MATERIAL + "/" + material.getId(), null, CookieUtil.getCookie(getApplicationContext()), new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                if (FileUtils.isExternalStorageWritable()) {
//                    File root = Environment.getExternalStorageDirectory();
//
//                    File dirOddin = new File(root.getAbsolutePath() + "/Oddin");
//                    if (!dirOddin.exists()) {
//                        dirOddin.mkdir();
//                    }
//
//                    File dirDiscipline = new File(dirOddin.getAbsolutePath() + "/" + mInstruction.getLecture().getName().trim());
//                    if (!dirDiscipline.exists()) {
//                        dirDiscipline.mkdir();
//                    }
//
//                    final File file = new File(dirDiscipline.getAbsolutePath(), material.getName().trim());
//                    try {
//
//                        FileOutputStream fileOutputStream = new FileOutputStream(file);
//                        fileOutputStream.write(responseBody);
//                        fileOutputStream.close();
//
//                        AlertDialog.Builder builder =
//                                new AlertDialog.Builder(LectureDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
//                        builder.setMessage("Material salvo em: " + file.getAbsolutePath());
//                        builder.setPositiveButton("OK", null);
//                        builder.setNegativeButton("ABRIR", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent newIntent = new Intent();
//                                newIntent.setDataAndType(Uri.parse("file://" + file.getPath()), material.getMime());
//                                newIntent.setAction(Intent.ACTION_VIEW);
//                                try {
//                                    startActivity(newIntent);
//                                } catch (android.content.ActivityNotFoundException e) {
//                                    Toast.makeText(getApplicationContext(), "Nenhum manipulador para este tipo de arquivo.", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                        builder.show();
//
//                        //material.setDownloaded(true);
//                        mMaterialDisciplineFragment.downloadFinished(position, Uri.parse("file://" + file.getPath()));
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Não foi possível salvar o arquivo.", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Toast.makeText(getApplicationContext(), "Não foi possível fazer a requisição no servidor, tente novamente.", Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }

    private void callDialog(String message, final String[] permissions, final int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LectureDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(LectureDetailsActivity.this, permissions, requestCode);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.show();

    }

    private void addListItem(Material material) {
        mList.add(material);
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
        outState.putParcelableArrayList("mList", (ArrayList<Material>) mList);
        outState.putParcelable(Instruction.TAG, mInstruction);
        super.onSaveInstanceState(outState);
    }
}
