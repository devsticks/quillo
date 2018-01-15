package io.quillo.quillo.handlers;

import android.view.View;

import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.views.ProfileActivity;

/**
 * Created by Stickells on 13/01/2018.
 */

public class ProfileController {

    private Listing temporaryListing;
    private int temporaryListingPosition;

    private ProfileActivity view;
    private Database database;

    public ProfileController(ProfileActivity view, Database database) {
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

        view.onSellerListingLoaded(newListing);
    }

    public void handleListingSwiped(int position, Listing listing) {
        database.deleteListing (listing);
        view.deleteListingCellAt(position);

        temporaryListing = listing;
        temporaryListingPosition = position;

        view.showUndoSnackBar();
    }

    public void handleUndoDeleteConfirmed() {
        if (temporaryListing != null) {
            database.insertListing(temporaryListing);
            view.insertListingCellAt(temporaryListingPosition, temporaryListing);

            temporaryListing = null;
            temporaryListingPosition = 0;
        }
    }

    public void handleSnackbarTimeout() {

    }
}
