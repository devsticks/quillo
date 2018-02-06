package io.quillo.quillo.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.ListingCellListener;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.interfaces.PersonListingsListener;
import io.quillo.quillo.utils.FirebaseHelper;
import io.quillo.quillo.utils.GlideApp;

/**
 * Created by shkla on 2018/01/22.
 */

public class ProfileFragment extends Fragment implements ListingCellListener, View.OnClickListener {

    public static final String FRAGMENT_NAME = ProfileFragment.class.getName();

    private boolean isViewingOwnProfile = true;
    private Person seller;
    private Listing temporaryListing;
    private int temporaryListingPosition;


    private ListingAdapter adapter;
    @BindView(R.id.rec_profile_listing_holder)
    RecyclerView recyclerView;
    @BindView(R.id.lbl_seller_name)
    TextView nameLabel;
    @BindView(R.id.lbl_seller_university)
    TextView universityLabel;
    @BindView(R.id.btn_edit_profile)
    ImageView editProfileBtn;
    @BindView(R.id.imv_profile_picture)
    ImageView profilePicture;
    @BindView(R.id.loader_animation)
    LottieAnimationView loader;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();

        if ( bundle != null && bundle.containsKey(IntentExtras.EXTRA_SELLER) ) {
            seller = (Person) getArguments().getSerializable(IntentExtras.EXTRA_SELLER);
            isViewingOwnProfile = false;
            editProfileBtn.setVisibility(View.GONE);
        }

        adapter = new ListingAdapter(this, getContext(), isViewingOwnProfile);

        setupDatabase();
        setUpView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isViewingOwnProfile) {
            ((MainActivity) getActivity()).showBottomNavBar();
            updateUser();
        }

    }

    public void updateUser(){
        if (seller != null){
            ((MainActivity)getActivity()).quilloDatabase.loadPerson(seller.getUid(), new PersonListener() {
                @Override
                public void onPersonLoaded(Person person) {
                    seller = person;
                    bindSellerToViews();
                }
            });
        }
    }
    public void setupDatabase() {
        String personUid;
        if (seller == null) {
            personUid = FirebaseHelper.getCurrentUserUid();
            if (personUid != null) {
                //Is viewing own profile
                ((MainActivity) getActivity()).quilloDatabase.loadPerson(personUid, new PersonListener() {
                    @Override
                    public void onPersonLoaded(Person person) {
                        seller = person;
                        bindSellerToViews();
                    }
                });
            }

        }else{
            //Viewing another profile
            personUid = seller.getUid();
            bindSellerToViews();
        }

        ((MainActivity) getActivity()).quilloDatabase.observePersonListings(personUid, new PersonListingsListener() {
            @Override
            public void onPersonListingLoaded(Listing listing) {
                adapter.addListing(listing);
            }

            @Override
            public void onPersonListingUpdated(Listing listing) {

            }

            @Override
            public void onPersonListingRemoved(Listing listing) {
                adapter.removeListing(listing.getUid());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.removeAllListings();
        if(seller == null){
            return;
        }
        if (seller.getUid() != null) {
            ((MainActivity) getActivity()).quilloDatabase.stopObservingPersonListings(seller.getUid());
        }
    }

    @OnClick(R.id.btn_edit_profile)
    public void handleEditProfileButtonClick() {
        if(seller == null){
            return;
        }
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentExtras.EXTRA_SELLER, seller);
        editProfileFragment.setArguments(bundle);


        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, editProfileFragment)
                .addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_holder).getClass().getName())
                .commit();

        ((MainActivity) getActivity()).hideBottomNavBar();
    }


    public void setUpView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_white));

        recyclerView.addItemDecoration(itemDecoration);

    }

    @Override
    public void onBookmarkClick(Listing listing) {
        ((MainActivity)getActivity()).quilloDatabase.addBookmark(listing);
        Toast.makeText(getContext(), "Added to bookmarks", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        ((MainActivity)getActivity()).quilloDatabase.removeBookmark(listing);
        Toast.makeText(getContext(), "Removed from bookmarks", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListingClick(Listing listing) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(IntentExtras.EXTRA_LISTING_UID, listing.getUid());

        ListingDetailFragment listingDetailFragment = new ListingDetailFragment();
        listingDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_holder, listingDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(FRAGMENT_NAME)
                .commit();

        ((MainActivity)getActivity()).hideBottomNavBar();
        ((MainActivity)getActivity()).setupToolbar();

    }


    public void bindSellerToViews() {
        nameLabel.setText(seller.getName());
        universityLabel.setText(seller.getUniversityUid());

        if (seller.getImageUrl() != null) {
            GlideApp.with(getContext())
                    .load(seller.getImageUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_24dp));
                            loader.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loader.cancelAnimation();
                            loader.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(profilePicture);


        }else{
            loader.cancelAnimation();
            loader.setVisibility(View.GONE);
        }
    }

    public void deleteListingCellAt(int position) {
        adapter.removeListing(position);
    }

    public void insertListingCellAt(int position, Listing newListing) {
        adapter.insertListing(position, newListing);
    }

    public void showUndoSnackBar(final Listing listing) {
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

                        handleSnackbarTimeout(listing);
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();


    }

    private ItemTouchHelper.Callback createHelperCallback() {
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

        deleteListingCellAt(position);

        temporaryListing = listing;
        temporaryListingPosition = position;

        showUndoSnackBar(listing);
    }

    private void handleUndoDeleteConfirmed() {
        if (temporaryListing != null) {
            //quilloDatabase.insertListing(temporaryListing);
            insertListingCellAt(temporaryListingPosition, temporaryListing);

            temporaryListing = null;
            temporaryListingPosition = 0;
        }
    }


    private void handleSnackbarTimeout(Listing listing) {
        ((MainActivity)getActivity()).quilloDatabase.deleteListing(listing);
    }


}
