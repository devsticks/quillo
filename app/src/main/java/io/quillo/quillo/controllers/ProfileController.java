package io.quillo.quillo.controllers;

import android.view.View;

import java.util.List;

import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.views.ProfileActivity;

/**
 * Created by Stickells on 13/01/2018.
 */

public class ProfileController {

    private Listing temporaryListing;
    private int temporaryListingPosition;

    private ProfileActivity view;
    private CustomFirebaseDatabase customFirebaseDatabase;
    private List<Listing> listings;

    public ProfileController(ProfileActivity view, CustomFirebaseDatabase customFirebaseDatabase) {
        this.view = view;
        this.customFirebaseDatabase = customFirebaseDatabase;

        getListingsFromDatabase();
    }

    public void getListingsFromDatabase() {
        //listings = customFirebaseDatabase.getListings();
    }

    public List<Listing> getListings() {
        return listings;
    }

    public void handleListingCellClick(Listing listing, View viewRoot) {
        view.startListingDetailActivity(listing, viewRoot);
    }

    public void createNewListing() {


//        Listing newListing = customFirebaseDatabase.createNewListing();
//
//        view.onSellerListingLoaded(newListing);
    }

    public void handleListingSwiped(int position, Listing listing) {
        customFirebaseDatabase.deleteListing (listing);
        view.deleteListingCellAt(position);

        temporaryListing = listing;
        temporaryListingPosition = position;

        view.showUndoSnackBar();
    }

    public void handleUndoDeleteConfirmed() {
        if (temporaryListing != null) {
            customFirebaseDatabase.insertListing(temporaryListing);
            view.insertListingCellAt(temporaryListingPosition, temporaryListing);

            temporaryListing = null;
            temporaryListingPosition = 0;
        }
    }

    public void handleSnackbarTimeout() {

    }
}
