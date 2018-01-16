package io.quillo.quillo.views;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.quillo.quillo.R;
import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.handlers.HomeSearchController;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.quillo.quillo.interfaces.listingsListener;

public class HomeSearchActivity extends AppCompatActivity implements listingsListener {

    private static final String EXTRA_DATABASE = "EXTRA_DATABASE";
    private static final String EXTRA_LISTING = "EXTRA_LISTING";

    private LayoutInflater layoutInflater;
    private RecyclerView mRecyclerView;
    private CustomAdapter adapter;
    private android.support.v7.widget.Toolbar toolbar;

    private List<Listing> listings;
    private Database database;
    private HomeSearchController controller;

    public static boolean databaseMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);
        //ButterKnife.bind(this);

        layoutInflater = getLayoutInflater();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tlb_home_search_activity);
        toolbar.setTitle(R.string.title_activity_search);
//        toolbar.setLogo(R.drawable.ic_view_list_white_24dp);
        toolbar.setTitleMarginStart(72);

        database = new Database();
        databaseMade = true;

        controller = new HomeSearchController(this, database);
        listings = controller.getListings();

        setUpView();
    }

    //TODO The majority of this code and functionality is duplicated in ProfileActivity, fix up.

    public void startListingDetailActivity(Listing listing, View viewRoot) {
        Intent i = new Intent(this, ListingDetailActivity.class);
        i.putExtra(EXTRA_DATABASE, database);
        i.putExtra(EXTRA_LISTING, listing);

        startActivity(i);
    }

    public void setUpView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rec_home_search_listing_holder);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter();
        mRecyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(HomeSearchActivity.this, R.drawable.divider_white));
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onListingLoaded(Listing newListing) {
        listings.add(newListing);
        int endOfList = listings.size() - 1;
        adapter.notifyItemInserted(endOfList);
        //mRecyclerView.smoothScrollToPosition(endOfList);
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ListingCellViewHolder> {

        @Override
        public ListingCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.listing_cell, parent, false);

            return new ListingCellViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ListingCellViewHolder holder, int position) {
            Listing currentItem = listings.get(position);

            holder.mTextbookIcon.setImageResource(currentItem.getColorResource());
            holder.mTextbookDescription.setText(currentItem.getDescription());
            holder.mTextbookName.setText(currentItem.getName());
            holder.mLoading.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return listings.size();
        }

        class ListingCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private CircleImageView mTextbookIcon;
            private TextView mTextbookDescription;
            private TextView mTextbookName;
            private ViewGroup mContainer;
            private ProgressBar mLoading;

            public ListingCellViewHolder(View itemView) {
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
                Listing listing = listings.get(this.getAdapterPosition());

                controller.handleListingCellClick(listing, view);
            }
        }

    }

}
