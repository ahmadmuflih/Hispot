package id.edutech.baso.mapsproject;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import models.APIService;
import models.Post;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    AlertDialog alertDialog;
    AlertDialog reportDialog;
    ImageButton like;
    ImageView fotoProfil;
    ImageView foto;
    TextView namaUser;
    TextView namaLokasi;
    TextView jumlahLike;
    TextView waktu;
    TextView review;
    RatingBar rating;
    ProgressBar progress;
    EditText reasonReport;
    String email;
    Post post;
    boolean statusLike;
    String api_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Photo");

        api_key = Preferences.getStringPreferences("api_key",getApplicationContext());

        like = (ImageButton) findViewById(R.id.like);
        fotoProfil = (ImageView) findViewById(R.id.foto_user);
        foto = (ImageView) findViewById(R.id.photo);
        namaUser = (TextView) findViewById(R.id.nama_user);
        namaLokasi = (TextView) findViewById(R.id.location_name);
        jumlahLike = (TextView) findViewById(R.id.jumlah_like);
        review = (TextView) findViewById(R.id.review);
        waktu = (TextView) findViewById(R.id.time);
        rating = (RatingBar) findViewById(R.id.rating);
        progress = (ProgressBar) findViewById(R.id.progress_foto);



        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.custom_dialog,null);
        alertDialog = new AlertDialog.Builder(this)
                .setView(customLayout)
                .create();

        findViewById(R.id.action).setOnClickListener(this);
        findViewById(R.id.like).setOnClickListener(this);

        customLayout.findViewById(R.id.share).setOnClickListener(this);
        customLayout.findViewById(R.id.report).setOnClickListener(this);
        customLayout.findViewById(R.id.save).setOnClickListener(this);
        fotoProfil.setOnClickListener(this);
        namaUser.setOnClickListener(this);
        namaLokasi.setOnClickListener(this);
        foto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                like();
                return true;
            }
        });

        final View reportLayout = inflater.inflate(R.layout.report_dialog,null);
        reportDialog = new AlertDialog.Builder(this)
                .setView(reportLayout)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitReport();
                    }
                })
                .create();
        reportDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                              @Override
                              public void onShow(DialogInterface arg0) {
                                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                      reportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
                                      reportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                                  }
                                  else{
                                      reportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                      reportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                                  }
                              }
                          });

        email = Preferences.getStringPreferences("email",getApplicationContext());
        EditText emailReport = (EditText)reportLayout.findViewById(R.id.input_email);
        emailReport.setText(email);
        reasonReport = (EditText)reportLayout.findViewById(R.id.input_report);
        initPhoto();
    }
    void initPhoto(){

        Intent intent = getIntent();
        int idPost = intent.getIntExtra("post_id",0);
        int idUser= intent.getIntExtra("id_user",0);
        float ratings= intent.getFloatExtra("rating",0);
        String reviews= intent.getStringExtra("review");
        String waktus = intent.getStringExtra("waktu");
        int jumlahLikes=intent.getIntExtra("jumlahLike",0);
        int locationID=intent.getIntExtra("location_id",0);
        String locationName = intent.getStringExtra("location_name");

        String urlThumbnail="";
        String urlGambar=intent.getStringExtra("url_gambar");
        post = new Post(idPost, urlThumbnail, urlGambar, jumlahLikes, idUser, locationName, locationID, ratings, reviews, waktus);
        try {
            Call<Validation> cekLikeCall = APIService.service.getLike("cek", post.getIdPost(), api_key);
            statusLike = false;
            cekLikeCall.enqueue(new Callback<Validation>() {
                @Override
                public void onResponse(Call<Validation> call, Response<Validation> response) {
                    if (response.isSuccessful()) {
                        statusLike = Boolean.parseBoolean(response.body().getStatus());
                        int countlike = response.body().getData();
                        if (statusLike) {
                            like.setImageResource(R.drawable.ic_tab_favourite2);
                        } else {
                            like.setImageResource(R.drawable.ic_tab_favourite);
                        }
                        jumlahLike.setText(countlike + " likes");
                    }
                }

                @Override
                public void onFailure(Call<Validation> call, Throwable t) {

                }
            });
        }catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }


        String foto_user=intent.getStringExtra("foto_user");
        String nama_user=intent.getStringExtra("nama_user");
        post.setNamaUser(nama_user);
        if(locationName!="")
        namaUser.setText(post.getNamaUser());
        else
        namaUser.setVisibility(View.GONE);
        namaLokasi.setText(post.getLocationName());
        jumlahLike.setText(post.getJumlahLike()+" likes");
        review.setText(post.getReview());
        waktu.setText(post.getWaktu());
        rating.setRating(ratings);
        if(statusLike){
            like.setImageResource(R.drawable.ic_tab_favourite2);
        }

        ImageUtil.capture(foto_user, "", fotoProfil, new ImageUtil.ImageUtilListener() {
            @Override
            public void onDisplayed() {

            }
        });

        progress.setVisibility(View.VISIBLE);
        String simpanFoto ="fotol"+post.getIdPost()+".png";
        Bitmap fotoS = ImageUtil.getImage(simpanFoto, getApplicationContext());
        if(fotoS==null) {
            ImageUtil.capture(post.getUrlGambar(), simpanFoto, foto, new ImageUtil.ImageUtilListener() {
                @Override
                public void onDisplayed() {
                    progress.setVisibility(View.GONE);
                }
            });
        }
        else {
            foto.setImageBitmap(fotoS);
            progress.setVisibility(View.GONE);
        }

    }

    void submitReport(){
        String isi_laporan = reasonReport.getText().toString();
        Call<Validation> reportCall = APIService.service.addReport(email,isi_laporan,post.getIdPost(),api_key);
        reportCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().equals("success")){
                        Toast.makeText(getApplicationContext(), "Report has been sent!", Toast.LENGTH_SHORT).show();
                        reasonReport.setText("");
                    }
                }
                else{
                    Toast.makeText(PhotoActivity.this, "Failed to send report, please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {
                Toast.makeText(PhotoActivity.this, "Failed to send report, please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    void like(){
        like.setEnabled(false);
        Call<Validation> likeCall= APIService.service.getLike("like",post.getIdPost(),api_key);
        likeCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                like.setEnabled(true);
                if (response.isSuccessful()){
                    if(response.body().getStatus().equals("success")){
                        String likes = jumlahLike.getText().toString();
                        int countlike=Integer.parseInt(likes.split(" ")[0]);
                        if (!statusLike) {
                            like.setImageResource(R.drawable.ic_tab_favourite2);
                            countlike++;
                        }
                        else {
                            like.setImageResource(R.drawable.ic_tab_favourite);
                            countlike--;
                        }
                        statusLike=!statusLike;
                        jumlahLike.setText(countlike+" likes");
                    }
                }
                else{
                    Toast.makeText(PhotoActivity.this, "Failed to like this post, please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {

            }
        });

    }
    void action(){
        alertDialog.show();
    }
    void share(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri uri = write("fotoShare.png",((BitmapDrawable)foto.getDrawable()).getBitmap());
        //Toast.makeText(PhotoActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Send Photo"));
    }
    void report(){
        reportDialog.show();
    }
    void save(){
        Bitmap fotoS = ((BitmapDrawable)foto.getDrawable()).getBitmap();

        SaveBitmap.saveBitmapToExternal(post.getLocationName()+".png",fotoS);
        Toast.makeText(getApplicationContext(),"Foto berhasil disimpan!",Toast.LENGTH_SHORT).show();
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
        int id=v.getId();
        switch (id){
            case R.id.action:
                action();
                break;
            case R.id.like:
                like();
                break;
            case R.id.share:
                share();
                break;
            case R.id.report:
                report();
                break;
            case R.id.save:
                save();
                break;
            case R.id.foto_user:

            case R.id.nama_user:
                    /*AlertDialog profil = new AlertDialog.Builder(this)
                            .setView(getLayoutInflater().inflate(R.layout.profile_dialog,null))
                            .create();
                    profil.show();*/
                break;
            case R.id.location_name:
                    Intent intent = new Intent(getApplicationContext(),LocationActivity.class);
                    intent.putExtra("locationId",post.getLocationID()+"");
                    intent.putExtra("locationName",post.getLocationName());
                    startActivity(intent);
                break;
        }
    }
    private Uri write(final String filename, final Bitmap bitmap) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(dir,"/Hispot");
            try {
                if(!file.exists())
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return Uri.fromFile(file);
    }

}
