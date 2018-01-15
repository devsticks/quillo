package io.quillo.quillo.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.quillo.quillo.R;
import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.handlers.ListingDetailController;
import io.quillo.quillo.interfaces.sellerListener;

/**
 * Created by Stickells on 13/01/2018.
 */

public class ListingDetailActivity extends AppCompatActivity implements sellerListener {

    private static final String EXTRA_DATABASE = "EXTRA_DATABASE";
    private static final String EXTRA_SELLER = "EXTRA_SELLER";
    private static final String EXTRA_LISTING = "EXTRA_LISTING";

    private TextView mListingName;
    private TextView mListingDescription;
    private View mListingImage;

    private LinearLayout mSellerProfileContainer;
    private ImageView mSellerProfilePic;
    private TextView mSellerName;

    private ListingDetailController controller;
    private Person seller;
    private Database database;
    private Listing listing;

    private boolean isViewingOwnListing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);

        Intent i = getIntent();
        database = (Database) i.getSerializableExtra(EXTRA_DATABASE);
        listing = (Listing) i.getSerializableExtra(EXTRA_LISTING);

        setUpView();

        controller = new ListingDetailController(this, database);

    }

// Fills UI with values from database and sets click handlers, etc
    public void setUpView() {
    //Listing goodies
        mListingName = (TextView) findViewById(R.id.lbl_textbook_name);
        mListingDescription = (TextView) findViewById(R.id.lbl_textbook_description);
        mListingImage = findViewById(R.id.imv_colored_background);

        mListingName.setText(listing.getName());
        mListingDescription.setText(listing.getDescription());

    //Seller goodies
        mSellerProfilePic = findViewById(R.id.imv_listing_detail_seller_profile_pic);
        mSellerName = (TextView) findViewById(R.id.lbl_listing_detail_seller_name);

        //TODO How do pictures get fetched and added to the view?
        // mListingImage.setBackgroundResource( drawableResourceExtra );

        mSellerProfileContainer = findViewById(R.id.btn_seller_profile);
        mSellerProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.handleSellerDetailButtonClick(seller,v);
            }
        });

    }

    @Override
    public void onSellerLoaded(Person seller) {

        this.seller = seller;
        //TODO Fill appropriate currentUser vibe here
        // isViewingOwnListing = seller.getUid().equals(currentUser.getUid());

        // mSellerProfilePic.setImageResource( ... );
        mSellerName.setText(seller.getName());

    }

    public void startProfileActivity(Person seller, View viewRoot) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(EXTRA_DATABASE, database);
        i.putExtra(EXTRA_SELLER, seller);

        startActivity(i);
    }

}