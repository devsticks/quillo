package io.quillo.quillo.interfaces;

import io.quillo.quillo.data.Listing;

/**
 * Created by Stickells on 18/01/2018.
 */

public interface ListingCellListener {
    void onBookmarkClick(Listing listing);
    void onUnBookmarkClick(Listing listing);
    void onListingClick(Listing listing);
}
