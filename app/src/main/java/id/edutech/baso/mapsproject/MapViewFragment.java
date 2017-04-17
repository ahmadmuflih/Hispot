package id.edutech.baso.mapsproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import models.APIService;
import models.Marker;
import models.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 11/6/2016.
 */
public class MapViewFragment extends Fragment {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    MapView mMapView;
    private GoogleMap googleMap;
    ImageView foto;
    ProgressBar progressBar;
    private TextInputLayout inputLayoutName;
    android.location.Location myLocation;
    int screenWidth,screenHeight, rootActivity;
    private final int ACTIVITY_HOME = 1;
    private final int ACTIVITY_POST = 2;
    private final String BASE_URL = "http://www.json-generator.com/";
    ArrayList<com.google.android.gms.maps.model.Marker> listMarkers; // Marker google
    TextView locationDistance;
    List<Marker> Markers;   // Informasi Lokasi
    Marker selectedMarker;
    ArrayList<MarkerIcon> markerIcons;
    View rootView;
    GPSTracker gps;
    ImageButton addLocation;
    Button btnAddLocation;
    double jarak;
    boolean isLoaded = false;

    public static MapViewFragment newInstance(int rootActivity) {
        Bundle bundle = new Bundle();
        bundle.putInt("rootActivity", rootActivity);

        MapViewFragment fragment = new MapViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootActivity = getArguments().getInt("rootActivity");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_mapview, container, false);
        foto = (ImageView)rootView.findViewById(R.id.location_photo);
        progressBar = (ProgressBar)rootView.findViewById(R.id.location_progress);
        locationDistance = (TextView)rootView.findViewById(R.id.location_distance);
        inputLayoutName = (TextInputLayout)rootView.findViewById(R.id.input_layout_nama);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        if(rootActivity==ACTIVITY_POST){
            rootView.findViewById(R.id.btn_open_location).setVisibility(View.GONE);
            rootView.findViewById(R.id.btn_direction).setVisibility(View.GONE);
            rootView.findViewById(R.id.btn_select_location).setVisibility(View.VISIBLE);
        }
        final List<String> categories = new ArrayList<String>();
        categories.add("Park");
        categories.add("Mountain");
        categories.add("Beach");
        categories.add("Building");
        categories.add("Forest");
        categories.add("Mall");
        categories.add("Culture");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addLocation =  ((ImageButton)rootView.findViewById(R.id.add_location));
        btnAddLocation = (Button)rootView.findViewById(R.id.btn_add_location);
        addLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((TextView)rootView.findViewById(R.id.add_location_popup)).setVisibility(View.VISIBLE);
                hidePopUp();
                return false;
            }
        });
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout)rootView.findViewById(R.id.add_location_action)).setVisibility(View.VISIBLE);
                if(rootActivity==ACTIVITY_HOME)
                    ((HomeActivity)getActivity()).setCaptureVisible(View.GONE);
                ((LinearLayout)rootView.findViewById(R.id.location_action)).setVisibility(View.GONE);

                addLocation.setVisibility(View.GONE);
                LatLng point = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                com.google.android.gms.maps.model.Marker mark = googleMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("NewLocation")
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapIcon("M0")))
                );
                listMarkers.add(mark);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(),myLocation.getLongitude())));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        });
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edit = (EditText)rootView.findViewById(R.id.input_nama);
                final String nama = edit.getText().toString().trim();
                if(nama.equals("")){
                    inputLayoutName.setError("Fill this form first!");
                    requestFocus(edit);
                }
                else {
                    final String type = "M"+(spinner.getSelectedItemPosition()+1);

                    final LatLng point = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());


                    final Marker newMarker = new Marker();
                    newMarker.setLatitude((float) point.latitude);
                    newMarker.setLongitude((float) point.longitude);
                    newMarker.setLocationName(nama);
                    newMarker.setType(type);


                    Call<Validation> addMarkerCall;
                    String api_key=Preferences.getStringPreferences("api_key",getActivity());
                    addMarkerCall = APIService.service.addMarker(newMarker.getLocationName(),newMarker.getType(),newMarker.getLatitude(),newMarker.getLongitude(),api_key);
                    addMarkerCall.enqueue(new Callback<Validation>() {
                        @Override
                        public void onResponse(Call<Validation> call, Response<Validation> response) {
                            if(response.isSuccessful()) {
                                Validation valid;
                                valid = response.body();
                                if (valid.getStatus().equals("success")) {
                                    listMarkers.get(listMarkers.size() - 1).remove();
                                    listMarkers.remove(listMarkers.size() - 1);
                                    com.google.android.gms.maps.model.Marker mark = googleMap.addMarker(new MarkerOptions()
                                            .position(point)
                                            .title(nama)
                                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapIcon(type)))
                                    );
                                    listMarkers.add(mark);
                                    Markers.add(newMarker);
                                    ((LinearLayout) rootView.findViewById(R.id.add_location_action)).setVisibility(View.GONE);
                                    addLocation.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), "Location has successfully added!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getActivity(), "Failed to add location, please check your internet connection!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Validation> call, Throwable t) {
                            Toast.makeText(getActivity(), "Failed to add location, please check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        mMapView.onResume(); // needed to get the map to display immediately
        DisplayMetrics display = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
        screenWidth = display.widthPixels;
        screenHeight = display.heightPixels;
        markerIcons = new ArrayList<>();
        markerIcons.add(new MarkerIcon("M1", "Park", true, getBitmapIcon("M1")));
        markerIcons.add(new MarkerIcon("M2", "Mountain", true, getBitmapIcon("M2")));
        markerIcons.add(new MarkerIcon("M3", "Beach", true, getBitmapIcon("M3")));
        markerIcons.add(new MarkerIcon("M4", "Building", true, getBitmapIcon("M4")));
        markerIcons.add(new MarkerIcon("M5", "Forest", true, getBitmapIcon("M5")));
        markerIcons.add(new MarkerIcon("M6", "Mall", true, getBitmapIcon("M6")));
        markerIcons.add(new MarkerIcon("M7", "Culture", true, getBitmapIcon("M7")));
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        MarkerIconAdapter adapter = new MarkerIconAdapter(getActivity().getApplicationContext(),markerIcons);
        final RecyclerView myList = (RecyclerView) rootView.findViewById(R.id.list_marker);
        adapter.setOnMarkerCheckedListener(new MarkerIconAdapter.OnMarkerCheckedListener() {
            @Override
            public void onChecked(MarkerIcon markerIcon) {
                setMarkerTypeVisibility(markerIcon.getTypeId(),markerIcon.isChecked());
            }
        });

        myList.setLayoutManager(layoutManager);
        myList.setAdapter(adapter);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setMinZoomPreference(7);
                if(checkLocationPermission()) {
                    gps = new GPSTracker(getActivity().getApplicationContext());
                    if (gps.canGetLocation()) {
                        myLocation = gps.getLocation();

                        // \n is for new line
                        //Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                    gps.setOnLocationChangedListener(new GPSTracker.OnLocationChangedListener() {
                        @Override
                        public void onChanged(Location location) {
                            myLocation = location;
                            // \n is for new line
                            //Toast.makeText(getActivity().getApplicationContext(), "Your changed Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                        }
                    });
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(),myLocation.getLongitude())));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                getMarkers();
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        int index = listMarkers.indexOf(marker);
                        foto.setImageResource(R.drawable.vr);
                        progressBar.setVisibility(View.VISIBLE);
                        if(index!=-1 && !listMarkers.get(index).getTitle().equals("NewLocation")) {
                            ((LinearLayout) rootView.findViewById(R.id.location_action)).setVisibility(View.VISIBLE);
                            if(rootActivity==ACTIVITY_HOME)
                                ((HomeActivity)getActivity()).setCaptureVisible(View.GONE);
                            ((LinearLayout)rootView.findViewById(R.id.add_location_action)).setVisibility(View.GONE);
                            ((TextView) rootView.findViewById(R.id.location_name)).setText(Markers.get(index).getLocationName());
                            ImageUtil.capture(Markers.get(index).getThumbnail(), "", foto, new ImageUtil.ImageUtilListener() {
                                @Override
                                public void onDisplayed() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                            selectedMarker = Markers.get(index);
                            android.location.Location markerLocation = new Location("Marker");
                            markerLocation.setLatitude(selectedMarker.getLatitude());
                            markerLocation.setLongitude(selectedMarker.getLongitude());

                            String distance = getLocationDistance(myLocation, markerLocation);
                            locationDistance.setText(distance);
                        }
                        return true;
                    }
                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if(isLoaded) {
                            if (listMarkers.get(listMarkers.size() - 1).getTitle().equals("NewLocation")) {
                                listMarkers.get(listMarkers.size() - 1).remove();
                                listMarkers.remove(listMarkers.size() - 1);
                            }
                            ((LinearLayout) rootView.findViewById(R.id.location_action)).setVisibility(View.GONE);
                            ((LinearLayout) rootView.findViewById(R.id.add_location_action)).setVisibility(View.GONE);
                            if(rootActivity==ACTIVITY_HOME)
                                ((HomeActivity) getActivity()).setCaptureVisible(View.VISIBLE);
                            addLocation.setVisibility(View.VISIBLE);
                        }
                    }
                });
                ((Button)rootView.findViewById(R.id.btn_direction)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "https://www.google.com/maps?saddr="+myLocation.getLatitude()+","+myLocation.getLongitude()+"&daddr="+ selectedMarker.getLatitude() +","+selectedMarker.getLongitude();
                        Log.e("MAPS",uri);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(Intent.createChooser(intent, "Select an application"));
                    }
                });
                ((Button)rootView.findViewById(R.id.btn_open_location)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(),LocationActivity.class);
                        intent.putExtra("locationId",selectedMarker.getLocationId());
                        intent.putExtra("locationName",selectedMarker.getLocationName());
                        startActivity(intent);
                    }
                });
                ((Button)rootView.findViewById(R.id.btn_select_location)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(jarak>=300){
                            Toast.makeText(getActivity(), "Too far away from your current location!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ((PostActivity) getActivity()).setSelectedLocation(selectedMarker);
                        }
                    }
                });
            }
        });

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(rootActivity==ACTIVITY_HOME)
            ((HomeActivity)getActivity()).setCaptureVisible(View.VISIBLE);
        gps.stopUsingGPS();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    public void getMarkers() {

        String api_key=Preferences.getStringPreferences("api_key",getActivity());
        Call<List<Marker>> call = APIService.service.getMarkerDetails(api_key);

        call.enqueue(new Callback<List<Marker>>() {
            @Override
            public void onResponse(Call<List<Marker>> call, Response<List<Marker>> response) {

                try {

                    Markers = response.body();
                    listMarkers = new ArrayList<com.google.android.gms.maps.model.Marker>();
                    int i = 0;
                    for(Marker marker : Markers){
                        LatLng point= marker.getLocation();
                        com.google.android.gms.maps.model.Marker mark = googleMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title(marker.getLocationId())
                                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapIcon(marker.getType())))
                        );
                        listMarkers.add(mark);
                    }

                    addLocation.setVisibility(View.VISIBLE);
                    ((TextView)rootView.findViewById(R.id.add_location_popup)).setVisibility(View.VISIBLE);
                    isLoaded = true;
                    hidePopUp();

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
    private Bitmap getBitmapIcon(String type){
        BitmapDrawable bitmapdraw= null;
        int icon_id= R.drawable.m_taman;;
        if(type.equals("M0"))
            icon_id = R.drawable.m_tambah;
        if(type.equals("M1"))
            icon_id = R.drawable.m_taman;
        else if(type.equals("M2"))
            icon_id = R.drawable.m_gunung;
        else if(type.equals("M3"))
            icon_id = R.drawable.m_pantai;
        else if(type.equals("M4"))
            icon_id = R.drawable.m_bangunan;
        else if(type.equals("M5"))
            icon_id = R.drawable.m_hutan;
        else if(type.equals("M6"))
            icon_id = R.drawable.m_pasar;
        else if(type.equals("M7"))
            icon_id = R.drawable.m_budaya;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bitmapdraw = (BitmapDrawable)getResources().getDrawable(icon_id,getActivity().getTheme());
        }
        else{
            bitmapdraw = (BitmapDrawable)getResources().getDrawable(icon_id);
        }
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, (screenHeight*84/1920), (screenHeight*150/1920), false);
        return smallMarker;
    }
    void changeMarkerIconList(int visibility){
        ((RecyclerView)rootView.findViewById(R.id.list_marker)).setVisibility(visibility);
    }
    void setMarkerTypeVisibility(String typeID, boolean visibility){
        for (int i=0; i < Markers.size();i++){
            if(Markers.get(i).getType().equals(typeID))
                listMarkers.get(i).setVisible(visibility);
        }
    }

    private String getLocationDistance(Location myLocation, Location markerLocation){
        String distance="";
        jarak = myLocation.distanceTo(markerLocation);
        double jarak2;
        if(jarak>1000000)
            distance="Too far away";
        else if(jarak>=200000) {
            jarak2 = (int)(jarak / 1000) * 100;
            distance = "More than " + jarak2 + "km";
        }
        else if(jarak>=1000 && jarak <200000) {
            jarak2 = (int)(jarak / 1000);
            distance = "About " + jarak2 + " km";
        }
        else{
            distance="About "+(int)jarak+" M";
        }
        return distance;
    }

    private void hidePopUp(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView)rootView.findViewById(R.id.add_location_popup)).setVisibility(View.GONE);
            }
        }, 3000);
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
