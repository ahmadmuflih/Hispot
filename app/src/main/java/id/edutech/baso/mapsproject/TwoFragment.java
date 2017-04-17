
package id.edutech.baso.mapsproject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.APIService;
import models.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class TwoFragment extends Fragment {
    ImageAdapter imageAdapter;
    View view;
    ArrayList<Post> posts;
    Call<ArrayList<Post>> postCall;
    public TwoFragment() {
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
        view = inflater.inflate(R.layout.fragment_two, container, false);


        String locationId = ((LocationActivity)getActivity()).getLocationId();
        String api_key=Preferences.getStringPreferences("api_key", getActivity());
        postCall = APIService.service.getLocationPost("all",locationId,api_key);
        postCall.enqueue(new Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                if (response.isSuccessful()) {
                    posts = response.body();
                    imageAdapter = new ImageAdapter(getActivity().getApplicationContext(),posts,4);
                    imageAdapter.setOnPostSelectedListener(new ImageAdapter.OnPostSelectedListener() {
                        @Override
                        public void onSelected(Post post) {
                            Intent intent = new Intent(getActivity(),PhotoActivity.class);
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
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    recyclerView.setAdapter(imageAdapter);
                    recyclerView.setFocusable(false);
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable t) {

            }
        });


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
