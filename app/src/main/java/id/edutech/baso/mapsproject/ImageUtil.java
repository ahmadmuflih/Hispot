package id.edutech.baso.mapsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Baso on 10/16/2016.
 */
public class ImageUtil {
    public static  void capture(String url, final String namaFile, final ImageView imageView, final ImageUtilListener imageUtilListener){
        ImageDownloader imageDownloader = new ImageDownloader(url);
        imageDownloader.setDownloadImageListener(new ImageDownloader.DownloadImageListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
                if(namaFile!="") {
                    SaveBitmap saveBitmap = new SaveBitmap(bitmap, namaFile, imageView.getContext());
                    saveBitmap.commit();
                }
                imageView.setImageBitmap(bitmap);
                imageUtilListener.onDisplayed();
            }

            @Override
            public void onError() {
                //Toast.makeText(imageView.getContext(), "Gagal membuka gambar!", Toast.LENGTH_SHORT).show();
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            imageDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            imageDownloader.execute();
    }
    public static  void setBackground(String url, final String namaFile, final Context context, final BackgroundUtilListener backgroundUtilListener){
        final ImageDownloader imageDownloader = new ImageDownloader(url);
        imageDownloader.setDownloadImageListener(new ImageDownloader.DownloadImageListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
                SaveBitmap saveBitmap = new SaveBitmap(bitmap,namaFile,context);
                saveBitmap.commit();
                Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
                backgroundUtilListener.onFinished(drawable);
            }

            @Override
            public void onError() {
                //Toast.makeText(context, "Gagal membuka gambar!", Toast.LENGTH_SHORT).show();
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            imageDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            imageDownloader.execute();

    }
    public static Bitmap getImage(String namaFile, Context context){
        String path = context.getFilesDir().getAbsolutePath()+"/"+namaFile;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path,options);
    }
    public static Bitmap getFullImage(String namaFile, Context context) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + namaFile;
        Bitmap b = null;
        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;

    }
    public static void deleteImage(String namaFile, Context context){
        File dir = context.getFilesDir();
        File file = new File(dir, namaFile);
        boolean deleted = file.delete();
        Log.d("Delete", String.valueOf(deleted));
    }
    public interface ImageUtilListener{
        void onDisplayed();
    }
    public interface BackgroundUtilListener{
        void onFinished(Drawable drawable);
    }
    public static void saveBitmap(Bitmap bitmap, String namaFile, ImageView imageView){
        SaveBitmap saveBitmap = new SaveBitmap(bitmap,namaFile,imageView.getContext());
        saveBitmap.commit();
    }
}
