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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.ListingsListener;

/**
 * Created by shkla on 2018/01/22.
 *
 *
 */

public class SearchFragment extends Fragment implements ListingCellListener {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static SearchFragment newInstance(){
        SearchFragment searchFragment = new SearchFragment();
        return  searchFragment;
    }

    private ListingAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ListingAdapter(this, getContext());
        setHasOptionsMenu(true);
        //TODO: Use users current university
       setupDatabase();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_search, container, false);

        ButterKnife.bind(this, view);
        setUpView(view);
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
        Log.d(SearchFragment.class.getName(), "Detatching listing event listener");
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
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        ((MainActivity)getActivity()).quilloDatabase.removeBookmark(listing);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity() ).showBottomNavbar();
    }

    @Override
    public void onListingClick(Listing listing) {

        ((MainActivity)getActivity()).showListingDetailFragment(listing);
    }

}
