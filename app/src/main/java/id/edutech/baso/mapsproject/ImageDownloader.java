package id.edutech.baso.mapsproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Baso on 10/16/2016.
 */
public class ImageDownloader extends AsyncTask<Void,Void,Bitmap> {
    private String url;
    private DownloadImageListener downloadImageListener;

    public ImageDownloader(String url) {
        this.url = url;
    }

    public void setDownloadImageListener(DownloadImageListener downloadImageListener) {
        this.downloadImageListener = downloadImageListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        HttpURLConnection httpURLConnection=null;

        try {
            URL objUrl = new URL(url);
            if(URLUtil.isHttpsUrl(url))
                httpURLConnection = (HttpsURLConnection) objUrl.openConnection();
            else if(URLUtil.isHttpUrl(url))
                httpURLConnection = (HttpURLConnection) objUrl.openConnection();
            httpURLConnection.connect();
            bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap!=null && downloadImageListener!=null)
            downloadImageListener.onFinish(bitmap);
        if(bitmap==null && downloadImageListener!=null)
            downloadImageListener.onError();
    }

    public interface DownloadImageListener{
        void onFinish(Bitmap bitmap);
        void onError();
    }
}
