package br.com.sienaidea.oddin.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.sienaidea.oddin.R;

public class FileUtils {

    public static byte[] readBytes(final InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("DEBUG", "entrou na thread bytes");

                // this is storage overwritten on each iteration with bytes
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                // we need to know how may bytes were read to write them to the byteBuffer
                int len = 0;
                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        byteBuffer.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).run();

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static byte[] readBytes(Uri uri) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(String.valueOf(uri)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] buf = new byte[1024];
        int n;
        try {
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] videoBytes = baos.toByteArray(); //this is the video in bytes.
        return videoBytes;
    }

    public static File createTempFile(Uri returnUri, String fileName, Context context, ContentResolver contentResolver) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(returnUri);
            byte[] bytes = FileUtils.readBytes(inputStream);

            File tempFile = new File(context.getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);
            return tempFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void saveFile(byte[] bytes, String fileName, String mimeType, final Context context) {

        if (isExternalStorageWritable()) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Oddin");

            if (!dir.exists()) {
                dir.mkdir();
            }

            final File file = new File(dir, fileName);
            try {

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setMessage(fileName + mimeType + " Salvo em: " + file.getAbsolutePath());
                builder.setPositiveButton("OK", null);
                builder.setNegativeButton("ABRIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent newIntent = new Intent();
                        newIntent.setDataAndType(Uri.parse("file://" + file.getPath()), "application/pdf");
                        newIntent.setAction(Intent.ACTION_VIEW);
                        try {
                            context.startActivity(newIntent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Não foi possível salvar o arquivo.", Toast.LENGTH_LONG).show();
        }
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static File getFileFromPath(String path) {
        return getFileFromPath(path, null);
    }

    public static File getFileFromPath(String path, String name){
        final File file;

        if (name == null){
            file = new File(path);
        }else {
            file = new File(path, name);
        }

        if (file.exists()) {
            FileInputStream is = null;
            try {

                is = new FileInputStream(file);
                byte[] bytes = readBytes(is);

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();

                return file;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static File getFileFromUri(Uri returnUri, String fileName, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(returnUri);
            byte[] bytes = FileUtils.readBytes(inputStream);

            File tempFile = new File(Environment.getExternalStorageDirectory(), fileName);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);
            return tempFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
