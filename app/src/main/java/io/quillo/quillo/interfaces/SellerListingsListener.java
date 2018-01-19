package io.quillo.quillo.interfaces;

import android.view.View;

import java.util.List;

import io.quillo.quillo.data.Listing;

/**
 * Created by Stickells on 15/01/2018.
 */

public interface SellerListingsListener {

    void onSellerListingLoaded(Listing listing);

}
