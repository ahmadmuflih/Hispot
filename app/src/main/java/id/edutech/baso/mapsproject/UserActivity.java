package id.edutech.baso.mapsproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.APIService;
import models.Post;
import models.ProfileResults;
import models.Subscribe;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageAdapter imageAdapter;
    ImageView foto;
    ImageView background;
    TextView nama,bio,txtPost,txtLike,txtSubscribing,txtSubscriber;
    Button btnSubscribe;
    String api_key;
    Subscribe user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("nama");
        final int userId = intent.getIntExtra("id",0);
        String fotoUser = intent.getStringExtra("foto");
        String biodata = intent.getStringExtra("bio");
        String backgroundUser = intent.getStringExtra("background");

        boolean status = intent.getBooleanExtra("status",false);
        user = new Subscribe();
        user.setNamaUser(userName);
        user.setFotoUser(fotoUser);
        user.setIdUser(userId);
        user.setKeterangan(biodata);
        user.setStatus(status);
        user.setBackgroundUser(backgroundUser);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(userName);

        LinearLayout linearLayout;
        linearLayout = (LinearLayout)findViewById(R.id.subscribing);
        linearLayout.setOnClickListener(this);
        linearLayout = (LinearLayout)findViewById(R.id.subscribers);
        linearLayout.setOnClickListener(this);
        foto = (ImageView)findViewById(R.id.foto);
        background = (ImageView)findViewById(R.id.background);
        btnSubscribe = (Button)findViewById(R.id.subscribe);
        nama = (TextView)findViewById(R.id.nama);
        bio=(TextView)findViewById(R.id.bio);
        txtPost = (TextView)findViewById(R.id.txtPost);
        txtLike = (TextView)findViewById(R.id.txtLike);
        txtSubscribing = (TextView)findViewById(R.id.txtSubscribing);
        txtSubscriber = (TextView)findViewById(R.id.txtSubscriber);
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubscribe.setEnabled(false);
                btnSubscribe.setText("Requesting..");
                Call<Validation> subscribeCall = APIService.service.subscribe(user.getIdUser()+"",api_key);
                subscribeCall.enqueue(new Callback<Validation>() {
                    @Override
                    public void onResponse(Call<Validation> call, Response<Validation> response) {
                        btnSubscribe.setEnabled(true);
                        if(response.isSuccessful()){
                            if(response.body().getStatus().equals("success")){
                                user.setStatus(!user.isStatus());
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                        cekStatus(user,btnSubscribe);
                    }

                    @Override
                    public void onFailure(Call<Validation> call, Throwable t) {

                    }
                });

            }
        });
        initProfile();
    }

    void initProfile(){
        ImageUtil.capture(user.getFotoUser(), "", foto, new ImageUtil.ImageUtilListener() {
            @Override
            public void onDisplayed() {
            }
        });
        ImageUtil.capture(user.getBackgroundUser(), "", background, new ImageUtil.ImageUtilListener() {
            @Override
            public void onDisplayed() {
            }
        });
        ArrayList<Post> posts = new ArrayList<>();




        cekStatus(user,btnSubscribe);

        nama.setText(user.getNamaUser());
        bio.setText(user.getKeterangan());

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        api_key = Preferences.getStringPreferences("api_key",getApplicationContext());
        Call<ProfileResults> callProfile = APIService.service.getProfile(user.getIdUser()+"",api_key);
        callProfile.enqueue(new Callback<ProfileResults>() {
            @Override
            public void onResponse(Call<ProfileResults> call, Response<ProfileResults> response) {
                if(response.isSuccessful()){
                    ProfileResults profileResults = response.body();
                    txtPost.setText(""+profileResults.getJumlahPost());
                    txtLike.setText(""+profileResults.getJumlahLike());
                    txtSubscribing.setText(""+profileResults.getSubscribing());
                    txtSubscriber.setText(""+profileResults.getSubscribers());
                    Preferences.setIntPreferences("jumlahPost",profileResults.getJumlahPost(),getApplicationContext());
                    Preferences.setIntPreferences("jumlahLike",profileResults.getJumlahLike(),getApplicationContext());
                    Preferences.setIntPreferences("jumlahSubscribing",profileResults.getSubscribing(),getApplicationContext());
                    Preferences.setIntPreferences("jumlahSubscriber",profileResults.getSubscribers(),getApplicationContext());
                    ArrayList<Post> posts = profileResults.getPosts();
                    imageAdapter = new ImageAdapter(getApplicationContext(),posts,4);
                    imageAdapter.setOnPostSelectedListener(new ImageAdapter.OnPostSelectedListener() {
                        @Override
                        public void onSelected(Post post) {
                            Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
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

                    recyclerView.setAdapter(imageAdapter);
                    recyclerView.setFocusable(false);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResults> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something error!", Toast.LENGTH_SHORT).show();
            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){


            case R.id.foto:

                break;


        }
    }
    public void cekStatus(Subscribe subscribe, Button button){
        if (subscribe.isStatus()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_check, getTheme()), null, null, null);
                button.setBackground(getResources().getDrawable(R.drawable.button_shape2,getTheme()));
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_check), null, null, null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(getResources().getDrawable(R.drawable.button_shape2));
                }
                else{
                    button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape2));
                }
            }
            button.setText("SUBSCRIBED");
            button.setTextColor(Color.WHITE);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_add, getTheme()), null, null, null);
                button.setBackground(getResources().getDrawable(R.drawable.button_shape,getTheme()));
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_add), null, null, null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(getResources().getDrawable(R.drawable.button_shape));
                }
                else{
                    button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
                }
            }
            button.setText("SUBSCRIBE");
            button.setTextColor(Color.parseColor("#00897B"));
        }
    }
}
