package io.quillo.quillo.utils;

import java.util.ArrayList;
import java.util.List;

import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.interfaces.ListingsListener;

/**
 * Created by shkla on 2018/02/03.
 */


public class ListingLoader {

    public interface ListingLoaderListener {
        public void onListingsLoaded(List<Listing> listings);

    }

    private ListingLoaderListener listingLoaderListener;
    private QuilloDatabase database;
    private List<Listing> listings;

    public ListingLoader(QuilloDatabase database, ListingLoaderListener listingLoaderListener){
        this.listingLoaderListener = listingLoaderListener;
        this.database = database;
        listings = new ArrayList<>();
    }

    public void loadListings(final ArrayList<String> listingUids){
        ListingsListener listingsListener = new ListingsListener() {
            @Override
            public void onListingLoaded(Listing listing) {
                listings.add(listing);
                if (listingUids.size() == listings.size()) {
                    listingLoaderListener.onListingsLoaded(listings);
                }
            }

            @Override
            public void onListingUpdated(Listing listing) {

            }

            @Override
            public void onListingRemoved(Listing listing) {

            }
        };
        for (String listingUid: listingUids){
            database.loadListing(listingUid, listingsListener);
        }
    }


}
