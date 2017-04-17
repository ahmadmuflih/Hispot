package id.edutech.baso.mapsproject;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import id.edutech.baso.mapsproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import models.Marker;
import models.MarkersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private final String BASE_URL = "http://www.json-generator.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        {
          "Markers": [
            {
              "id": "marker01",
              "latitude": -6.977248,
              "longitude": 107.629434
            },
            {
              "id": "marker02",
              "latitude": -6.973109,
              "longitude": 107.639113
            },
            {
              "id": "marker03",
              "latitude": -6.97046,
              "longitude": 107.630319
            }
          ]
        }
         */
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setMinZoomPreference(7);
        getMarkerArray();


    }
    public void getMarkerArray() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MarkersAPI service = retrofit.create(MarkersAPI.class);

        Call<List<Marker>> call = service.getMarkerDetails();

        call.enqueue(new Callback<List<Marker>>() {
            @Override
            public void onResponse(Call<List<Marker>> call, Response<List<Marker>> response) {

                try {

                    List<Marker> Markers= response.body();

                    for(Marker marker : Markers){
                        LatLng point= marker.getLocation();
                        googleMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title(marker.getLocationId())
                        );
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(Markers.get(0).getLocation()));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<List<Marker>> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

}
