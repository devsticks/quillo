package io.quillo.quillo.interfaces;

import io.quillo.quillo.data.Listing;

/**
 * Created by shkla on 2018/01/21.
 */

public interface PersonListingsListener {

    void onPersonListingLoaded(Listing listing);

    void onPersonListingUpdated(Listing listing);

    void onPersonListingRemoved(Listing listing);

}
