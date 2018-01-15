package io.quillo.quillo.ui;

/**
 * Created by Stickells on 14/01/2018.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.quillo.quillo.R;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.logic.Controller;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class ListingRecyclerAdapter extends RecyclerView.Adapter<ListingRecyclerAdapter.ViewHolder> {

    private List<Listing> listOfData;

    private LayoutInflater layoutInflater;
    private RecyclerView listingRecyclerView;
    private ListingRecyclerAdapter adapter;
    private android.support.v7.widget.Toolbar toolbar;

    private Controller controller;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.listing_cell, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Listing currentItem = listOfData.get(position);

        holder.mTextbookIcon.setImageResource(currentItem.getColorResource());
        holder.mTextbookDescription.setText(currentItem.getDescription());
        holder.mTextbookName.setText(currentItem.getName());
        holder.mLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return listOfData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView mTextbookIcon;
        private TextView mTextbookDescription;
        private TextView mTextbookName;
        private ViewGroup mContainer;
        private ProgressBar mLoading;

        public ViewHolder(View itemView) {
            super(itemView);

            this.mTextbookIcon = (CircleImageView) itemView.findViewById(R.id.imv_cell_listing_icon);
            this.mTextbookDescription = (TextView) itemView.findViewById(R.id.lbl_cell_textbook_description);
            this.mTextbookName = (TextView) itemView.findViewById(R.id.lbl_cell_textbook_name);
            this.mContainer = (ViewGroup) itemView.findViewById(R.id.root_list_item);
            this.mLoading = (ProgressBar) itemView.findViewById(R.id.pro_item_data);

            this.mContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Listing listing = listOfData.get(this.getAdapterPosition());

            controller.onListingCellClick(listing, view);
        }
    }
}