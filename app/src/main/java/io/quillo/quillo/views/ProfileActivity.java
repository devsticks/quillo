package io.quillo.quillo.views;

/**
 * Created by Stickells on 15/01/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.quillo.quillo.R;
import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.controllers.ProfileController;
import io.quillo.quillo.interfaces.SellerListingsListener;

public class ProfileActivity extends AppCompatActivity implements SellerListingsListener, View.OnClickListener {

    private static final String EXTRA_SELLER = "EXTRA_SELLER";
    private static final String EXTRA_DATABASE = "EXTRA_DATABASE";
    private static final String EXTRA_LISTING = "EXTRA_LISTING";

    private List<Listing> sellerListings;
    private boolean isViewingOwnProfile = true;
    private boolean isLoggedIn = false;
    private Person seller;
    private CustomFirebaseDatabase customFirebaseDatabase;

    private ProfileController controller;

    private LayoutInflater layoutInflater;
    private RecyclerView mRecyclerView;
    private CustomAdapter adapter;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        customFirebaseDatabase = (CustomFirebaseDatabase) i.getSerializableExtra(EXTRA_DATABASE);
        seller = (Person) i.getSerializableExtra(EXTRA_SELLER);
        //TODO Fill appropriate currentUser vibe here
        // isViewingOwnProfile = seller.getUid().equals(currentUser.getUid());

        layoutInflater = getLayoutInflater();

        // TODO Toolbar code needed?
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tlb_profile_activity);
        toolbar.setTitle(R.string.title_activity_profile);
//        toolbar.setLogo(R.drawable.ic_view_list_white_24dp);
        toolbar.setTitleMarginStart(72);

        controller = new ProfileController(this, customFirebaseDatabase);
        sellerListings = controller.getListings();

        setUpView();
    }

    @Override
    public void startListingDetailActivity(Listing listing, View viewRoot) {
        Intent i = new Intent(this, ListingDetailActivity.class);
        i.putExtra(EXTRA_LISTING, listing);

        startActivity(i);
    }

    public void startAddEditListingActivity() {
        Intent i = new Intent(this, AddEditListingActivity.class);
        i.putExtra(EXTRA_SELLER, seller);

        startActivity(i);
    }

    public void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);

        startActivity(i);
    }

    @Override
    public void setUpView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rec_profile_listing_holder);

        FloatingActionButton mAddListingButton = (FloatingActionButton) findViewById(R.id.fab_add_listing);
        mAddListingButton.setOnClickListener(this);
        if (isViewingOwnProfile) {
            mAddListingButton.setVisibility(View.VISIBLE);
        } else {
            mAddListingButton.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter();
        mRecyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.divider_white));

        mRecyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onSellerListingLoaded(Listing newListing) {
        sellerListings.add(newListing);
        int endOfList = sellerListings.size() - 1;
        adapter.notifyItemInserted(endOfList);
        //mRecyclerView.smoothScrollToPosition(endOfList);
    }

    @Override
    public void deleteListingCellAt(int position) {
        sellerListings.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void showUndoSnackBar() {
        Snackbar.make(
                findViewById(R.id.root_profile_activity),
                getString(R.string.action_delete_item),
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.handleUndoDeleteConfirmed();
            }
        })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        controller.handleSnackbarTimeout();
                    }
                })
                .show();
    }

    @Override
    public void insertListingCellAt(int position, Listing listItem) {
        sellerListings.add(position, listItem);
        adapter.notifyItemInserted(position);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.fab_add_listing) {
            if (isLoggedIn) {
                startAddEditListingActivity();
            } else {
                startLoginActivity();
            }
        }
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ListingCellViewHolder> {

        @Override
        public ListingCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.listing_cell, parent, false);

            return new ListingCellViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ListingCellViewHolder holder, int position) {
            Listing currentItem = sellerListings.get(position);

            holder.mTextbookDescription.setText(currentItem.getDescription());
            holder.mTextbookName.setText(currentItem.getName());
            holder.mLoading.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return sellerListings.size();
        }

        class ListingCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mTextbookDescription;
            private TextView mTextbookName;
            private ViewGroup mContainer;
            private ProgressBar mLoading;

            public ListingCellViewHolder(View itemView) {
                super(itemView);

                this.mTextbookDescription = (TextView) itemView.findViewById(R.id.lbl_cell_author);
                this.mTextbookName = (TextView) itemView.findViewById(R.id.lbl_cell_name);
                this.mContainer = (ViewGroup) itemView.findViewById(R.id.root_list_item);
                this.mLoading = (ProgressBar) itemView.findViewById(R.id.pro_item_data);

                this.mContainer.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Listing listing = sellerListings.get(this.getAdapterPosition());

                controller.handleListingCellClick(listing, view);
            }
        }

    }

    private ItemTouchHelper.Callback createHelperCallback () {
                /*First Param is for Up/Down motion, second is for Left/Right.
        Note that we can supply 0, one constant (e.g. ItemTouchHelper.LEFT), or two constants (e.g.
        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) to specify what directions are allowed.
        */
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            //not used, as the first parameter above is 0
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                controller.handleListingSwiped(
                        position,
                        sellerListings.get(position)
                );
            }
        };

        return simpleItemTouchCallback;
    }

}



