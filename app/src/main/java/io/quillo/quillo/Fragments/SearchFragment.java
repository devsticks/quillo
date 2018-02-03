package io.quillo.quillo.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.ElasticSearchAPI;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.PasswordListener;
import io.quillo.quillo.utils.HitsList;
import io.quillo.quillo.utils.HitsObject;
import io.quillo.quillo.utils.ListingLoader;
import io.quillo.quillo.utils.ListingSource;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shkla, Tumiel on 2018/01/22.
 *
 *
 */

public class SearchFragment extends Fragment implements ListingCellListener, SearchView.OnQueryTextListener{


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static SearchFragment newInstance(){
        SearchFragment searchFragment = new SearchFragment();
        return  searchFragment;
    }

    private ListingAdapter adapter;

    // Search vars
    private String mElasticSearchPassword;
    private ArrayList<Listing> searchListings;
    private String universityUid;
    private int searchPage = 0;
    private int searchListingsPerPage = 20;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ListingAdapter(this, getContext(), false);
        setHasOptionsMenu(true);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_search, container, false);
        ButterKnife.bind(this, view);
        setUpView(view);
        onQueryTextChange("");

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        clearDatabase();
    }

    private void clearDatabase(){
        Log.d(SearchFragment.class.getName(), "Detaching listing event listener");
        adapter.removeAllListings();
    }


    public void setUpView(View view) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_white));
        recyclerView.addItemDecoration(itemDecoration);
    }



    @Override
    public void onBookmarkClick(Listing listing) {
        ((MainActivity)getActivity()).quilloDatabase.addBookmark(listing);
        Toast.makeText(getContext(), "Added to bookmarks", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        ((MainActivity)getActivity()).quilloDatabase.removeBookmark(listing);
        Toast.makeText(getContext(), "Removed from bookmarks", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity() ).showBottomNavBar();
    }

    @Override
    public void onListingClick(Listing listing) {

        ((MainActivity)getActivity()).showListingDetailFragment(listing);
    }

    @Override
    public boolean onQueryTextSubmit(String searchText) {
        return onQueryTextChange(searchText);
    }

    // This method implements the elastic search functionality on text change
    @Override
    public boolean onQueryTextChange(String searchText) {
        searchPage = 0;
        if (searchText == null){
            searchText = "";
        }
        getHitsFromElasticSearchQuery(searchText, new HitsLoadedListener() {
            @Override
            public void ListingUidsLoaded(ArrayList<String> listingUids) {
                if (listingUids.isEmpty()){
                    //No listings match search
                    adapter.removeAllListings();
                }else {

                    ListingLoader listingLoader = new ListingLoader(((MainActivity) getActivity()).quilloDatabase, new ListingLoader.ListingLoaderListener() {
                        @Override
                        public void onListingsLoaded(List<Listing> listings) {
                            adapter.setListings(listings);
                        }
                    });
                    listingLoader.loadListings(listingUids);
                }
            }
        });
        return true;
    }

    interface HitsLoadedListener{
        public void ListingUidsLoaded(ArrayList<String> listingUids);
    }

    public void getHitsFromElasticSearchQuery(final String searchText, final HitsLoadedListener hitsLoadedListener){
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        universityUid = sharedPreferences.getString(getString(R.string.shared_pref_university_key), null);

        ((MainActivity)getActivity()).quilloDatabase.getElasticSearchPassword(new PasswordListener() {
            @Override
            public void onPasswordLoaded(String password) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ElasticSearchAPI searchAPI = retrofit.create(ElasticSearchAPI.class);

                HashMap<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Authorization", Credentials
                        .basic(getString(R.string.elastic_search_username), password));

                String searchString = "";

                // Add other parameters here in future
                searchString = searchString + searchText + "*";

                if (universityUid != null) {
                    searchString = searchString + " universityUid:" + universityUid;
                }else{
                    return;
                }

                Call<HitsObject> call = searchAPI.search(headerMap, "AND", searchString,
                        searchListingsPerPage,searchPage*searchListingsPerPage);

                call.enqueue(new Callback<HitsObject>() {
                    @Override
                    public void onResponse(Call<HitsObject> call, Response<HitsObject> response) {
                        ArrayList<String> listingUids = new ArrayList<>();
                        Log.d("response", "onResponse: server response: " + response.toString());

                        HitsList hitsList = new HitsList();
                        String jsonResponse = "";
                        try {
                            if (response.isSuccessful()) {
                                hitsList = response.body().getHits();
                            } else {
                                jsonResponse = response.errorBody().string();
                            }

                            for(ListingSource listingSouce: hitsList.getListingIndex()){
                                listingUids.add(listingSouce.getListing().getUid());
                            }


                            hitsLoadedListener.ListingUidsLoaded(listingUids);

                        } catch (NullPointerException e) {
                            Log.e("Error", "onResponse: NullPointerException: " + e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("Error", "onResponse: IndexOutOfBoundsException: " + e.getMessage());
                        } catch (IOException e) {
                            Log.e("Error", "onResponse: IOException: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<HitsObject> call, Throwable t) {
                        Log.e("Error", "onFailure: " + t.getMessage());
                        Toast.makeText(getActivity(), "search failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onPasswordLoadFailed() {

            }
        });

    }

    //TODO: pagination onScroll
//    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            if (mIsLoading) {
//                return;
//            }
//            int visibleItemCount = layoutManager.getChildCount();
//            int totalItemCount = mLayoutManager.getItemCount();
//            int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
//            if (pastVisibleItems + visibleItemCount >= totalItemCount) {
//                //End of list
//            }
//        }
//    };

}
