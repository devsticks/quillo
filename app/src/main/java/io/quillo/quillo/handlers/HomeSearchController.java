package io.quillo.quillo.handlers;

import android.view.View;

import java.util.List;

import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.views.HomeSearchActivity;

/**
 * Created by Stickells on 13/01/2018.
 */

public class HomeSearchController {

    private HomeSearchActivity view;
    private Database database;
    private List<Listing> listings;

    public HomeSearchController(HomeSearchActivity view, Database database) {
        this.view = view;
        this.database = database;

        getListingsFromDatabase();
    }

    public List<Listing> getListings () {
        return listings;
    }

    public void getListingsFromDatabase() {
        listings = database.getListings();
    }

    public void handleListingCellClick(Listing listing, View viewRoot) {
        view.startListingDetailActivity(listing, viewRoot);
    }

}
