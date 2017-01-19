package br.com.sienaidea.oddin.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    /**
     * @param context
     * @param uri
     * @return file name of the Uri
     */
    public static String getFileName(Context context, Uri uri) {
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);

        if (returnCursor == null) {
            return uri.getLastPathSegment();
        } else {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            //int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            return returnCursor.getString(nameIndex);
            //String size = Long.toString(returnCursor.getLong(sizeIndex));
        }
    }

    /**
     * @param context
     * @param uri
     * @return the mimeType of the Uri
     */
    public static String getMimeType(Context context, Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    /**
     * @param context
     * @param uri
     * @return inputstream of the Uri
     */
    public static InputStream getInputstream(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param inputStream
     * @return byteArray of the file inputStream
     * @throws IOException
     */
    public static byte[] readBytes(final InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        new Thread(new Runnable() {
            @Override
            public void run() {

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

    /**
     * Checks if external storage is available for read and write
     *
     * @return true or false
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getFileFromPath(String path) {
        return getFileFromPath(path, null);
    }

    public static File getFileFromPath(String path, String name) {
        final File file;

        if (name == null) {
            file = new File(path);
        } else {
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
}
