package io.quillo.quillo.utils;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.quillo.quillo.data.Listing;

/**
 * Created by Tom on 2018/01/28.
 */

@IgnoreExtraProperties
public class ListingSource {

    @SerializedName("_source")
    @Expose
    private Listing listing;


    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }
}
