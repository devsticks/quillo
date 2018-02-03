package io.quillo.quillo.controllers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.quillo.quillo.R;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.OnLoadMoreListener;
import io.quillo.quillo.views.ListingCell;
import io.quillo.quillo.views.LoadingViewHolder;

/**
 * Created by Stickells on 18/01/2018.
 */

public class ListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<Listing> listings;
    private Context context;
    private boolean isViewingOwnListings;
    private ListingCellListener listingCellListener;
    //dis 4 srch #its way too late
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView recyclerView;

    // page-my-nate
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public ListingAdapter(ListingCellListener listingCellListener, Context context, boolean isViewingOwnListings) {
        this.listingCellListener = listingCellListener;
        this.context = context;
        this.isViewingOwnListings = isViewingOwnListings;
        listings = new ArrayList<Listing>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_data, parent, false);
            return new LoadingViewHolder(v);
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_cell, parent, false);
            return new ListingCell(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListingCell) {
            ListingCell mHolder = (ListingCell)holder;

            Listing listing = listings.get(position);
            mHolder.setListing(listing);

            if(isViewingOwnListings){
                mHolder.hideBookmark();
            }

            mHolder.setListingCellListener(listingCellListener);
            if (listing.getImageUrl() != null) {
                Glide.with(context).load(listing.getImageUrl()).into(mHolder.getIcon());
            }
        }
        else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
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

    public void setListings(ArrayList<Listing> listings){
        this.listings = listings;
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return listings.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void addOnScroll(RecyclerView rcv){
        recyclerView = rcv;

//        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }


    // LISTINGCELL STUFF MOVED FROM HERE TO OWN CLASS, LISTING CELL

}
