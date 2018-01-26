package io.quillo.quillo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.ListingsListener;

/**
 * Created by shkla on 2018/01/22.
 *
 *
 */

public class SearchFragment extends Fragment implements ListingsListener, ListingCellListener {


    public static SearchFragment newInstance(){
        SearchFragment searchFragment = new SearchFragment();
        return  searchFragment;
    }

    private RecyclerView recyclerView;
    private ListingAdapter adapter;

    private QuilloDatabase quilloDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        adapter = new ListingAdapter(this, getContext());
        quilloDatabase = new QuilloDatabase();
        quilloDatabase.setListingsListener(this);
        quilloDatabase.queryListings("");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_search, container, false);
        setUpView(view);
        return view;

    }

    //TODO The majority of this code and functionality is duplicated in ProfileActivity, fix up.

    public void startListingDetailActivity(Listing listing) {
        //TODO Update nav to use fragments
        /*Intent intent = new Intent(this, ListingDetailActivity.class);
        intent.putExtra(IntentExtras.EXTRA_LISTING, listing);

        startActivity(intent);*/
    }

    public void setUpView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_home_search_listing_holder);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_white));
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onListingLoaded(Listing newListing) {
        adapter.addListing(newListing);
    }

    @Override
    public void onListingUpdated(Listing listing) {
        adapter.updateListing(listing);
    }

    @Override
    public void onBookmarkClick(Listing listing) {
        quilloDatabase.addBookmark(listing);
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        quilloDatabase.removeBookmark(listing);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity() ).showNavbar();
    }

    @Override
    public void onListingClick(Listing listing) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(IntentExtras.EXTRA_LISTING, listing);

        ListingDetailFragment listingDetailFragment = new ListingDetailFragment();
        listingDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, listingDetailFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("Search Fragment")
                    .commit();

        ((MainActivity)getActivity()).hideNavBar();

    }

}
