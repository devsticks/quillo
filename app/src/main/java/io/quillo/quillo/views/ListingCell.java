package io.quillo.quillo.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import io.quillo.quillo.R;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.interfaces.ListingCellListener;

/**
 * Created by Stickells on 18/01/2018.
 */

public class ListingCell extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Listing listing;
    private ImageView icon;
    private TextView name;
    private TextView author;
    private TextView price;
    private ToggleButton bookmarkButton;
    private ViewGroup container;
    private ProgressBar loading;

    private ListingCellListener listingCellListener;

    public ListingCell(View itemView) {
        super(itemView);

        this.icon = (ImageView) itemView.findViewById(R.id.imv_cell_listing_icon);
        this.name = (TextView) itemView.findViewById(R.id.lbl_cell_name);
        this.author = (TextView) itemView.findViewById(R.id.lbl_cell_author);
        this.price = (TextView) itemView.findViewById(R.id.lbl_cell_price);
        this.container = (ViewGroup) itemView.findViewById(R.id.root_list_item);
        this.loading = (ProgressBar) itemView.findViewById(R.id.pro_item_data);
        loading.setVisibility(View.INVISIBLE);
        this.bookmarkButton = (ToggleButton) itemView.findViewById(R.id.btn_bookmark);

        this.container.setOnClickListener(this);

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarkButton.isChecked())
                {
                    listingCellListener.onBookmarkClick(listing);
                }
                else {
                    listingCellListener.onUnBookmarkClick(listing);
                }
            }
        });
    }

    public void setListingCellListener(ListingCellListener listingCellListener) {
        this.listingCellListener = listingCellListener;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
        bindListingToViews();
    }

    private void bindListingToViews(){
        name.setText(listing.getName());
        author.setText(listing.getAuthor());
        price.setText("R " + String.valueOf(listing.getPrice()));

        if (listing.isBookmarked()){
            bookmarkButton.toggle();
        }
    }

    public ImageView getIcon(){
        return icon;
    }

    @Override
    public void onClick(View view) {
        listingCellListener.onListingClick(listing);
    }
}