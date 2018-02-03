package io.quillo.quillo.utils;

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
        public void onMoreListingsLoaded(List<Listing> listings);
    }

    private ListingLoaderListener listingLoaderListener;
    private QuilloDatabase database;
    private List<Listing> listings;

    public ListingLoader(ListingLoaderListener listingLoaderListener, QuilloDatabase database){
        this.listingLoaderListener = listingLoaderListener;
        this.database = database;
    }

    public void loadListings(final String[] listingUids){
        for (int index  = 0; index < listingUids.length; index++) {
            database.loadListing(listingUids[index], new ListingsListener() {
                @Override
                public void onListingLoaded(Listing listing) {
                    listings.add(listing);
                    if (listingUids.length == listings.size()) {
                        listingLoaderListener.onListingsLoaded(listings);
                    }
                }

                @Override
                public void onListingUpdated(Listing listing) {

                }

                @Override
                public void onListingRemoved(Listing listing) {

                }
            });
        }

    }

    public void loadMoreListings(final String[] listingUids){
        for (int index  = 0; index < listingUids.length; index++) {
            database.loadListing(listingUids[index], new ListingsListener() {
                @Override
                public void onListingLoaded(Listing listing) {
                    listings.add(listing);
                    if (listingUids.length == listings.size()) {
                        listingLoaderListener.onMoreListingsLoaded(listings);
                    }
                }

                @Override
                public void onListingUpdated(Listing listing) {

                }

                @Override
                public void onListingRemoved(Listing listing) {

                }
            });
        }
    }

}
