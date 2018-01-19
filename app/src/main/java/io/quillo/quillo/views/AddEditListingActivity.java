package io.quillo.quillo.views;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;

public class AddEditListingActivity extends AppCompatActivity {

    private CustomFirebaseDatabase customFirebaseDatabase;
    private Listing listing;
    private Person seller;

    @BindView(R.id.input_title) TextInputEditText titleInput;
    @BindView(R.id.input_description) TextInputEditText descriptionInput;
    @BindView(R.id.input_isbn) TextInputEditText isbnInput;
    @BindView(R.id.input_price) TextInputEditText priceInput;
    @BindView(R.id.input_author) TextInputEditText authorInput;

    @BindView(R.id.imv_listing_photo_1) ImageView photo1;
    @BindView(R.id.imv_listing_photo_2) ImageView photo2;
    @BindView(R.id.imv_listing_photo_3) ImageView photo3;

    ArrayList<ImageView> listingImageViews;
    ImageView currentPhotoAdder;
    int numberOfPhotos = 0;
    private boolean addingListing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_listing);
        ButterKnife.bind(this);

        customFirebaseDatabase = new CustomFirebaseDatabase();

        Intent intent  = getIntent();
        Bundle extras = intent.getExtras();
        seller = (Person) intent.getSerializableExtra(IntentExtras.EXTRA_SELLER);
        listing = (Listing) intent.getSerializableExtra(IntentExtras.EXTRA_LISTING);

        if (extras.getSerializable(IntentExtras.EXTRA_LISTING) != null) { //includes listing, so we're editing
            addingListing = false;
            listing = (Listing) intent.getSerializableExtra(IntentExtras.EXTRA_LISTING);
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

        if (!validate()) {
            return;
        }

        if (addingListing) {

            Listing newListing = new Listing(
                    titleInput.getText().toString(),
                    descriptionInput.getText().toString(),
                    seller.getUid(),
                    //TODO Get actual current userUid
                    "1",
                    Integer.parseInt(priceInput.getText().toString().substring(2)),
                    isbnInput.getText().toString().substring(5),
                    authorInput.getText().toString());

            customFirebaseDatabase.addListing(newListing);
            listing = newListing;

        } else { // Updating listing
//            Listing newListing = new Listing(
//                    titleInput.getText().toString(),
//                    descriptionInput.getText().toString(),
//                    seller.getUid(),
//                    "1",
//                    Integer.parseInt(priceInput.getText().toString().substring(2)),
//                    isbnInput.getText().toString().substring(5),
//                    authorInput.getText().toString());
//
//            listing = newListing;

            listing.setName(titleInput.getText().toString());
            listing.setDescription(descriptionInput.getText().toString());
            listing.setPrice(Integer.parseInt(priceInput.getText().toString().substring(2)));
            listing.setISBN(isbnInput.getText().toString().substring(5));
            listing.setAuthor(authorInput.getText().toString());

            customFirebaseDatabase.updateListing(listing);
        }

        startListingDetailActivity(v);
    }

    public void startListingDetailActivity(View viewRoot) {
        Intent intent = new Intent(this, ListingDetailActivity.class);
        intent.putExtra(IntentExtras.EXTRA_LISTING, listing);
        intent.putExtra(IntentExtras.EXTRA_SELLER, seller);

        startActivity(intent);
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

        listingImageViews.add(photo1);
        listingImageViews.add(photo2);
        listingImageViews.add(photo3);

        priceInput.setText("R ");
        Selection.setSelection(priceInput.getText(), priceInput.getText().length());

        priceInput.addTextChangedListener(new TextWatcher() {

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
                    priceInput.setText("R ");
                    Selection.setSelection(priceInput.getText(), priceInput.getText().length());

                }

            }
        });

        isbnInput.setText("ISBN ");
        Selection.setSelection(isbnInput.getText(), isbnInput.getText().length());

        isbnInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("ISBN ")) {
                    isbnInput.setText("ISBN ");
                    Selection.setSelection(isbnInput.getText(), isbnInput.getText().length());

                }

            }
        });

        if (! addingListing) { //We're editing
            fillViewFields(listing);
        }

    }

    private void fillViewFields(Listing listing) {
        titleInput.setText(listing.getName());
        authorInput.setText(listing.getAuthor());
        descriptionInput.setText(listing.getDescription());
        priceInput.setText("R " + listing.getPrice());
        isbnInput.setText("ISBN " + listing.getISBN());
    }

    public boolean validate() {
        boolean valid = true;

        String title = titleInput.getText().toString();
        String author = authorInput.getText().toString();
        String price = priceInput.getText().toString().substring(2);
        String isbn = isbnInput.getText().toString().substring(5);

        if (title.isEmpty()) {
            titleInput.setError("enter a title for your listing");
            valid = false;
        } else {
            titleInput.setError(null);
        }

        if (author.isEmpty()) {
            authorInput.setError("enter a title for your listing");
            valid = false;
        } else {
            titleInput.setError(null);
        }

        if (price.isEmpty()) {
            priceInput.setError("enter a sale price");
            valid = false;
        } else {
            priceInput.setError(null);
        }

        if (isbn.isEmpty() || (isbn.length() != 10 && isbn.length() != 13)) {
            isbnInput.setError("enter a 10- or 13-digit ISBN");
            valid = false;
        } else {
            isbnInput.setError(null);
        }

        return valid;
    }

}
