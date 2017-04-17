
package id.edutech.baso.mapsproject;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;

import java.io.File;
import java.io.IOException;

import models.APIService;
import models.Marker;
import models.Validation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class PostFragment extends Fragment implements View.OnClickListener {
    View rootView;
    RatingBar rating;
    TextView ratingText;
    EditText review;
    Marker selectedLocation;
    Bitmap bitmap, preview;
    ProgressDialog progressDialog;
    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_post, container, false);
        ((Button)rootView.findViewById(R.id.select_location)).setOnClickListener(this);
        ((ImageButton)rootView.findViewById(R.id.cancel_selected)).setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        rating = (RatingBar)rootView.findViewById(R.id.rating);
        ratingText = (TextView)rootView.findViewById(R.id.rating_text);
        review = (EditText)rootView.findViewById(R.id.review);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingText.setText("Given Stars : "+rating);
            }
        });
        if(bitmap==null) {
            final Uri uri = Uri.parse(Preferences.getStringPreferences("filteredPhoto", getActivity().getApplicationContext()));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        preview = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.3), (int) (bitmap.getHeight() * 0.3), true);
                        ((ImageView) rootView.findViewById(R.id.foto_post)).setImageBitmap(preview);
                    }
                }
            }, 3000);
        }
        else{
            ((ImageView) rootView.findViewById(R.id.foto_post)).setImageBitmap(preview);
        }
        selectedLocation = ((PostActivity)getActivity()).getSelectedLocation();
        setSelectedLocation(selectedLocation);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.select_location:
                //((LinearLayout)rootView.findViewById(R.id.selected_location)).setVisibility(View.VISIBLE);
                ((PostActivity)getActivity()).displaySelectedScreen(2);
                break;
            case R.id.cancel_selected:
                ((LinearLayout)rootView.findViewById(R.id.selected_location)).setVisibility(View.GONE);
                ((Button)rootView.findViewById(R.id.select_location)).setVisibility(View.VISIBLE);
                ((PostActivity)getActivity()).setSelectedLocation(null);
                selectedLocation=null;
                break;

        }
    }
    public void setSelectedLocation(Marker location){
        if(location==null) {
            rootView.findViewById(R.id.selected_location).setVisibility(View.GONE);
            rootView.findViewById(R.id.select_location).setVisibility(View.VISIBLE);
        }
        else {
            rootView.findViewById(R.id.selected_location).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.select_location).setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.location_name)).setText(location.getLocationName());
        }
    }

    public void validate(){
        if(rating.getRating()==0){
            Toast.makeText(getActivity(), "Please fill rating bar!", Toast.LENGTH_SHORT).show();
        }
        else if(review.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), "Please write a review!", Toast.LENGTH_SHORT).show();
        }
        else if(selectedLocation==null){
            Toast.makeText(getActivity(), "Please select a location!", Toast.LENGTH_SHORT).show();
        }
        else{
            uploadFile(Preferences.getStringPreferences("filteredPhoto", getActivity().getApplicationContext()));
        }
    }
    private void uploadFile(String mediaPath) {
        progressDialog.show();

        // Map is used to multipart the file using okhttp3.RequestBody
        Log.d("FILE",mediaPath);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/post.png");

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        String api_key = Preferences.getStringPreferences("api_key",getActivity());
        //Toast.makeText(getActivity(), "Review : "+review.getText().toString()+"\nRating : "+rating.getRating()+"\napi key"+api_key, Toast.LENGTH_SHORT).show();
        Call<Validation> call = APIService.service.uploadFile(fileToUpload, filename,review.getText().toString(),rating.getRating(),selectedLocation.getLocationId(),api_key);
        call.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                Validation serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getStatus().equals("success")) {
                        Toast.makeText(getActivity(), "Berhasil",Toast.LENGTH_SHORT).show();
                        Preferences.setBooleanPreferences("posted",true,getActivity());
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), serverResponse.getStatus().toString(),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {
                Toast.makeText(getActivity(), "Oops!"+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UPLOAD",t.getMessage());
            }
        });

    }
    /*private void uploadImage(String imagePath) {


        progressDialog.show();

        //File creating from selected URL
        File file = new File(Uri.parse(imagePath).getPath());

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

        Call<Validation> resultCall = APIService.service.uploadImage(body);

        // finally, execute the request
        resultCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                progressDialog.dismiss();

                // Response Success or Fail
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("success"))
                        Toast.makeText(getActivity(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "failed to post!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {
                Toast.makeText(getActivity(), "Oops!"+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UPLOAD",t.getMessage());
            }
        });

    }*/
}
