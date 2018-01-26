package io.quillo.quillo.interfaces;

import io.quillo.quillo.data.Listing;

/**
 * Created by Stickells on 13/01/2018.
 */

public interface ListingsListener {

    void onListingLoaded(Listing listing);

    void onListingUpdated(Listing listing);

    void onListingRemoved(Listing listing);

}
