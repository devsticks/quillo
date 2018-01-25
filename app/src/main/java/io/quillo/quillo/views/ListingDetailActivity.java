package io.quillo.quillo.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.cketti.mailto.EmailIntentBuilder;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ContactOptionsDialogController;
import io.quillo.quillo.data.CurrentUser;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.PersonListener;

/**
 * Created by Stickells on 13/01/2018.
 */

public class ListingDetailActivity extends AppCompatActivity implements PersonListener {

    private Person seller;
    private QuilloDatabase quilloDatabase;
    private Listing listing;
    private ContactOptionsDialogController contactDialogController;

    private boolean isViewingOwnListing;

    @BindView(R.id.tlb_listing_detail) Toolbar toolbar;
    @BindView(R.id.fab_contact_seller) FloatingActionButton contactSeller;
    @BindView(R.id.btn_seller_profile) View sellerContainerButton;
    @BindView(R.id.div_seller) View sellerContainer;
    @BindView(R.id.imv_seller_profile_pic) ImageView sellerProfilePic;
    @BindView(R.id.lbl_seller_name) TextView sellerName;

    @BindView(R.id.btn_call) View call;
    @BindView(R.id.btn_email) View email;
    @BindView(R.id.btn_text) View text;
    @BindView(R.id.btn_share) View share;

    @BindView(R.id.lbl_title) TextView title;
    @BindView(R.id.lbl_author) TextView author;
    @BindView(R.id.lbl_description) TextView description;
    @BindView(R.id.lbl_price) TextView price;
    @BindView(R.id.imv_image) ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_listing_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        listing = (Listing) intent.getSerializableExtra(IntentExtras.EXTRA_LISTING);

        quilloDatabase = new QuilloDatabase();
        quilloDatabase.setPersonListener(this);
        quilloDatabase.observeUser(listing.getSellerUid());

        setUpView();

    }

    public void setUpView() {
        title.setText(listing.getName());
        author.setText("James Stewart");
        description.setText(listing.getDescription());
        price.setText(String.valueOf(listing.getPrice()));

        //TODO How do pictures get fetched and added to the view?
        // image.setBackgroundResource( drawableResourceExtra );

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.fab_contact_seller)
    public void handleContactSellerClick (View v) {
        contactDialogController.showContactOptionsDialog();
    }

    @Override
    public void onPersonLoaded(final Person person) {

        this.seller = person;
        //TODO Fill appropriate currentUser vibe here
        isViewingOwnListing = person.getUid().equals(CurrentUser.Uid);
        if (isViewingOwnListing) {
            sellerContainer.setVisibility(View.GONE);
        } else {
            sellerContainer.setVisibility(View.VISIBLE);
        }

        sellerContainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(person,v);
            }
        });

        //TODO FILL PROFILE PIC AND UNIVERSITY
        // sellerProfilePic.setImageResource( ... );
        //sellerUniversity.setText(seller.getUniversity());
        sellerName.setText(person.getName());

        if (person.getPhoneNumber() != null) {
            call.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);

                    callIntent.setData(Uri.parse("tel:" + person.getPhoneNumber()));

                    view.getContext().startActivity(callIntent);
                }
            });

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Uri uri = Uri.parse("smsto:" + person.getPhoneNumber());
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                        smsIntent.putExtra("sms_body", "Hi " + person.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo.");
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
                        .to(person.getEmail())
                        .subject("Quillo Enquiry")
                        .body("Hi " + person.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo.")
                        .start();
                if (!success) {
                    Toast.makeText(view.getContext(),
                            "Email failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //OLD
        contactDialogController = new ContactOptionsDialogController(person, listing, this);
    }

    public void startProfileActivity(Person seller, View viewRoot) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(IntentExtras.EXTRA_SELLER, seller);

        startActivity(i);
    }

}