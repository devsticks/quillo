package io.quillo.quillo.data;

import java.util.List;

/**
 * Created by Stickells on 13/01/2018.
 */

public interface DatabaseListener {

    List<Listing> getListings();

    Listing getListing(String listingId);

    Listing createNewListing();

    void deleteListing(Listing listing);

    void insertListing(Listing listing);
}
