package id.edutech.baso.mapsproject;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import models.APIService;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    String nama,email,urlFoto,urlBackground;
    TextView textNama, textEmail;
    LinearLayout layoutNavbar;
    ImageView imageProfil;
    private GoogleApiClient mGoogleApiClient;
    Menu actionMenu;
    boolean isTrendingLoaded = false, isNearbyLoaded = false,isExploreLoaded = false,isFeedbackLoaded = false;
    Fragment fragmentTrending,fragmentNearby,fragmentExplore, fragmentFeedback;
    MapViewFragment mapView;
    private static final String TAG = "HomeActivity";
    final int CHOOSE_CAMERA = 1;
    final int CROPING_CODE = 2;
    Uri outPutfileUri;
    File output, outputFile;
    Call<Validation> validationCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trending");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mGoogleApiClient = ((Authentication) getApplication()).getGoogleApiClient(HomeActivity.this, this);

        
    
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        textNama = (TextView)hView.findViewById(R.id.textNama);
        textEmail = (TextView)hView.findViewById(R.id.textEmail);
        imageProfil=(ImageView)hView.findViewById(R.id.imageProfil);

        nama = Preferences.getStringPreferences("nama",getApplicationContext());
        email = Preferences.getStringPreferences("email",getApplicationContext());
        urlFoto = Preferences.getStringPreferences("imageUrl",getApplicationContext());
        urlBackground = Preferences.getStringPreferences("backgroundUrl",getApplicationContext());
        layoutNavbar=(LinearLayout)hView.findViewById(R.id.navbar_layout);
        layoutNavbar.setOnClickListener(this);
        findViewById(R.id.take_picture).setOnClickListener(this);



        initProfile();


    }
    public void initProfile(){
        textNama.setText(nama);
        textEmail.setText(email);
        Bitmap fotoTersimpan = ImageUtil.getImage("fotoProfil.png",getApplicationContext());
        Bitmap backgroundTersimpan = ImageUtil.getImage("fotoBackground.png",getApplicationContext());
        if(!urlFoto.equals("") && !Preferences.getStringPreferences("LinkFoto", getApplicationContext()).equals(urlFoto)){
            ImageUtil.capture(urlFoto, "fotoProfil.png", imageProfil, new ImageUtil.ImageUtilListener() {
                @Override
                public void onDisplayed() {
                    Preferences.setStringPreferences("LinkFoto",urlFoto,getApplicationContext());
                    Log.d("PROFILE", "LOAD DARI URL");
                }
            });
        }
        else if(Preferences.getStringPreferences("LinkFoto", getApplicationContext()).equals(urlFoto) && fotoTersimpan!=null){
            imageProfil.setImageBitmap(fotoTersimpan);
            Log.d("PROFIL", "LOAD DARI INTERNAL");
        }
        else{
            Log.d("PROFIL", "GAGAL LOAD FOTO");
        }


        if(!urlBackground.equals("") && !Preferences.getStringPreferences("LinkBackground", getApplicationContext()).equals(urlBackground)){
            ImageUtil.setBackground(urlBackground, "fotoBackground.png", getApplicationContext(), new ImageUtil.BackgroundUtilListener() {
                @Override
                public void onFinished(Drawable drawable) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        layoutNavbar.setBackground(drawable);
                    }
                    else{
                        layoutNavbar.setBackgroundDrawable(drawable);
                    }
                    Log.d("BACKGROUND", "LOAD DARI URL");
                }
            });
        }
        else if(Preferences.getStringPreferences("LinkBackground", getApplicationContext()).equals(urlBackground) && backgroundTersimpan!=null){
            Drawable drawable = new BitmapDrawable(getResources(),backgroundTersimpan);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layoutNavbar.setBackground(drawable);
            }
            else{
                layoutNavbar.setBackgroundDrawable(drawable);
            }

            imageProfil.setImageBitmap(fotoTersimpan);
            Log.d("BACKGROUND", "LOAD DARI INTERNAL");
        }
        else{
            Log.d("BACKGROUND", "GAGAL LOAD BACKGROUND");
        }
    }
    

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trending, menu);
        actionMenu = menu;
        displaySelectedScreen(R.id.nav_trending);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search){
            startActivity(new Intent(getApplicationContext(),SearchActivity.class));
        }
        else if (id == R.id.action_expand){
            actionMenu.findItem(R.id.action_close_expand).setVisible(true);
            actionMenu.findItem(R.id.action_expand).setVisible(false);
            mapView.changeMarkerIconList(View.VISIBLE);
        }
        else if (id == R.id.action_close_expand){
            actionMenu.findItem(R.id.action_expand).setVisible(true);
            actionMenu.findItem(R.id.action_close_expand).setVisible(false);
            mapView.changeMarkerIconList(View.GONE);
        }
        else if(id == R.id.action_send_feedback){
            Toast.makeText(HomeActivity.this, "Send!", Toast.LENGTH_SHORT).show();
            FeedbackFragment feedbackF = (FeedbackFragment) fragmentFeedback;
            feedbackF.validate();
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        String api_key=Preferences.getStringPreferences("api_key",this);

        final android.support.v7.app.AlertDialog textDialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage("Your login session has expired, please log out to continue!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .create();
        textDialog.setCancelable(false);
        textDialog.setCanceledOnTouchOutside(false);
        validationCall = APIService.service.getValidation(api_key);
        validationCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                if(response.isSuccessful()){
                    Validation valid=response.body();
                    Log.e("a",""+response.raw());
                    if(valid.getStatus().equals("invalid")){

                        textDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {
                //Toast.makeText(HomeActivity.this, "Status : Gagal" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        nama=Preferences.getStringPreferences("nama",getApplicationContext());
        textNama.setText(nama);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap fotoTersimpan = ImageUtil.getImage("fotoProfil.png",getApplicationContext());
                Bitmap backgroundTersimpan = ImageUtil.getImage("fotoBackground.png",getApplicationContext());
                if(fotoTersimpan!=null)
                    imageProfil.setImageBitmap(fotoTersimpan);
                if(backgroundTersimpan!=null) {
                    Drawable drawable = new BitmapDrawable(getResources(),backgroundTersimpan);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        layoutNavbar.setBackground(drawable);
                    }
                    else {
                        layoutNavbar.setBackgroundDrawable(drawable);
                    }
                }
            }
        }, 1000);
    }

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;
        if (id == R.id.nav_trending) {
            getSupportActionBar().setTitle("Trending");
            if(!isTrendingLoaded){
                isTrendingLoaded=true;
                fragmentTrending = new TrendingFragment();
            }
            fragment = fragmentTrending;
        } else if (id == R.id.nav_nearby) {
            getSupportActionBar().setTitle("Nearby");
            if(!isNearbyLoaded){
                isNearbyLoaded=true;
                fragmentNearby = new NearbyFragment();
            }
            fragment = fragmentNearby;
        } else if (id == R.id.nav_explore) {
            getSupportActionBar().setTitle("Explore");
            if(!isExploreLoaded){
                isExploreLoaded=true;
                fragmentExplore = new MapViewFragment().newInstance(1);
                mapView = (MapViewFragment) fragmentExplore;

            }
            fragment = fragmentExplore;
        } else if (id == R.id.nav_send) {
            getSupportActionBar().setTitle("Feedback");
            if(!isFeedbackLoaded){
                isFeedbackLoaded=true;
                fragmentFeedback = new FeedbackFragment();

            }
            fragment = fragmentFeedback;
        } else if (id == R.id.nav_logout) {
            signOut();
        }
        if(id==R.id.nav_explore){
            actionMenu.findItem(R.id.action_expand).setVisible(true);
        }
        else{
            actionMenu.findItem(R.id.action_expand).setVisible(false);
            actionMenu.findItem(R.id.action_close_expand).setVisible(false);
        }
        if (fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    private void signOut() {
        Preferences.setBooleanPreferences("login",false,getApplicationContext());
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Intent login = new Intent(HomeActivity.this, LoginActivity.class);
                Toast.makeText(getApplicationContext(), "Anda berhasil sign out!", Toast.LENGTH_SHORT).show();
                startActivity(login);
                finish();
            }
        });
    }

    private void takePicture(){
        Intent IntentButtonCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File output=new File(dir, "temp1.jpg");
        outPutfileUri = Uri.fromFile(output);
        IntentButtonCamera.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        if(IntentButtonCamera.resolveActivity(getPackageManager())!=null){
            startActivityForResult(IntentButtonCamera,CHOOSE_CAMERA);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.navbar_layout:
                    Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(intent);
                break;
            case R.id.take_picture:
                takePicture();
                break;
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
   /*
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    */
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            if (requestCode==CHOOSE_CAMERA) {
                outputFile = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "temp.jpg");
                CropingIMG();
            }
            else if (requestCode == CROPING_CODE) {

                try {
                    if(outputFile.exists()){
                        //Bitmap photo = decodeFile(output);
                        //imageView.setImageBitmap(photo);
                        outPutfileUri = Uri.fromFile(outputFile);
                        Preferences.setStringPreferences("postUri",outPutfileUri.toString(),getApplicationContext());
                        Intent i = new Intent(this, FilterActivity.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(outPutfileUri);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));

            if (size == 1) {
                Intent i   = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title  = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon  = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);
                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (outPutfileUri != null ) {
                            getContentResolver().delete(outPutfileUri, null, null );
                            outPutfileUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();
                alert.show();


            }
        }
    }


    public void setCaptureVisible(int visibility){
            findViewById(R.id.capture_layout).setVisibility(visibility);
    }
    public void setSendFeedbackVisible(boolean visibility){
        actionMenu.findItem(R.id.action_send_feedback).setVisible(visibility);
    }
}
