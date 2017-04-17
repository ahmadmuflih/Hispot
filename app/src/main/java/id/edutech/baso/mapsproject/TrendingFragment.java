package id.edutech.baso.mapsproject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import models.APIService;
import models.Location;
import models.Post;
import models.PostResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class TrendingFragment extends Fragment{
    View view;
    ImageView imageVR, imageProfil;
    ProgressBar progressVR;
    private ImageAdapter imageAdapter;
    TextView txtNama;
    String locationVR;
    int locationID;
    //

    private static final String TAG = SimpleVrPanoramaActivity.class.getSimpleName();
    private VrPanoramaView panoWidgetView;

    public boolean loadImageSuccessful;
    private Uri fileUri;
    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
    private ImageLoaderTask backgroundImageLoaderTask;
    public TrendingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTrending();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_trending, container, false);
        imageProfil=(ImageView) view.findViewById(R.id.imageProfil);
        //imageVR=(ImageView) view.findViewById(R.id.vr_image);
        txtNama = (TextView)view.findViewById(R.id.location_name);
        progressVR=(ProgressBar)view.findViewById(R.id.progress_vr);



        panoWidgetView = (VrPanoramaView) view.findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());

        // Initial launch of the app or an Activity recreation due to rotation.
        return view;
    }

    public void initTrending(){
        final Realm realm = Realm.getDefaultInstance();
        String api_key = Preferences.getStringPreferences("api_key",getActivity());

        /*---- Offline Mode */

        RealmResults<Post> capture = realm.where(Post.class).equalTo("statusTrending","1").findAll();
        /*
        RealmList<Post> results = new RealmList<>();
        results.addAll(capture.subList(0, capture.size()));
        */

        final ArrayList<Post> posts = new ArrayList<>(capture);
        Log.d("POST", "read : "+posts.size());
        imageAdapter = new ImageAdapter(getActivity().getApplicationContext(),posts,1);
        imageAdapter.setOnPostSelectedListener(new ImageAdapter.OnPostSelectedListener() {
            @Override
            public void onSelected(Post post) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra("post_id", post.getIdPost());
                intent.putExtra("url_gambar", post.getUrlGambar());
                intent.putExtra("waktu",post.getWaktu());
                intent.putExtra("review",post.getReview());
                intent.putExtra("location_id",post.getLocationID());
                intent.putExtra("location_name", post.getLocationName());
                intent.putExtra("id_user",post.getIdUser());
                intent.putExtra("nama_user",post.getNamaUser());
                intent.putExtra("rating", post.getRating());
                intent.putExtra("jumlahLike",post.getJumlahLike());
                intent.putExtra("foto_user",post.getFoto_user());
                startActivity(intent);
            }
        });
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setFocusable(false);



        //

        Call<PostResults> postResultsCall = APIService.service.getTrending("","",api_key);
        postResultsCall.enqueue(new Callback<PostResults>() {
            @Override
            public void onResponse(Call<PostResults> call, Response<PostResults> response) {
                if(response.isSuccessful()){

                    /*      Update Status Trending */

                    realm.beginTransaction();
                    for(Post post: posts){
                        post.setStatusTrending("");
                        realm.copyToRealmOrUpdate(post);

                    }
                    realm.commitTransaction();

                    //

                    ArrayList<Post>newPosts = response.body().getPosts();

                    for(Post post : newPosts){
                        post.setStatusTrending("1");
                    }

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(newPosts);
                    realm.commitTransaction();

                    RealmResults<Post> results = realm.where(Post.class).findAll();
                    Log.d("Post", "Saved to realm. size : "+results.size());

                    Location location = response.body().getLocation();

                    locationVR = location.getVr();
                    locationID = location.getLocationId();
                    Preferences.setIntPreferences("vrtrendingid",locationID,getActivity());
                    Preferences.setStringPreferences("vrtrendingurl",locationVR,getActivity());
                    handleIntent(getActivity().getIntent());
                    txtNama.setText(location.getLocationName());

                    imageAdapter.setPosts(newPosts);
                    recyclerView.setAdapter(imageAdapter);
                }
                else{
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResults> call, Throwable t) {
                //Toast.makeText(getActivity(), "Oops!", Toast.LENGTH_SHORT).show();
                locationVR = Preferences.getStringPreferences("vrtrendingurl",getActivity());
                locationID = Preferences.getIntPreferences("vrtrendingid",getActivity());
                handleIntent(getActivity().getIntent());
            }
        });

    }



    protected void onNewIntent(Intent intent) {
        Log.i(TAG, this.hashCode() + ".onNewIntent()");
        // Save the intent. This allows the getIntent() call in onCreate() to use this new Intent during
        // future invocations.
        getActivity().setIntent(intent);
        // Load the new image.
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        // Determine if the Intent contains a file to load.
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Log.i(TAG, "ACTION_VIEW Intent recieved");

            fileUri = intent.getData();
            if (fileUri == null) {
                Log.w(TAG, "No data uri specified. Use \"-d /path/filename\".");
            } else {
                Log.i(TAG, "Using file " + fileUri.toString());
            }

            panoOptions.inputType = intent.getIntExtra("inputType", VrPanoramaView.Options.TYPE_MONO);
            Log.i(TAG, "Options.inputType = " + panoOptions.inputType);
        } else {
            Log.i(TAG, "Intent is not ACTION_VIEW. Using default pano image.");
            fileUri = null;
            panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
        }

        // Load the bitmap in a background thread to avoid blocking the UI thread. This operation can
        // take 100s of milliseconds.
        if (backgroundImageLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundImageLoaderTask.cancel(true);
        }
        backgroundImageLoaderTask = new ImageLoaderTask();
        backgroundImageLoaderTask.execute(Pair.create(fileUri, panoOptions));
    }

    @Override
    public void onPause() {
        panoWidgetView.pauseRendering();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        panoWidgetView.resumeRendering();
    }

    @Override
    public void onDestroy() {
        // Destroy the widget and free memory.
        panoWidgetView.shutdown();

        // The background task has a 5 second timeout so it can potentially stay alive for 5 seconds
        // after the activity is destroyed unless it is explicitly cancelled.
        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
        super.onDestroy();
    }
    class ImageLoaderTask extends AsyncTask<Pair<Uri, VrPanoramaView.Options>, Void, Boolean> {

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(Pair<Uri, VrPanoramaView.Options>... fileInformation) {
            final VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
            panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;  // It's safe to use null VrPanoramaView.Options.
            final String simpanFoto ="vr"+locationID+".png";
            Bitmap fotoS = ImageUtil.getImage(simpanFoto, getActivity());
            if(fotoS==null) {
                ImageDownloader imageDownloader = new ImageDownloader(locationVR);
                imageDownloader.setDownloadImageListener(new ImageDownloader.DownloadImageListener() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        SaveBitmap saveBitmap = new SaveBitmap(bitmap, simpanFoto, getActivity());
                        saveBitmap.commit();
                        panoWidgetView.loadImageFromBitmap(bitmap, panoOptions);
                    }

                    @Override
                    public void onError() {
                        //Toast.makeText(getContext(), "Gagal membuka gambar!", Toast.LENGTH_SHORT).show();

                    }
                });
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    imageDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    imageDownloader.execute();

            }
            else{
                panoWidgetView.loadImageFromBitmap(fotoS, panoOptions);
            }

            /*
            try {
                istr.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close input stream: " + e);
            }
            */

            return true;
        }
    }

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrPanoramaEventListener {
        /**
         * Called by pano widget on the UI thread when it's done loading the image.
         */
        @Override
        public void onLoadSuccess() {
            loadImageSuccessful = true;
        }

        /**
         * Called by pano widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            loadImageSuccessful = false;
            Toast.makeText(
                    getActivity(), "Error loading pano: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }
    }
}
