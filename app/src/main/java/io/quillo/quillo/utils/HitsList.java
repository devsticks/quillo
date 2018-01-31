package io.quillo.quillo.utils;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Tom on 2018/01/28.
 */

@IgnoreExtraProperties
public class HitsList {

    @SerializedName("hits")
    @Expose
    private List<ListingSource> listingIndex;


    public List<ListingSource> getListingIndex() {
        return listingIndex;
    }

    public void setListIndex(List<ListingSource> listingIndex) {
        this.listingIndex = listingIndex;
    }
}
