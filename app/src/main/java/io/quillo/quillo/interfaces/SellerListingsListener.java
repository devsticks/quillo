package io.quillo.quillo.interfaces;

import io.quillo.quillo.data.Listing;

/**
 * Created by shkla on 2018/01/21.
 */

public interface SellerListingsListener {

    void onSellerListingLoaded(Listing listing);

    void onSellerListingUpdated(Listing listing);

}
