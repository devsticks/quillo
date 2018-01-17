package io.quillo.quillo.views;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;

public class AddEditListingActivity extends AppCompatActivity {

    private static final String EXTRA_DATABASE = "EXTRA_DATABASE";
    private static final String EXTRA_SELLER = "EXTRA_SELLER";
    private static final String EXTRA_LISTING = "EXTRA_LISTING";
    private static final String ADD_LISTING_HEADER = "Add Listing";
    private static final String EDIT_LISTING_HEADER = "Edit Listing";

    private Database database;
    private Listing listing;
    private Person seller;

    @BindView(R.id.input_title) TextInputEditText mTitleInput;
    @BindView(R.id.input_description) TextInputEditText mDescriptionInput;
    @BindView(R.id.input_isbn) TextInputEditText mISBNInput;
    @BindView(R.id.input_price) TextInputEditText mPriceInput;

    @BindView(R.id.imv_listing_photo_1) ImageView mPhoto1;
    @BindView(R.id.imv_listing_photo_2) ImageView mPhoto2;
    @BindView(R.id.imv_listing_photo_3) ImageView mPhoto3;

    ArrayList<ImageView> listingImageViews;
    ImageView currentPhotoAdder;
    int numberOfPhotos = 0;
    private boolean addingListing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_listing);
        ButterKnife.bind(this);

        Intent i  = getIntent();
        Bundle extras = i.getExtras();
        database = (Database) i.getSerializableExtra(EXTRA_DATABASE);
        seller = (Person) i.getSerializableExtra(EXTRA_SELLER);
        listing = (Listing) i.getSerializableExtra(EXTRA_LISTING);

        if (extras.getSerializable(EXTRA_LISTING) != null) { //includes listing, so we're editing
            addingListing = false;
            listing = (Listing) i.getSerializableExtra(EXTRA_LISTING);
            setUpView(addingListing);
            updatePhotoButtons();
        } else { //doesn't include listing, so we're adding a new one
            addingListing = true;
            setUpView(addingListing);
            currentPhotoAdder = listingImageViews.get(0);
        }
    }

    @OnClick({R.id.imv_listing_photo_1, R.id.imv_listing_photo_2, R.id.imv_listing_photo_3})
    public void handleAddPhotoClick(View v) {
        if (currentPhotoAdder != null && v.getId() == currentPhotoAdder.getId()) {
            //TODO add a photo to DB
            numberOfPhotos++;
            updatePhotoButtons();
            if (numberOfPhotos > 2) {
                currentPhotoAdder = null;
            }
        }
    }

    @OnClick(R.id.btn_publish)
    public void handlePublishClick(View v) {
        //TODO Add listing to database
        if (addingListing) {
            //TODO createNewListing is fake.
            Listing newListing = database.createNewListing();
            database.addListingToDatabase(newListing);
        } else {
            //TODO How should this be done properly?
            database.updateListing(listing);
        }

        startListingDetailActivity(v);
    }

    public void startListingDetailActivity(View viewRoot) {
        Intent i = new Intent(this, ListingDetailActivity.class);
        i.putExtra(EXTRA_DATABASE, database);
        i.putExtra(EXTRA_LISTING, listing);
        i.putExtra(EXTRA_SELLER, seller);

        startActivity(i);
    }

    private void updatePhotoButtons() {
        // TODO get real data here
        // numberOfPhotos = listing.getNumberOfPhotos();
        // ArrayList<int> listingPhotoResIds = listing.getPhotos();

        // Fill pics if they already exist
        for (int i = 0; i<3; i++) {
            if (i < numberOfPhotos) {
                //listingImageViews.get(i).setImageResource(listingPhotoResIds.get(i));
                //TODO this is a dummy, get rid of it
                listingImageViews.get(i).setImageResource(R.drawable.ic_photo_primary_light_24dp);
            } else if (i == numberOfPhotos) {
                listingImageViews.get(i).setImageResource(R.drawable.ic_add_photo_white_24dp);
                currentPhotoAdder = listingImageViews.get(i);
            } else {
                listingImageViews.get(i).setImageResource(R.drawable.ic_photo_primary_light_24dp);
            }
        }
    }

    private void setUpView (boolean addingListing) {
        listingImageViews = new ArrayList<>(3);

        listingImageViews.add(mPhoto1);
        listingImageViews.add(mPhoto2);
        listingImageViews.add(mPhoto3);

        mPriceInput.setText("R ");
        Selection.setSelection(mPriceInput.getText(), mPriceInput.getText().length());

        mPriceInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("R ")) {
                    mPriceInput.setText("R ");
                    Selection.setSelection(mPriceInput.getText(), mPriceInput.getText().length());

                }

            }
        });

        mISBNInput.setText("ISBN ");
        Selection.setSelection(mISBNInput.getText(), mISBNInput.getText().length());

        mISBNInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("ISBN ")) {
                    mISBNInput.setText("ISBN ");
                    Selection.setSelection(mISBNInput.getText(), mISBNInput.getText().length());

                }

            }
        });

        if (! addingListing) { //We're editing
            fillViewFields(listing);
        }

    }

    private void fillViewFields(Listing listing) {
        mTitleInput.setText(listing.getName());
        mDescriptionInput.setText(listing.getDescription());
        mPriceInput.setText("R " + listing.getPrice());
        mISBNInput.setText("ISBN " + listing.getISBN());
    }


}
