package io.quillo.quillo.interfaces;

import io.quillo.quillo.data.Listing;

/**
 * Created by shkla on 2018/01/26.
 */

public interface BookmarkListener {

    public void onBookmarkAdded(Listing listing);
    public void onBookmarkRemoved(String listingUid);

}
