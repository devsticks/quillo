package io.quillo.quillo.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

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

    public ListingAdapter(ListingCellListener listingCellListener, Context context, boolean isViewingOwnListings) {
        this.listingCellListener = listingCellListener;
        this.context = context;
        this.isViewingOwnListings = isViewingOwnListings;
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

        if (isViewingOwnListings) {
            holder.hideBookmark();
        }

        holder.setListingCellListener(listingCellListener);
        if (listing.getImageUrl() != null) {
            Glide.with(context).load(listing.getImageUrl()).into(holder.getIcon());
        }
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

    public void addListings(List<Listing> listings){
        for (int i = 0; i < listings.size(); i++){
            addListing(listings.get(i));
        }
    }

    public void removeListing(int position) {
        listings.remove(position);
        this.notifyItemRemoved(position);
    }
    public void removeAllListings(){
        listings.clear();
        notifyDataSetChanged();
    }

    public void removeListing(String listingUid){
        int pos = indexOfListing(listingUid);
        if (pos != -1){
            listings.remove(pos);
            this.notifyItemRemoved(pos);
        }
    }

    private int indexOfListing(String listingUid){
        for (int i = 0; i < listings.size(); i++){
            if(listings.get(i).getUid().equals(listingUid)){
                return i;
            }
        }
        return -1;
    }

    public void insertListing(int position, Listing listing) {
        listings.add(position, listing);
        this.notifyItemInserted(position);
    }

    public void setListings(List<Listing> listings){
        this.listings = listings;
        notifyDataSetChanged();
    }

    public void updateListing(Listing listing) {
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).getUid().equals(listing.getUid())) {
                listings.set(i, listing);
                this.notifyItemChanged(i);
                break;
            }
        }
    }

    public List<Listing> getListings() {
        return listings;
    }

    // LISTINGCELL STUFF MOVED FROM HERE TO OWN CLASS, LISTING CELL

}
