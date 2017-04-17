package id.edutech.baso.mapsproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import models.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import models.User;


public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{
    ProgressBar progressBar;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = LocationActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private String BASE_URL="http://192.168.1.105/hispot/";
    Call<User> loginCall;
    User user;
    boolean cekLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cekLogin = Preferences.getBooleanPreferences("login",getApplicationContext());

        Log.d("aaa"," status login :"+cekLogin);
        if(!cekLogin) {
            setContentView(R.layout.activity_login);

            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                ActionBar actionBar = getActionBar();
                if (actionBar != null)
                    actionBar.hide();
            }
            //button = (Button) findViewById(R.id.btn_sign_in);

            findViewById(R.id.btn_sign_in).setOnClickListener(this);
            SignInButton signInButton = (SignInButton) findViewById(R.id.btn_sign_in);
            signInButton.setSize(SignInButton.SIZE_STANDARD);

        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        GoogleSignInOptions gso = ((Authentication) getApplication()).getGoogleSignInOptions();
        mGoogleApiClient = ((Authentication) getApplication()).getGoogleApiClient(LoginActivity.this, this);
        //button.setOnClickListener(this);



    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                progressBar.setVisibility(v.VISIBLE);
                signIn();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            //Toast.makeText(getApplicationContext(),"status code : "+result.getStatus().getStatusMessage(),Toast.LENGTH_LONG).show();
            handleSignInResult(result);

        }
    }
    public void signOut() {
    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {

            }
        });
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personPhotoUrl="";
            if(acct.getPhotoUrl()!=null)
                personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            loginCall = APIService.service.getLogin(email,personName,personPhotoUrl);
            loginCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        user = response.body();
                        Preferences.setStringPreferences("user_id",user.getId(),getApplicationContext());
                        Preferences.setStringPreferences("bio","Ini adalah biodata saya yang akan ditampilkan di profil saya",getApplicationContext());
                        Preferences.setStringPreferences("nama",user.getNama(),getApplicationContext());
                        Preferences.setStringPreferences("email",user.getEmail(),getApplicationContext());
                        Preferences.setStringPreferences("imageUrl",user.getFoto(),getApplicationContext());
                        Preferences.setStringPreferences("backgroundUrl",user.getBackground(),getApplicationContext());
                        Preferences.setStringPreferences("api_key",user.getApiKey(),getApplicationContext());
                        //Toast.makeText(getApplicationContext(), "API : "+Preferences.getStringPreferences("api_key",getApplicationContext()), Toast.LENGTH_SHORT).show();
                        Preferences.setBooleanPreferences("login",true,getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Login failed, check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            });
            /*
            Preferences.setBooleanPreferences("login",true,getApplicationContext());
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
            finish();
            */
        }
        else{
            //Toast.makeText(getApplicationContext(),"Terjadi kesalahan!",Toast.LENGTH_LONG).show();
            //progressBar.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(cekLogin) {
            /*
            Preferences.setStringPreferences("user_id","2",getApplicationContext());
            String api_key="fIUtYHgAJj9hHTlO";
            String email="ahmadbaso97@gmail.com";
            Preferences.setStringPreferences("api_key",api_key,getApplicationContext());
            */
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
