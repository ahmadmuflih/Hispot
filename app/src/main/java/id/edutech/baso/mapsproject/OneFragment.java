
package id.edutech.baso.mapsproject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import id.edutech.baso.mapsproject.R;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.ArrayList;

import models.APIService;
import models.Post;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class OneFragment extends Fragment{
    RecyclerView recyclerView;
    View view;
    ArrayList<Post> posts;
    private static final String TAG = SimpleVrPanoramaActivity.class.getSimpleName();
    private VrPanoramaView panoWidgetView;
    private String locationVR;
    private Uri fileUri;
    public boolean loadImageSuccessful;
    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
    private ImageLoaderTask backgroundImageLoaderTask;
    Call<ArrayList<Post>> postCall;
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.popular_review);
        panoWidgetView = (VrPanoramaView) view.findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());

        // Initial launch of the app or an Activity recreation due to rotation.

        String locationId = ((LocationActivity)getActivity()).getLocationId();
        String api_key=Preferences.getStringPreferences("api_key", getActivity());
        Call<Validation> getVRCall = APIService.service.getvr(locationId,api_key);
        getVRCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                if(response.isSuccessful()){
                    locationVR = response.body().getStatus();
                    handleIntent(getActivity().getIntent());
                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {

            }
        });
        postCall = APIService.service.getLocationPost("popular",locationId,api_key);
        postCall.enqueue(new Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                if(response.isSuccessful()) {
                    posts = response.body();
                    PopularReviewAdapter adapter = new PopularReviewAdapter(getActivity().getApplicationContext(), posts);
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.popular_review);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setFocusable(false);
                    adapter.setOnPostSelectedListener(new PopularReviewAdapter.OnPostSelectedListener() {
                        @Override
                        public void onSelected(Post post) {
                            Intent intent = new Intent(getActivity(), PhotoActivity.class);
                            intent.putExtra("post_id", post.getIdPost());
                            intent.putExtra("url_gambar", post.getUrlGambar());       // Nanti diganti dengan getUrlGambar
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
                }
                else{
                    Toast.makeText(getActivity(), "Failed to load posts, please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable t) {
                Toast.makeText(getActivity(), "Ewh!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
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
        if(locationVR.equals("failed")){
            Toast.makeText(getActivity(), "There is no VR available for this place", Toast.LENGTH_SHORT).show();
        }
        else {
            backgroundImageLoaderTask = new ImageLoaderTask();
            backgroundImageLoaderTask.execute(Pair.create(fileUri, panoOptions));
        }
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
            ImageDownloader imageDownloader = new ImageDownloader(locationVR);
            imageDownloader.setDownloadImageListener(new ImageDownloader.DownloadImageListener() {
                @Override
                public void onFinish(Bitmap bitmap) {
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
