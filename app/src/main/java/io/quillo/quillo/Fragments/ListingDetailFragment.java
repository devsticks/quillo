package io.quillo.quillo.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.cketti.mailto.EmailIntentBuilder;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ContactOptionsDialogController;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.interfaces.PersonListener;

/**
 * Created by shkla on 2018/01/25.
 */

public class ListingDetailFragment extends Fragment {



    private Person seller;
    private QuilloDatabase quilloDatabase;
    private Listing listing;
    private ContactOptionsDialogController contactDialogController;

    private boolean isViewingOwnListing;

    //TODO: Add edition field

    @BindView(R.id.tlb_listing_detail)
    Toolbar toolbar;
    @BindView(R.id.fab_contact_seller)
    FloatingActionButton contactSeller;
    @BindView(R.id.btn_seller_profile) View sellerContainerButton;
    @BindView(R.id.div_seller) View sellerContainer;
    @BindView(R.id.imv_seller_profile_pic)
    ImageView sellerProfilePic;
    @BindView(R.id.lbl_seller_name)
    TextView sellerName;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quilloDatabase = new QuilloDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_detail, container, false);

        Bundle bundle = this.getArguments();
        ButterKnife.bind(this, view);

        listing = (Listing)bundle.getSerializable(IntentExtras.EXTRA_LISTING);
        bindListingToViews();
        loadSeller();

        return view;
    }

    

    @OnClick(R.id.fab_contact_seller)
    public void handleContactSellerClick (View v) {
        contactDialogController.showContactOptionsDialog();
    }

    public void loadSeller(){
        if (listing != null) {
            quilloDatabase.loadPerson(listing.getUid(), new PersonListener() {
                @Override
                public void onPersonLoaded(Person person) {
                    seller = person;
                    bindSellerToViews();
                }
            });
        }
    }

    private void bindSellerToViews(){

        isViewingOwnListing = seller.getUid().equals(listing.getUid());
        if (isViewingOwnListing) {
            sellerContainer.setVisibility(View.GONE);
        } else {
            sellerContainer.setVisibility(View.VISIBLE);
        }

        sellerContainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(seller,v);
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

    private void  bindListingToViews(){
        title.setText(listing.getName());
        author.setText("James");
        description.setText(listing.getDescription());
        price.setText("R" + listing.getPrice());
    }



    public void startProfileActivity(Person seller, View viewRoot) {
       //TODO: Implement fragments

       /* Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(IntentExtras.EXTRA_SELLER, seller);

        startActivity(i);*/
    }
}
