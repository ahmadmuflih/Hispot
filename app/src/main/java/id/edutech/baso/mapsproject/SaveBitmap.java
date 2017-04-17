package id.edutech.baso.mapsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Baso on 10/17/2016.
 */
public class SaveBitmap {
    private static final String TAG = "Save Bitmap";
    Bitmap bitmap;
    String namaFile;
    Context context;
    public SaveBitmap(Bitmap bitmap, String namaFile, Context context){
        this.bitmap = bitmap;
        this.namaFile = namaFile;
        this.context=context;
    }
    public void commit(){
        if(bitmap != null) {
            write(namaFile, bitmap);
        }
    }
    public static void saveBitmapToExternal(final String filename, final Bitmap bitmap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File path = new File(dir,"/Hispot");
                if(!path.exists())
                    path.mkdirs();
                try {
                    File file = new File(path, filename);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    Log.d("save","tersimpan");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("save","gagal");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("save","gagal");
                }
            }
        }).start();
    }
    private void write(final String filename, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(context.getFilesDir().getAbsolutePath(), filename);
                try {
                    if(!file.exists())
                        file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    Log.d(TAG, "Usable space "+file.getUsableSpace());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
