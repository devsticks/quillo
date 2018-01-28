package io.quillo.quillo.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.cketti.mailto.EmailIntentBuilder;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.FirebaseHelper;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.ListingsListener;
import io.quillo.quillo.interfaces.PersonListener;

/**
 * Created by shkla on 2018/01/25.
 */

public class ListingDetailFragment extends Fragment implements AddEditListingFragment.EditListingListener {
    //TODO: Fix image scaling

    private Person seller;
    private Listing listing;


    private boolean isViewingOwnListing = false;

    //TODO: Add edition field

    @BindView(R.id.tlb_listing_detail)
    Toolbar toolbar;
    @BindView(R.id.fab_listing_action)
    FloatingActionButton listingActionFAB;
    @BindView(R.id.btn_seller_profile)
    View sellerContainerButton;
    @BindView(R.id.div_seller)
    View sellerContainer;
    @BindView(R.id.imv_seller_profile_pic)
    ImageView sellerProfilePic;
    @BindView(R.id.lbl_seller_name)
    TextView sellerNameTV;
    @BindView(R.id.lbl_seller_university)
    TextView sellerUniversityTV;

    @BindView(R.id.btn_call)
    View call;
    @BindView(R.id.btn_email)
    View email;
    @BindView(R.id.btn_text)
    View text;
    @BindView(R.id.btn_share)
    View share;

    @BindView(R.id.lbl_title)
    TextView title;
    @BindView(R.id.lbl_author)
    TextView author;
    @BindView(R.id.lbl_description)
    TextView description;
    @BindView(R.id.lbl_price)
    TextView price;
    @BindView(R.id.imv_listing_image)
    ImageView listingImage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_detail, container, false);

        Bundle bundle = this.getArguments();
        ButterKnife.bind(this, view);

        listing = (Listing) bundle.getSerializable(IntentExtras.EXTRA_LISTING);
        bindListingToViews();
        loadSeller();

        return view;
    }


    public void loadSeller() {
        if (listing != null) {
            ((MainActivity) getActivity()).quilloDatabase.loadPerson(listing.getSellerUid(), new PersonListener() {
                @Override
                public void onPersonLoaded(Person person) {
                    seller = person;

                    isViewingOwnListing = FirebaseHelper.getCurrentUserUid().equals(listing.getSellerUid());
                    if (isViewingOwnListing) {
                        sellerContainer.setVisibility(View.GONE);
                        listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                    } else {
                        sellerContainer.setVisibility(View.VISIBLE);
                        bindSellerToViews();
                    }
                }
            });
        }
    }

    private void bindSellerToViews() {

        sellerNameTV.setText(seller.getName());
        sellerUniversityTV.setText(seller.getUniversityUid());

        if (seller.getPhotoUrl() != null) {
            Glide.with(getContext()).load(seller.getPhotoUrl()).into(sellerProfilePic);
        }

        setupSellerContainerButtons();
    }

    @OnClick(R.id.fab_listing_action)
    public void handleListingActionClick() {
        if (isViewingOwnListing) {
            //Edit listing action
            AddEditListingFragment addEditListingFragment = new AddEditListingFragment();
            addEditListingFragment.setTargetFragment(this, 1);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentExtras.EXTRA_LISTING, listing);
            addEditListingFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_holder, addEditListingFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();


        } else {
            //Bookmark action
            if (listing.isBookmarked()) {
                listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                listing.setBookmarked(false);
                ((MainActivity) getActivity()).quilloDatabase.removeBookmark(listing);
            } else {
                listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                listing.setBookmarked(true);
                ((MainActivity) getActivity()).quilloDatabase.addBookmark(listing);

            }
        }
    }

    @OnClick(R.id.seller_view_container)
    public void handleSellerProfileClick() {
        Log.d(ListingDetailFragment.class.getName(), "Seller profile click");

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentExtras.EXTRA_SELLER, seller);
        profileFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, profileFragment)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .addToBackStack(null)
                .commit();
    }


    private void setupSellerContainerButtons() {
        sellerContainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (seller.getPhoneNumber() != null) {
            call.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);

                    callIntent.setData(Uri.parse("tel:" + seller.getPhoneNumber()));

                    view.getContext().startActivity(callIntent);
                }
            });

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Uri uri = Uri.parse("smsto:" + seller.getPhoneNumber());
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                        smsIntent.putExtra("sms_body", "Hi " + seller.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo.");
                        view.getContext().startActivity(smsIntent);
                    } catch (Exception e) {
                        Toast.makeText(view.getContext(),
                                "SMS failed, please try again later!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            call.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
        }

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = EmailIntentBuilder.from(view.getContext())
                        .to(seller.getEmail())
                        .subject("Quillo Enquiry")
                        .body("Hi " + seller.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo.")
                        .start();
                if (!success) {
                    Toast.makeText(view.getContext(),
                            "Email failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindListingToViews() {
        title.setText(listing.getName());
        author.setText("James");
        description.setText(listing.getDescription());
        price.setText("R" + listing.getPrice());

        if (listing.isBookmarked()) {
            listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
        }

        Glide.with(getContext()).load(listing.getImageUrl()).into(listingImage);
    }
    public void setListing(Listing listing){
        this.listing = listing;
    }

    @Override
    public void onListingUpdated() {
        ((MainActivity)getActivity()).quilloDatabase.loadListing(listing.getUid(), new ListingsListener() {
            @Override
            public void onListingLoaded(Listing listing) {
                setListing(listing);
                bindListingToViews();
            }

            @Override
            public void onListingUpdated(Listing listing) {

            }

            @Override
            public void onListingRemoved(Listing listing) {

            }
        });
    }
}
