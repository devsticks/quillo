package io.quillo.quillo.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.QuilloDatabase;
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

    private QuilloDatabase quilloDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        adapter = new ListingAdapter(this, getContext());
        setHasOptionsMenu(true);
        quilloDatabase = new QuilloDatabase();
        quilloDatabase.setListingsListener(this);
        quilloDatabase.queryListings("");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_search, container, false);

        toolbar = ((AppCompatActivity)getActivity()).findViewById(R.id.tlb_home_search);

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
