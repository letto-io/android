package br.com.sienaidea.oddin.DAO;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import br.com.sienaidea.oddin.retrofitModel.Instruction;
import br.com.sienaidea.oddin.server.HttpApi;
import br.com.sienaidea.oddin.util.CookieUtil;
import br.com.sienaidea.oddin.util.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Siena Idea on 03/08/2016.
 */
public class MaterialDAO {
    private Context mContext;
    private Instruction mInstruction;
    private Uri mUri;
    private String mFileName;

    public MaterialDAO(Context context, Instruction instruction, Uri uri, String fileName) {
        this.mContext = context;
        this.mInstruction = instruction;
        this.mUri = uri;
        this.mFileName = fileName;
    }

    public void createInstructionMaterial() {
        File file = FileUtils.createTempFile(mUri, mFileName, mContext);
        HttpApi.newMaterial(mContext, mInstruction, file, FileUtils.getMimeType(mContext, mUri));
    }
}
