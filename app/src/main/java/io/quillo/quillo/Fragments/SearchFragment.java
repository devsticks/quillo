package io.quillo.quillo.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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

public class SearchFragment extends Fragment implements ListingCellListener, SearchView.OnQueryTextListener{


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static SearchFragment newInstance(){
        SearchFragment searchFragment = new SearchFragment();
        return  searchFragment;
    }

    private ListingAdapter adapter;
//    private ProgressBar spinner;

    // Search vars
    private String mElasticSearchPassword;
    private ArrayList<Listing> searchListings;
    private String universityUid;
    private int searchPage = 0;
    private int searchListingsPerPage = 12;
    private boolean mIsLoading;
    private String savedSearchText;



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
        universityUid = getString(R.string.shared_pref_university_key);
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
                        searchListings.remove(searchListings.size() - 1);
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
        final String universityUid = sharedPreferences.getString(getString(R.string.shared_pref_university_key), null);

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

//        elasticSearchQuery("");

        ((MainActivity)getActivity()).quilloDatabase.observeListings(universityUid, new ListingsListener() {
            @Override
            public void onListingLoaded(Listing listing) {
                adapter.addListing(listing);
                Log.i(SearchFragment.class.getName(), "Listing added: " + listing.getUid());
            }

            @Override
            public void onListingUpdated(Listing listing) {

            }

            @Override
            public void onListingRemoved(Listing listing) {

            }
        });
    }

    private void clearDatabase(){
        Log.d(SearchFragment.class.getName(), "Detaching listing event listener");
        ((MainActivity)getActivity()).quilloDatabase.stopObservingListings();
    }


    public void setUpView(View view) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

//        spinner = (ProgressBar)view.findViewById(R.id.indeterminateBar);
//        spinner.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_white));
        recyclerView.addItemDecoration(itemDecoration);

//        spinner.setVisibility(View.VISIBLE);
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
        ((MainActivity) getActivity() ).showBottomNavbar();
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

    // This method implements the elastic search functionality on text change
    @Override
    public boolean onQueryTextChange(String searchText) {
        this.savedSearchText = searchText;
        searchPage = 0;

        elasticSearchQuery(searchText);

//        if (!searchText.equals("")) {
//            elasticSearchQuery(searchText);
//        }
//        else{
//            setupDatabase();
//            return false;
//        }

        if (searchListings.size() != 0) {
            return true;
        }
        return false;
    }

    public void elasticSearchQuery(final String searchText){ //, final DataListener dataListener
        mIsLoading = true;

        if (searchPage == 0) {
            adapter.removeAllListings();
        }
        searchPage = 0;
        
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
                0, searchListingsPerPage, searchString);

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
                        searchListings.add(l);
                        ((MainActivity)getActivity()).quilloDatabase.loadListingImageData(l);
                    }

                    //set the listings into the adapter
                    if (searchPage == 0) {
                        Log.d("a", "setting listings" + searchListings.toString());
                        adapter.setListings(searchListings);
                    }
                    else{
                        adapter.addListings(searchListings);
                    }

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
        searchPage++;
        mIsLoading = false;
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
