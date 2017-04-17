
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

import models.Subscribe;

/**
 * Created by Baso on 10/8/2016.
 */
public class SearchPeopleFragment extends Fragment{
    View view;
    ArrayList<Subscribe> people;
    PeopleAdapter adapter;
    public SearchPeopleFragment() {
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
        view = inflater.inflate(R.layout.fragment_search_people, container, false);
        return view;
    }
    public void searchResult(ArrayList<Subscribe> searchResult){
            people = searchResult;

            if(adapter!=null){
                adapter.setEmpty();
            }
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.search_people_results);
            if(people.size()>0) {
                adapter = new PeopleAdapter(getActivity().getApplicationContext(), people);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setOnPeopleSelectedListener(new PeopleAdapter.OnPeopleSelectedListener() {
                    @Override
                    public void onSelected(Subscribe People) {
                        Intent i = new Intent(getActivity().getApplicationContext(),UserActivity.class);
                        i.putExtra("nama",People.getNamaUser());
                        i.putExtra("id",People.getIdUser());
                        i.putExtra("foto",People.getFotoUser());
                        i.putExtra("bio",People.getKeterangan());
                        i.putExtra("status",People.isStatus());
                        i.putExtra("background",People.getBackgroundUser());
                        startActivity(i);
                    }
                });
                getActivity().findViewById(R.id.no_people_results).setVisibility(View.GONE);
                getActivity().findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
            }
            else{
                getActivity().findViewById(R.id.no_people_results).setVisibility(View.VISIBLE);
            }
            getActivity().findViewById(R.id.searching_people).setVisibility(View.GONE);
    }
}
