package io.quillo.quillo.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.controllers.ContactOptionsDialogController;
import io.quillo.quillo.controllers.ListingDetailController;
import io.quillo.quillo.interfaces.SellerListener;

/**
 * Created by Stickells on 13/01/2018.
 */

public class ListingDetailActivity extends AppCompatActivity implements SellerListener {

    private ListingDetailController controller;
    private Person seller;
    private CustomFirebaseDatabase customFirebaseDatabase;
    private Listing listing;
    private ContactOptionsDialogController contactDialogController;

    private boolean isViewingOwnListing = false;

    @BindView(R.id.fab_contact_seller) FloatingActionButton mContactSeller;
    @BindView(R.id.btn_seller_profile) LinearLayout mSellerProfileContainer;
    @BindView(R.id.imv_listing_detail_seller_profile_pic) ImageView mSellerProfilePic;
    @BindView(R.id.lbl_listing_detail_seller_name) TextView mSellerName;

    @BindView(R.id.lbl_textbook_name) TextView mListingName;
    @BindView(R.id.lbl_textbook_description) TextView mListingDescription;
    @BindView(R.id.imv_colored_background) ImageView mListingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        listing = (Listing) intent.getSerializableExtra(IntentExtras.EXTRA_LISTING);

        customFirebaseDatabase = new CustomFirebaseDatabase();

        setUpView();

        controller = new ListingDetailController(this, customFirebaseDatabase);

    }

// Fills UI with values from customFirebaseDatabase and sets click handlers, etc
    public void setUpView() {
    //Listing goodies
        mListingName.setText(listing.getName());
        mListingDescription.setText(listing.getDescription());

    //Seller goodies
        //TODO How do pictures get fetched and added to the view?
        // mListingImage.setBackgroundResource( drawableResourceExtra );

        mSellerProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.handleSellerDetailButtonClick(seller,v);
            }
        });

     //Contact goodies

    }

    @OnClick(R.id.fab_contact_seller)
    public void handleContactSellerClick (View v) {
        contactDialogController.showContactOptionsDialog();
    }

    @Override
    public void onSellerLoaded(Person seller) {

        this.seller = seller;
        //TODO Fill appropriate currentUser vibe here
        // isViewingOwnListing = seller.getUid().equals(currentUser.getUid());

        // mSellerProfilePic.setImageResource( ... );
        mSellerName.setText(seller.getName());

        contactDialogController = new ContactOptionsDialogController(seller, listing, this);
    }

    public void startProfileActivity(Person seller, View viewRoot) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(IntentExtras.EXTRA_SELLER, seller);

        startActivity(i);
    }

}