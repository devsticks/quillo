package io.quillo.quillo.interfaces;

import io.quillo.quillo.data.Listing;

/**
 * Created by shkla on 2018/02/06.
 */

public interface OneTimeListingListener {

    public void onListingLoaded(Listing listing);
    public void onListingLoadFail();
}
