package io.quillo.quillo.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.handlers.ContactOptionsDialogController;
import io.quillo.quillo.handlers.ListingDetailController;
import io.quillo.quillo.interfaces.sellerListener;

/**
 * Created by Stickells on 13/01/2018.
 */

public class ListingDetailActivity extends AppCompatActivity implements sellerListener {

    private static final String EXTRA_DATABASE = "EXTRA_DATABASE";
    private static final String EXTRA_SELLER = "EXTRA_SELLER";
    private static final String EXTRA_LISTING = "EXTRA_LISTING";

    private ListingDetailController controller;
    private Person seller;
    private Database database;
    private Listing listing;
    private ContactOptionsDialogController dialogController;

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

        Intent i = getIntent();
        database = (Database) i.getSerializableExtra(EXTRA_DATABASE);
        listing = (Listing) i.getSerializableExtra(EXTRA_LISTING);

        setUpView();

        controller = new ListingDetailController(this, database);
    }

// Fills UI with values from database and sets click handlers, etc
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
        dialogController.showContactOptionsDialog();
    }

//    @OnClick (R.id.btn_dialog_call)
//    public void handleDialogCallClick (View v) {
////        Toast.makeText(v.getContext(), "Calling...", Toast.LENGTH_LONG).show();
//    }
//
//    @OnClick (R.id.btn_dialog_email)
//    public void handleDialogEmailClick (View v) {
////        Toast.makeText(v.getContext(), "Emailing...", Toast.LENGTH_LONG).show();
//    }
//
//    @OnClick (R.id.btn_dialog_text)
//    public void handleDialogTextClick (View v) {
////        Toast.makeText(v.getContext(), "Texting...", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onSellerLoaded(Person seller) {

        this.seller = seller;
        //TODO Fill appropriate currentUser vibe here
        // isViewingOwnListing = seller.getUid().equals(currentUser.getUid());

        // mSellerProfilePic.setImageResource( ... );
        mSellerName.setText(seller.getName());

        dialogController = new ContactOptionsDialogController(seller, listing, this);

    }

    public void startProfileActivity(Person seller, View viewRoot) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(EXTRA_DATABASE, database);
        i.putExtra(EXTRA_SELLER, seller);

        startActivity(i);
    }

}