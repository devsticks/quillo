package io.quillo.quillo.interfaces;

import android.view.View;

import java.util.List;

import io.quillo.quillo.data.Listing;

/**
 * Created by Stickells on 15/01/2018.
 */

public interface sellerListener {

    void startListingDetailActivity(Listing listing, View viewRoot);

    void setUpRecyclerAdapterAndView (List<Listing> listOfData);

    void onSellerListingLoaded(Listing listing);

    void deleteListingCellAt(int position);

    void showUndoSnackBar();

    void insertListingCellAt(int position, Listing listItem);

}
