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
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.FirebaseHelper;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.interfaces.PersonListingsListener;

/**
 * Created by shkla on 2018/01/22.
 */

public class ProfileFragment extends Fragment implements PersonListingsListener, PersonListener, ListingCellListener, View.OnClickListener{

    private boolean isViewingOwnProfile = true;
    private boolean isLoggedIn = true;
    private Person seller;
    private QuilloDatabase quilloDatabase;
    private Listing temporaryListing;
    private int temporaryListingPosition;

    private ListingAdapter adapter;
    @BindView(R.id.rec_profile_listing_holder)
    RecyclerView recyclerView;
    @BindView(R.id.tlb_profile_activity)
    Toolbar toolbar;
    @BindView(R.id.lbl_seller_name)
    TextView nameLabel;
    @BindView(R.id.lbl_seller_university)
    TextView universityLabel;

    public static ProfileFragment newInstance(){
        ProfileFragment profileFragment = new ProfileFragment();
        return  profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ListingAdapter(this, getContext());

        quilloDatabase = new QuilloDatabase();
        setupDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        ButterKnife.bind(this, view);
        setUpView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).showNavbar();
    }

    public void setupDatabase(){
        quilloDatabase.setPersonListingsListener(this);
        quilloDatabase.setPersonListener(this);
        quilloDatabase.observePerson(FirebaseHelper.getCurrentFirebaseUser().getUid());
    }

    //TODO Update with fragments

    public void setUpView(View view) {
        FloatingActionButton mAddListingButton = (FloatingActionButton) view.findViewById(R.id.fab_add_listing);
        recyclerView = (RecyclerView)view.findViewById(R.id.rec_profile_listing_holder);

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
        //TODO get actual universityLabel when we have that up and running
        //universityLabel = ...
    }

    @Override
    public void onBookmarkClick(Listing listing) {
        quilloDatabase.addBookmark(listing);
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        quilloDatabase.removeBookmark(listing);
    }

    @Override
    public void onListingClick(Listing listing) {
        ((MainActivity)getActivity()).showListingDetailFragment(listing);
    }

    @Override
    public void onPersonListingLoaded(Listing newListing) {
        adapter.addListing(newListing);
    }

    @Override
    public void onPersonLoaded(Person person) {
        bindSellerToViews(person);
    }


    public void bindSellerToViews(Person seller){
        nameLabel.setText(seller.getName());
        universityLabel.setText(seller.getUniversityUid());
    }

    @Override
    public void onPersonListingUpdated(Listing listing) {
        adapter.updateListing(listing);
    }

    @Override
    public void onPersonListingRemoved(Listing listing) {
        adapter.removeListing(listing.getUid());
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
        quilloDatabase.deleteListing(listing);
        deleteListingCellAt(position);

        temporaryListing = listing;
        temporaryListingPosition = position;

        showUndoSnackBar();
    }

    private void handleUndoDeleteConfirmed() {
        if (temporaryListing != null) {
            //quilloDatabase.insertListing(temporaryListing);
            insertListingCellAt(temporaryListingPosition, temporaryListing);

            temporaryListing = null;
            temporaryListingPosition = 0;
        }
    }



    private void handleSnackbarTimeout() {

    }


}
