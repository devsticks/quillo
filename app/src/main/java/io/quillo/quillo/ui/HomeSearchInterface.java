package io.quillo.quillo.ui;

import android.view.View;

import io.quillo.quillo.data.Listing;

import java.util.List;

/**
 * Created by Stickells on 13/01/2018.
 */

public interface HomeSearchInterface {

    void startListingDetailActivity(String textbookName, String textbookDescription, int colorResource, View viewRoot);

    void setUpAdapterAndView (List<Listing> listOfData);

    void addNewListingToView(Listing newListing);

    void deleteListingCellAt(int position);

    void showUndoSnackBar();

    void insertListingCellAt(int position, Listing listItem);
}
