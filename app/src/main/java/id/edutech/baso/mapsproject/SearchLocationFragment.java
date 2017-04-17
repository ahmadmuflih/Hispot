
package id.edutech.baso.mapsproject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.Location;

/**
 * Created by Baso on 10/8/2016.
 */
public class SearchLocationFragment extends Fragment{
    View view;
    ArrayList<Location> location;
    LocationAdapter adapter;
    public SearchLocationFragment() {
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
        view = inflater.inflate(R.layout.fragment_search_location, container, false);
        return view;
    }
    public void searchResult(ArrayList<Location> searchResult){
        location = searchResult;
        if(adapter!=null){
            adapter.setEmpty();
        }
        if(location.size()>0) {
            adapter = new LocationAdapter(getActivity().getApplicationContext(), location);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.search_location_results);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setOnLocationSelectedListener(new LocationAdapter.OnLocationSelectedListener() {
                @Override
                public void onSelected(Location location) {
                    Intent intent = new Intent(getActivity(),LocationActivity.class);
                    intent.putExtra("locationId",""+location.getLocationId());
                    intent.putExtra("locationName",location.getLocationName());
                    startActivity(intent);
                }
            });
            getActivity().findViewById(R.id.no_location_results).setVisibility(View.GONE);
            getActivity().findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
        }
        else{
            getActivity().findViewById(R.id.no_location_results).setVisibility(View.VISIBLE);
        }
        getActivity().findViewById(R.id.searching_location).setVisibility(View.GONE);

    }
}
