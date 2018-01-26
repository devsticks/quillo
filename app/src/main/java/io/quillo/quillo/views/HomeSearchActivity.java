package io.quillo.quillo.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.ListingsListener;

public class HomeSearchActivity extends AppCompatActivity implements ListingsListener, ListingCellListener {



    private RecyclerView recyclerView;
    private ListingAdapter adapter;
    private android.support.v7.widget.Toolbar toolbar;

    private QuilloDatabase quilloDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_search);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.tlb_home_search);
        setSupportActionBar(toolbar);

        adapter = new ListingAdapter(this, this);

        quilloDatabase = new QuilloDatabase();
        quilloDatabase.setListingsListener(this);
        quilloDatabase.queryListings("");

        setUpView();
    }

    //TODO The majority of this code and functionality is duplicated in ProfileActivity, fix up.

    public void startListingDetailActivity(Listing listing) {
        Intent intent = new Intent(this, ListingDetailActivity.class);
        intent.putExtra(IntentExtras.EXTRA_LISTING, listing);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.splash: {
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.my_listings: {
                Person me = new Person("1", "Dev", "sticks@gmail.com", "08321234");
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(IntentExtras.EXTRA_SELLER, me);
                startActivity(intent);
                break;
            }
            case R.id.signup_login: {
                Intent intent = new Intent(this, SignUpLoginActivity.class);
                startActivity(intent);
                break;
            }
        }
        return false;
    }

    public void setUpView() {
        recyclerView = (RecyclerView) findViewById(R.id.rec_home_search_listing_holder);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(HomeSearchActivity.this, R.drawable.divider_white));
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
    public void onListingClick(Listing listing) {
        startListingDetailActivity(listing);
    }


}
