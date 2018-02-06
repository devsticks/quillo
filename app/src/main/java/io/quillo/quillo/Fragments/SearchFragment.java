package io.quillo.quillo.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import com.google.firebase.auth.FirebaseUser;

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
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.ElasticSearchAPI;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.utils.OnLoadMoreListener;
import io.quillo.quillo.interfaces.PasswordListener;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.utils.FirebaseHelper;
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

public class SearchFragment extends Fragment implements ListingCellListener, MaterialSearchView.OnQueryTextListener{

    private static final String BASE_URL = "http://35.205.236.168//elasticsearch/listings/listing/";


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.loading_state)
    View loadingState;
    @BindView(R.id.empty_state)
    View emptyState;
    @BindView(R.id.error_state)
    View errorState;

    public static SearchFragment newInstance(){
        SearchFragment searchFragment = new SearchFragment();
        return  searchFragment;
    }

    private ListingAdapter adapter;

    // Search vars
    MaterialSearchView searchView;
    private String universityUid;
    private int searchPage = 0;
    private int searchListingsPerPage = 14;
    private String lastSearchText;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar itemProgressBar;

    //state vars
    public final int STATE_NORMAL = 0;
    public final int STATE_LOADING = 1;
    public final int STATE_EMPTY = 2;
    public final int STATE_ERROR = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ListingAdapter(this, getContext(), false);
        setHasOptionsMenu(true);
        checkIfUniversityIsKnown();
    }
    public void checkIfUniversityIsKnown(){
        FirebaseUser currentUser = FirebaseHelper.getCurrentFirebaseUser();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String universityUid = sharedPreferences.getString(getString(R.string.shared_pref_university_key), null);

        if (universityUid == null){
            //Uni is not saved in shared pref
            if(currentUser == null){
                //User is not logged in

                ((MainActivity)getActivity()).showLandingFragment();

            } else {
                //User is logged in get uni from firebase
                ((MainActivity)getActivity()).quilloDatabase.loadPerson(currentUser.getUid(), new PersonListener() {
                    @Override
                    public void onPersonLoaded(Person person) {
                        String personUniversityUid = person.getUniversityUid();
                        ((MainActivity)getActivity()).saveUniversityUidToSharedPrefrences(personUniversityUid);
                    }
                });

            }
        }
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
    public void onStart() {
        ((MainActivity)getActivity()).toolbar.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).toolbar.getBackground().setAlpha(255);
        super.onStart();
    }

    @Override
    public void onStop() {
        if(((MainActivity)getActivity()).toolbar != null) {
            ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDatabase();
    }

    private void clearDatabase(){
        Log.d(SearchFragment.class.getName(), "Detaching listing event listener");
        adapter.removeAllListings();
    }


    public void setUpView(View view) {

        showState(STATE_LOADING);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_white));
        recyclerView.addItemDecoration(itemDecoration);

        itemProgressBar = view.findViewById(R.id.item_progress_bar);
        recyclerView.addOnScrollListener(resetOnScroll());


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onQueryTextChange(lastSearchText);
            }
        });

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

    @Override
    public boolean onQueryTextChange(String searchText) {
        searchPage = 0;

        if (searchText == null){
            searchText = "";
        }
        lastSearchText = searchText;
        getHitsFromElasticSearchQuery(searchText, new HitsLoadedListener() {
            @Override
            public void ListingUidsLoaded(ArrayList<String> listingUids) {
                if (listingUids.isEmpty()){
                    //No listings match search
                    showState(STATE_EMPTY);
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

        if (universityUid == null){
            return;
        }

        ((MainActivity)getActivity()).quilloDatabase.getElasticSearchPassword(new PasswordListener() {
            @Override
            public void onPasswordLoaded(String password) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
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

                Call<HitsObject> call = searchAPI.search(headerMap, "AND",
                        searchPage*searchListingsPerPage, searchListingsPerPage, "price", searchString);

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
                        } finally {
                            onRefreshComplete();
                            showState(STATE_NORMAL);
                        }
                    }

                    @Override
                    public void onFailure(Call<HitsObject> call, Throwable t) {
                        Log.e("Error", "onFailure: " + t.getMessage());
                        Toast.makeText(getActivity(), "search failed", Toast.LENGTH_SHORT).show();
                    }
                });
                searchPage++;
            }

            @Override
            public void onPasswordLoadFailed() {

            }
        });

    }

    private void onRefreshComplete() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showState(int stateNumber){
        switch (stateNumber){
            case STATE_NORMAL:
                recyclerView.setVisibility(View.VISIBLE);
                loadingState.setVisibility(View.GONE);
                emptyState.setVisibility(View.GONE);
                errorState.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                recyclerView.setVisibility(View.GONE);
                loadingState.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
                errorState.setVisibility(View.GONE);
                break;
            case STATE_EMPTY:
                recyclerView.setVisibility(View.GONE);
                loadingState.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                errorState.setVisibility(View.GONE);
                break;
            case STATE_ERROR:
                recyclerView.setVisibility(View.GONE);
                loadingState.setVisibility(View.GONE);
                emptyState.setVisibility(View.GONE);
                errorState.setVisibility(View.VISIBLE);
                break;
        }

    }

    public OnLoadMoreListener resetOnScroll(){
        return new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                itemProgressBar.setVisibility(View.VISIBLE);

                if (lastSearchText == null){
                    itemProgressBar.setVisibility(View.GONE);
                    return;
                }
                getHitsFromElasticSearchQuery(lastSearchText, new HitsLoadedListener() {
                    @Override
                    public void ListingUidsLoaded(ArrayList<String> listingUids) {
                        if (listingUids.isEmpty()){
                            //No listings match search
                            //adapter.removeAllListings();
                        }else {
                            ListingLoader listingLoader = new ListingLoader(((MainActivity) getActivity()).quilloDatabase, new ListingLoader.ListingLoaderListener() {
                                @Override
                                public void onListingsLoaded(List<Listing> listings) {
                                    adapter.addListings(listings);
                                    itemProgressBar.setVisibility(View.GONE);
                                }
                            });
                            listingLoader.loadListings(listingUids);
                        }
                        itemProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        };
    }

}
