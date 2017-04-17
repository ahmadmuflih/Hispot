package id.edutech.baso.mapsproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;

import java.io.IOException;
import java.util.ArrayList;

import models.APIService;
import models.Post;
import models.ProfileResults;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageAdapter imageAdapter;
    ImageView background;
    ImageView foto;
    TextView nama,bio, newNama,newBio,txtPost,txtLike,txtSubscribing,txtSubscriber;
    ImageButton gantiFoto;
    AlertDialog textDialog,profileDialog;
    String fotoStatus;
    Bitmap fotoTersimpan;
    String api_key;

    final int CHOOSE_CAMERA = 1;
    final int CHOOSE_GALLERY = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Profile");

        LinearLayout linearLayout;
        linearLayout = (LinearLayout)findViewById(R.id.subscribing);
        linearLayout.setOnClickListener(this);
        linearLayout = (LinearLayout)findViewById(R.id.subscribers);
        linearLayout.setOnClickListener(this);
        background = (ImageView)findViewById(R.id.background);
        foto = (ImageView)findViewById(R.id.foto);
        gantiFoto = (ImageButton) findViewById(R.id.ganti_foto);
        nama = (TextView)findViewById(R.id.nama);
        bio = (TextView)findViewById(R.id.bio);
        txtPost = (TextView)findViewById(R.id.txtPost);
        txtLike = (TextView)findViewById(R.id.txtLike);
        txtSubscribing = (TextView)findViewById(R.id.txtSubscribing);
        txtSubscriber = (TextView)findViewById(R.id.txtSubscriber);
        background.setOnClickListener(this);
        gantiFoto.setOnClickListener(this);
        foto.setOnClickListener(this);
        nama.setOnClickListener(this);
        bio.setOnClickListener(this);

        LayoutInflater inflater = getLayoutInflater();
        View editProfileLayout = inflater.inflate(R.layout.edit_profile_dialog,null);
        newNama = (TextView) editProfileLayout.findViewById(R.id.nama_baru);
        newBio = (TextView) editProfileLayout.findViewById(R.id.bio_baru);
        textDialog = new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(editProfileLayout)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editProfile();
                    }
                })
                .create();
        initProfile();
    }

    void initProfile(){
        api_key=Preferences.getStringPreferences("api_key",getApplicationContext());
        fotoTersimpan = ImageUtil.getImage("fotoProfil.png",getApplicationContext());
        Bitmap backgroundTersimpan = ImageUtil.getImage("fotoBackground.png",getApplicationContext());
        if(fotoTersimpan!=null){
            foto.setImageBitmap(fotoTersimpan);
        }
        if(backgroundTersimpan!=null){
            background.setImageBitmap(backgroundTersimpan);
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        final int jumlahPost = Preferences.getIntPreferences("jumlahPost",getApplicationContext());
        final int jumlahLike = Preferences.getIntPreferences("jumlahLike",getApplicationContext());
        final int jumlahSubscribing = Preferences.getIntPreferences("jumlahSubscribing",getApplicationContext());
        int jumlahSubscriber = Preferences.getIntPreferences("jumlahSubscriber",getApplicationContext());
        String nNama = Preferences.getStringPreferences("nama",getApplicationContext());
        String nBio = Preferences.getStringPreferences("bio",getApplicationContext());
        nama.setText(nNama);
        bio.setText(nBio);
        txtPost.setText(""+jumlahPost);
        txtLike.setText(""+jumlahLike);
        txtSubscribing.setText(""+jumlahSubscribing);
        txtSubscriber.setText(""+jumlahSubscriber);
        String user_id=Preferences.getStringPreferences("user_id",getApplicationContext());
        Call<ProfileResults> callProfile = APIService.service.getProfile(user_id,api_key);
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
                    imageAdapter = new ImageAdapter(getApplicationContext(),posts,3);
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
                    Toast.makeText(ProfileActivity.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResults> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something error!", Toast.LENGTH_SHORT).show();
            }
        });


    }
    void editProfile(){
        final String nNama = newNama.getText().toString();
        final String nBio = newBio.getText().toString();
        Call<Validation> updateCall = APIService.service.updateProfile("update_bio",nNama,nBio,api_key);
        updateCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().equals("success")){
                        Preferences.setStringPreferences("nama",nNama,getApplicationContext());
                        Preferences.setStringPreferences("bio",nBio,getApplicationContext());
                        nama.setText(nNama);
                        bio.setText(nBio);
                        Toast.makeText(getApplicationContext(), "New profile has been saved", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ProfileActivity.this, "Failed to update profile, please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something error!", Toast.LENGTH_SHORT).show();
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
            case R.id.subscribing:
                startActivity(new Intent(getApplicationContext(),SubscribingActivity.class));
                break;
            case R.id.subscribers:
                startActivity(new Intent(getApplicationContext(),SubscribersActivity.class));
                break;
            case R.id.ganti_foto:
                fotoStatus="foto";
                getPhotoDialog("Change Profile Photo").show();
                break;
            case R.id.background:
                fotoStatus="background";
                getPhotoDialog("Change Background Photo").show();
                break;

            case R.id.foto:
                View photoLayout = getLayoutInflater().inflate(R.layout.photo_dialog,null);
                ImageView foto = (ImageView) photoLayout.findViewById(R.id.fotoprofile);
                foto.setImageBitmap(fotoTersimpan);
                profileDialog = new AlertDialog.Builder(this)
                        .setView(photoLayout)
                        .create();
                profileDialog.show();
                break;
            case R.id.nama:

            case R.id.bio:
                    newNama.setText(Preferences.getStringPreferences("nama",getApplicationContext()));
                    newBio.setText(Preferences.getStringPreferences("bio",getApplicationContext()));
                    textDialog.show();
                break;

        }
    }

    public AlertDialog getPhotoDialog(String title){
        final CharSequence[] items = {
                "Take from Camera", "Select from Gallery", "Delete"
        };
        AlertDialog photoDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                Intent IntentButtonCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if(IntentButtonCamera.resolveActivity(getPackageManager())!=null){
                                    startActivityForResult(IntentButtonCamera,CHOOSE_CAMERA);
                                }
                                break;
                            case 1:
                                Intent in = new Intent();
                                in.setType("image/*");
                                in.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(in, "Choose a Photo"), CHOOSE_GALLERY);
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(), fotoStatus, Toast.LENGTH_SHORT).show();
                                if(fotoStatus=="foto") {
                                    ImageUtil.deleteImage("fotoProfil.png", getApplicationContext());
                                    foto.setImageResource(R.drawable.avatar);
                                }
                                else if(fotoStatus=="background"){
                                    ImageUtil.deleteImage("fotoBackground.png", getApplicationContext());
                                    background.setImageResource(R.drawable.side_nav_bar);
                                }
                                break;
                        }
                    }
                })
                .create();
        return photoDialog;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            Bitmap hasil = null;
            if(requestCode==CHOOSE_GALLERY){
                if (data != null) {
                    try {
                        hasil = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (requestCode==CHOOSE_CAMERA){
                hasil = (Bitmap) data.getExtras().get("data");
                if(hasil==null){
                    Toast.makeText(getApplicationContext(),"Terjadi kesalahan!",Toast.LENGTH_LONG).show();
                }
            }
            if(hasil!=null){
                if(fotoStatus=="foto") {
                    ImageUtil.saveBitmap(hasil, "fotoProfil.png", foto);
                    foto.setImageBitmap(hasil);
                }
                else if(fotoStatus=="background"){
                    ImageUtil.saveBitmap(hasil, "fotoBackground.png", background);
                    background.setImageBitmap(hasil);
                }
            }
        }

    }
}
