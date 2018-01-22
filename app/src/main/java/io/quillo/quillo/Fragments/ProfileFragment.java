package io.quillo.quillo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.SellerListingsListener;

/**
 * Created by shkla on 2018/01/22.
 */

public class ProfileFragment extends Fragment implements SellerListingsListener, ListingCellListener, View.OnClickListener{

    private boolean isViewingOwnProfile = true;
    private boolean isLoggedIn = true;
    private Person seller;
    private CustomFirebaseDatabase customFirebaseDatabase;
    private Listing temporaryListing;
    private int temporaryListingPosition;

    private ListingAdapter adapter;
    @BindView(R.id.rec_profile_listing_holder)
    RecyclerView recyclerView;
    @BindView(R.id.tlb_profile_activity)
    Toolbar toolbar;
    @BindView(R.id.lbl_seller_name)
    TextView name;
    @BindView(R.id.lbl_seller_university)
    TextView university;

    public static ProfileFragment newInstance(){
        ProfileFragment profileFragment = new ProfileFragment();
        return  profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());

        adapter = new ListingAdapter(this, getContext());
        customFirebaseDatabase = new CustomFirebaseDatabase();
        customFirebaseDatabase.setSellerListingsListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        setUpView(view);
        return view;
    }

    //TODO Update with fragments
/*
    public void startListingDetailActivity(Listing listing) {


        Intent intent = new Intent(this, ListingDetailActivity.class);
        intent.putExtra(IntentExtras.EXTRA_LISTING, listing);

        startActivity(intent);
    }

    public void startAddEditListingActivity() {
        Intent intent = new Intent(this, AddEditListingActivity.class);
        intent.putExtra(IntentExtras.EXTRA_SELLER, seller);

        startActivity(intent);
    }

    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }*/



    public void setUpView(View view) {

        FloatingActionButton mAddListingButton = (FloatingActionButton) view.findViewById(R.id.fab_add_listing);
        recyclerView = (RecyclerView)view.findViewById(R.id.rec_profile_listing_holder);
        name = (TextView)view.findViewById(R.id.lbl_seller_name);
        mAddListingButton.setOnClickListener(this);
        if (isViewingOwnProfile) {
            mAddListingButton.setVisibility(View.VISIBLE);
        } else {
            mAddListingButton.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_white));

        recyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        name.setText("Tamir Shklaz");
        //TODO get actual university when we have that up and running
        //university = ...

    }

    @Override
    public void onBookmarkClick(Listing listing) {
        customFirebaseDatabase.addBookmark(listing);
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        customFirebaseDatabase.removeBookmark(listing);
    }

    @Override
    public void onListingClick(Listing listing) {
        //startListingDetailActivity(listing);
    }

    @Override
    public void onSellerListingLoaded(Listing newListing) {
        adapter.addListing(newListing);
    }

    @Override
    public void onSellerListingUpdated(Listing listing) {
        adapter.updateListing(listing);
    }

    public void deleteListingCellAt(int position) {
        adapter.removeListing(position);
    }

    public void insertListingCellAt(int position, Listing newListing) {
        adapter.insertListing(position, newListing);
    }

    public void showUndoSnackBar() {
        Snackbar.make(
                getActivity().findViewById(R.id.root_profile_activity),
                getString(R.string.action_delete_item),
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUndoDeleteConfirmed();
            }
        })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        handleSnackbarTimeout();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.fab_add_listing) {
            if (isLoggedIn) {
               // startAddEditListingActivity();
            } else {
                //startLoginActivity();
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
                if (isViewingOwnProfile) {
                    handleListingSwiped(
                            position,
                            adapter.getListings().get(position)
                    );
                }
            }
        };

        return simpleItemTouchCallback;
    }

    private void handleListingSwiped(int position, Listing listing) {
        customFirebaseDatabase.deleteListing(listing);
        deleteListingCellAt(position);

        temporaryListing = listing;
        temporaryListingPosition = position;

        showUndoSnackBar();
    }

    private void handleUndoDeleteConfirmed() {
        if (temporaryListing != null) {
            customFirebaseDatabase.insertListing(temporaryListing);
            insertListingCellAt(temporaryListingPosition, temporaryListing);

            temporaryListing = null;
            temporaryListingPosition = 0;
        }
    }

    private void handleSnackbarTimeout() {

    }
}
