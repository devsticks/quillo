package io.quillo.quillo.logic;

import android.view.View;

import io.quillo.quillo.data.DatabaseListener;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.ui.HomeSearchInterface;

/**
 * Created by Stickells on 13/01/2018.
 */

public class Controller {

    private Listing temporaryListing;
    private int temporaryListingPosition;

    private HomeSearchInterface view;
    private DatabaseListener database;

    public Controller(HomeSearchInterface view, DatabaseListener database) {
        this.view = view;
        this.database = database;

        getListFromDataSource();
    }

    public void getListFromDataSource() {
        view.setUpAdapterAndView(
                database.getListings()
        );
    }

    public void onListingCellClick(Listing listing, View viewRoot) {
        view.startListingDetailActivity(listing.getName(), listing.getDescription(), listing.getColorResource(), viewRoot);
    }

    public void createNewListing() {
        Listing newListing = database.createNewListing();

        view.addNewListingToView(newListing);
    }

    public void onListingSwiped(int position, Listing listing) {
        database.deleteListing (listing);
        view.deleteListingCellAt(position);

        temporaryListing = listing;
        temporaryListingPosition = position;

        view.showUndoSnackBar();
    }

    public void onUndoConfirmed() {
        if (temporaryListing != null) {
            database.insertListing(temporaryListing);
            view.insertListingCellAt(temporaryListingPosition, temporaryListing);

            temporaryListing = null;
            temporaryListingPosition = 0;
        }
    }

    public void onSnackbarTimeout() {

    }
}
