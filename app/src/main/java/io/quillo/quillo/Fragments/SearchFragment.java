package io.quillo.quillo.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.ListingsListener;
import io.quillo.quillo.views.LandingActivity;

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
    private android.support.v7.widget.Toolbar toolbar;

    private CustomFirebaseDatabase customFirebaseDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        adapter = new ListingAdapter(this, getContext());
        customFirebaseDatabase = new CustomFirebaseDatabase();
        customFirebaseDatabase.setListingsListener(this);
        customFirebaseDatabase.queryListings("");

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_search, container, false);

        toolbar = ((AppCompatActivity)getActivity()).findViewById(R.id.tlb_home_search);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        setUpView(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_overflow_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.landing: {
                Intent intent = new Intent(getActivity(), LandingActivity.class);
                startActivity(intent);
                break;
            }
//            case R.id.my_listings: {
//                Person me = new Person("1", "Dev", "sticks@gmail.com", "08321234");
//                Intent intent = new Intent(this, ProfileActivity.class);
//                intent.putExtra(IntentExtras.EXTRA_SELLER, me);
//                startActivity(intent);
//                break;
//            }
//            case R.id.signup_login: {
//                Intent intent = new Intent(this, SignUpLoginActivity.class);
//                startActivity(intent);
//                break;
//            }
        }
        return false;
    }

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
        customFirebaseDatabase.addBookmark(listing);
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        customFirebaseDatabase.removeBookmark(listing);
    }

    @Override
    public void onListingClick(Listing listing) {
        startListingDetailActivity(listing);
    }

}
