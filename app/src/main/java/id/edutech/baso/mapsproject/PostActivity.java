package id.edutech.baso.mapsproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import id.edutech.baso.mapsproject.R;

import models.Marker;


public class PostActivity extends AppCompatActivity{

    private final int FRAGMENT_POST = 1;
    private final int FRAGMENT_MAPVIEW = 2;
    Fragment fragmentPost;
    Marker selectedLocation;
    MapViewFragment mapView;
    Menu actionMenu;
    int fragmentActive=1;



    boolean isLocationSelected = false, isPostLoaded=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");
    

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post,menu);
        actionMenu = menu;
        displaySelectedScreen(FRAGMENT_POST);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.post:
                PostFragment postFragment = (PostFragment) fragmentPost;
                postFragment.validate();
                break;
            case R.id.action_expand:
                actionMenu.findItem(R.id.action_close_expand).setVisible(true);
                actionMenu.findItem(R.id.action_expand).setVisible(false);
                mapView.changeMarkerIconList(View.VISIBLE);
                break;
            case R.id.action_close_expand:
                actionMenu.findItem(R.id.action_expand).setVisible(true);
                actionMenu.findItem(R.id.action_close_expand).setVisible(false);
                mapView.changeMarkerIconList(View.GONE);
                break;

            case android.R.id.home:
                    onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(fragmentActive==FRAGMENT_MAPVIEW)
            setSelectedLocation(null);
        else
            super.onBackPressed();
    }

    public void displaySelectedScreen(int id) {
        Fragment fragment = null;
        if (id == FRAGMENT_POST) {
            getSupportActionBar().setTitle("Post");
            if (!isPostLoaded) {
                isPostLoaded = true;
                fragmentPost = new PostFragment();
            }
            actionMenu.findItem(R.id.post).setVisible(true);
            actionMenu.findItem(R.id.action_expand).setVisible(false);
            actionMenu.findItem(R.id.action_close_expand).setVisible(false);
            fragment = fragmentPost;

        } else if (id == FRAGMENT_MAPVIEW) {
            getSupportActionBar().setTitle("Select Location");
            fragment = MapViewFragment.newInstance(2);
            mapView = (MapViewFragment) fragment;
            actionMenu.findItem(R.id.action_expand).setVisible(true);
            actionMenu.findItem(R.id.post).setVisible(false);
        }
        if (fragment!=null){
            fragmentActive=id;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }
    }
    public void setSelectedLocation(Marker location){
        this.selectedLocation = location;
        if(location==null) {
            setLocationSelected(false);
        }
        else {
            setLocationSelected(true);
        }
        displaySelectedScreen(1);
    }
    public void setLocationSelected(boolean locationSelected) {
        isLocationSelected = locationSelected;
    }

    public Marker getSelectedLocation() {
        return selectedLocation;
    }

}
