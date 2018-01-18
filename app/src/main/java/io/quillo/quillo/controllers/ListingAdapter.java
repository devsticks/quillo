package io.quillo.quillo.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import io.quillo.quillo.R;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.views.ListingCell;

/**
 * Created by Stickells on 18/01/2018.
 */

public class ListingAdapter extends RecyclerView.Adapter<ListingCell> {

    private List<Listing> listings;
    private Context context;
    private boolean isViewingOwnListings;
    private ListingCellListener listingCellListener;

    public ListingAdapter (ListingCellListener listingCellListener, Context context) {
        this.listingCellListener = listingCellListener;
        this.context = context;
        listings = new ArrayList<Listing>();
    }

    @Override
    public ListingCell onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_cell, parent, false);
        return new ListingCell(v);

    }

    @Override
    public void onBindViewHolder(ListingCell holder, int position) {
        Listing listing = listings.get(position);
        holder.setListing(listing);
        holder.setListingCellListener(listingCellListener);
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public void addListing(Listing listing) {
        listings.add(listing);
        int endOfList = listings.size() - 1;
        this.notifyItemInserted(endOfList);
    }

    // LISTINGCELL STUFF MOVED FROM HERE TO OWN CLASS, LISTING CELL

}
