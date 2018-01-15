package io.quillo.quillo.handlers;

import android.view.View;

import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.listingsListener;
import io.quillo.quillo.views.HomeSearchActivity;

/**
 * Created by Stickells on 13/01/2018.
 */

public class HomeSearchController {

    private HomeSearchActivity view;
    private Database database;

    public HomeSearchController(HomeSearchActivity view, Database database) {
        this.view = view;
        this.database = database;

        getListFromDataSource();
    }

    public void getListFromDataSource() {
        view.setUpRecyclerAdapterAndView(
                database.getListings()
        );
    }

    public void handleListingCellClick(Listing listing, View viewRoot) {
        view.startListingDetailActivity(listing, viewRoot);
    }

    public void createNewListing() {
        Listing newListing = database.createNewListing();

        view.onListingAdded(newListing);
    }

}
