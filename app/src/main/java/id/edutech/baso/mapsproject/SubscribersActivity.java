package id.edutech.baso.mapsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.APIService;
import models.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribersActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Subscribers");
        progressBar = (ProgressBar) findViewById(R.id.progress_subscribers);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_subscribers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String api_key=Preferences.getStringPreferences("api_key",getApplicationContext());
        Call<ArrayList<Subscribe>> subscribersCall = APIService.service.getSubscriber("subscriber",api_key);
        subscribersCall.enqueue(new Callback<ArrayList<Subscribe>>() {
            @Override
            public void onResponse(Call<ArrayList<Subscribe>> call, Response<ArrayList<Subscribe>> response) {
                if(response.isSuccessful()){
                    ArrayList<Subscribe> subscribers = response.body();
                    SubscribeAdapter adapter = new SubscribeAdapter(getApplicationContext(),subscribers);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnSubscribeSelectedListener(new SubscribeAdapter.OnSubscribeSelectedListener() {
                        @Override
                        public void onSelected(Subscribe subscribe) {
                            Intent i = new Intent(getApplicationContext(),UserActivity.class);
                            i.putExtra("nama",subscribe.getNamaUser());
                            i.putExtra("id",subscribe.getIdUser());
                            i.putExtra("foto",subscribe.getFotoUser());
                            i.putExtra("bio",subscribe.getKeterangan());
                            i.putExtra("status",subscribe.isStatus());
                            i.putExtra("background",subscribe.getBackgroundUser());
                            startActivity(i);
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Unable to load subscribers, please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<Subscribe>> call, Throwable t) {

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
}
