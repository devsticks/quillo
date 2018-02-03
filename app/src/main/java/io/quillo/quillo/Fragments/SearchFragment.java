package io.quillo.quillo.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.DataListener;
import io.quillo.quillo.interfaces.ElasticSearchAPI;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.ListingsListener;
import io.quillo.quillo.interfaces.OnLoadMoreListener;
import io.quillo.quillo.utils.HitsList;
import io.quillo.quillo.utils.HitsObject;
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

public class SearchFragment extends Fragment implements ListingCellListener, MaterialSearchView.OnQueryTextListener{


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static SearchFragment newInstance(){
        SearchFragment searchFragment = new SearchFragment();
        return  searchFragment;
    }

    private ListingAdapter adapter;
//    private ProgressBar spinner;

    // Search vars
    MaterialSearchView searchView;
    private String mElasticSearchPassword;
    private ArrayList<Listing> searchListings;
    private String universityUid;
    private int searchPage = 0;
    private int searchListingsPerPage = 12;
    private boolean mIsLoading;
    private String savedSearchText;
    private ListingAdapter searchAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ListingAdapter(this, getContext(), false);
        setHasOptionsMenu(true);

        setupDatabase();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_search, container, false);

        ButterKnife.bind(this, view);
        setUpView(view);
        savedSearchText = "";

        adapter.addOnScroll(recyclerView);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override public void onLoadMore() {
                Log.e("haint", "Load More");

                //Add the loading spinner
                searchListings.add(null);
                adapter.notifyItemInserted(searchListings.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading spinner
                        adapter.removeListing(searchListings.size() - 1);
                        adapter.notifyItemRemoved(searchListings.size());

                        if (adapter.getListings().size() == 0){
                            searchPage = 0;
                        }
                        elasticSearchQuery(savedSearchText);

                        adapter.notifyDataSetChanged();
                        adapter.setLoaded(); /////wahhhaayayyaay
                    }
                }, 5000);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDatabase();
    }

    private void setupDatabase(){
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        universityUid = sharedPreferences.getString(getString(R.string.shared_pref_university_key), null);

        mElasticSearchPassword = ((MainActivity)getActivity()).quilloDatabase.getElasticSearchPassword(new DataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot data) {
                DataSnapshot singleSnapshot = data.getChildren().iterator().next();
                mElasticSearchPassword = singleSnapshot.getValue().toString();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

        elasticSearchQuery("");

//        ((MainActivity)getActivity()).quilloDatabase.observeListings(universityUid, new ListingsListener() {
//            @Override
//            public void onListingLoaded(Listing listing) {
//                adapter.addListing(listing);
//                Log.i(SearchFragment.class.getName(), "Listing added: " + listing.getUid());
//            }
//
//            @Override
//            public void onListingUpdated(Listing listing) {
//
//            }
//
//            @Override
//            public void onListingRemoved(Listing listing) {
//
//            }
//        });
    }

    private void clearDatabase(){
        Log.d(SearchFragment.class.getName(), "Detaching listing event listener");
        ((MainActivity)getActivity()).quilloDatabase.stopObservingListings();
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
        savedSearchText = "";
    }

    @Override
    public void onListingClick(Listing listing) {

        ((MainActivity)getActivity()).showListingDetailFragment(listing);
    }

    @Override
    public boolean onQueryTextSubmit(String searchText) {
        return onQueryTextChange(searchText);
    }

    @Override
    public boolean onQueryTextChange(String searchText) {
        savedSearchText = searchText;
        searchPage = 0;

        if (searchText != null && !searchText.isEmpty()) {
            elasticSearchQuery(searchText);
        }
        else{
            recyclerView.setAdapter(adapter);
            return false;
        }

        if (searchListings.size() != 0) {
            return true;
        }
        return false;
    }

    public void elasticSearchQuery(final String searchText){ //, final DataListener dataListener
//        if (searchPage == 0) {
////            adapter.removeAllListings();
//            searchAdapter = new ListingAdapter(this, getContext(), false);
//        }
        
        searchListings = new ArrayList<Listing>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ElasticSearchAPI searchAPI = retrofit.create(ElasticSearchAPI.class);

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials
                .basic(getString(R.string.elastic_search_username), mElasticSearchPassword));

        String searchString = "";

        // Add other parameters here in future
        searchString = searchString + searchText + "*";

        if (universityUid != null) {
            searchString = searchString + " universityUid:" + universityUid;
        }

        Call<HitsObject> call = searchAPI.search(headerMap, "AND",
                searchPage, searchListingsPerPage, searchString);

        call.enqueue(new Callback<HitsObject>() {
            @Override
            public void onResponse(Call<HitsObject> call, Response<HitsObject> response) {
                Log.d("response", "onResponse: server response: " + response.toString());

                HitsList hitsList = new HitsList();
                String jsonResponse = "";
                try {
                    if (response.isSuccessful()) {
                        hitsList = response.body().getHits();
                    } else {
                        jsonResponse = response.errorBody().string();
                    }

                    for (int i = 0; i < hitsList.getListingIndex().size(); i++) {
                        Log.d("a", hitsList.getListingIndex().get(i).getListing().getDescription());
                        Listing l = hitsList.getListingIndex().get(i).getListing();
                        l.setImageUrl(null);
                        searchListings.add(l);
                        ((MainActivity)getActivity()).quilloDatabase.loadListingImageData(l);
                    }

                    Log.d("Listings", searchListings.toString());

                    //set the listings into the adapter
                    Log.d("pageNum", String.valueOf(searchPage));
                    if (searchPage == 0) {
                        Log.d("setting listings", searchListings.toString());
                        adapter.setListings(searchListings);
                        recyclerView.setAdapter(adapter);
                    }
                    else{
                        Log.d("adding listings", searchListings.toString());
                        adapter.addListings(searchListings);
                    }

                    searchPage++;

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
