package id.edutech.baso.mapsproject;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;
import java.util.List;

import models.APIService;
import models.SearchResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    TabLayout tabLayout;
    private ViewPager viewPager;
    SearchResults results;
    Call<SearchResults> searchResultsCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Search");
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SearchPeopleFragment(), "PEOPLE");
        adapter.addFrag(new SearchLocationFragment(), "LOCATION");
        viewPager.setAdapter(adapter);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ViewPagerAdapter pagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
                final SearchPeopleFragment searchPeopleFragment = (SearchPeopleFragment) pagerAdapter.getItem(0);
                final SearchLocationFragment searchLocationFragment = (SearchLocationFragment) pagerAdapter.getItem(1);

                tabLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.searching_people).setVisibility(View.VISIBLE);
                findViewById(R.id.no_people_results).setVisibility(View.GONE);
                findViewById(R.id.searching_location).setVisibility(View.VISIBLE);
                findViewById(R.id.no_location_results).setVisibility(View.GONE);

                viewPager.setVisibility(View.VISIBLE);
                String api_key=Preferences.getStringPreferences("api_key",getApplicationContext());
                searchResultsCall = APIService.service.getSearch(query,api_key);
                searchResultsCall.enqueue(new Callback<SearchResults>() {
                    @Override
                    public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                        if(response.isSuccessful()){
                            results = response.body();
                            if(results!=null && results.getCode()==200) {
                                searchPeopleFragment.searchResult(results.getPeople());
                                searchLocationFragment.searchResult(results.getLocations());
                            }
                        }
                        else{
                            Toast.makeText(SearchActivity.this, "Gagal!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResults> call, Throwable t) {
                        t.printStackTrace();
                        //Toast.makeText(SearchActivity.this, "Ewh!", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
