package io.quillo.quillo.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.DatabaseContract;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;

public class AddEditListingActivity extends AppCompatActivity implements SelectPhotoDialog.OnPhotoSelectedListener  {

    @Override
    public void getImagePath(Uri imagePath) {
        Glide.with(this).load(imagePath).into(photo1);
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        photo1.setImageBitmap(bitmap);
    }

    private static final int RC_PERMISSIONS = 1;

    private CustomFirebaseDatabase customFirebaseDatabase;
    private Listing listing;
    private Person seller;


    ArrayList<ImageView> listingImageViews;
    ImageView currentPhotoAdder;
    int numberOfPhotos = 0;
    private boolean addingListing = false;

    @BindView(R.id.input_title)
    TextInputEditText titleInput;
    @BindView(R.id.input_description)
    TextInputEditText descriptionInput;
    @BindView(R.id.input_isbn)
    TextInputEditText isbnInput;
    @BindView(R.id.input_price)
    TextInputEditText priceInput;
    @BindView(R.id.imv_listing_photo_1)
    ImageView photo1;
    @BindView(R.id.imv_listing_photo_2)
    ImageView photo2;
    @BindView(R.id.imv_listing_photo_3)
    ImageView photo3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_listing);
        ButterKnife.bind(this);

        customFirebaseDatabase = new CustomFirebaseDatabase();

        Intent intent = getIntent();
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
            showPhotoDialog();
            numberOfPhotos++;
            updatePhotoButtons();
            if (numberOfPhotos > 2) {
                currentPhotoAdder = null;
            }
        }
    }

    private void showPhotoDialog(){
        SelectPhotoDialog dialog = new SelectPhotoDialog();
        dialog.show(getSupportFragmentManager(), "Select Photo");


    }

    private void verifyPermissions() {
        String[] permisions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permisions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permisions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permisions[2]) == PackageManager.PERMISSION_GRANTED) {


        } else {
            ActivityCompat.requestPermissions(AddEditListingActivity.this, permisions, RC_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    @OnClick(R.id.btn_publish)
    public void handlePublishClick(View v) {
        HashMap<String, String> fields = getFields();
        if (addingListing) {
            if (fields == null) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            long secondsSince1970 = calendar.getTimeInMillis();

            Listing newListing = new Listing(fields.get(DatabaseContract.FIREBASE_LISTING_NAME),
                    fields.get(DatabaseContract.FIREBASE_LISTING_DESCRIPTION),
                    fields.get(DatabaseContract.FIREBASE_LISTING_SELLERUID),
                    Integer.parseInt(fields.get(DatabaseContract.FIREBASE_LISTING_PRICE)),
                    fields.get(DatabaseContract.FIREBASE_LISTING_ISBN),
                    secondsSince1970);
            listing = newListing;
            customFirebaseDatabase.addListing(newListing, getBytesFromBitmap(getBitmapFromPhoto(), 50));
        } else { // Updating listing

        }
        startListingDetailActivity(v);
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
    private Bitmap getBitmapFromPhoto(){
        photo1.setDrawingCacheEnabled(true);
        photo1.buildDrawingCache();
        return photo1.getDrawingCache();
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
        for (int i = 0; i < 3; i++) {
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

    private void setUpView(boolean addingListing) {
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

        if (!addingListing) { //We're editing
            fillViewFields(listing);
        }

    }

    private void fillViewFields(Listing listing) {
        titleInput.setText(listing.getName());
        descriptionInput.setText(listing.getDescription());
        priceInput.setText("R " + listing.getPrice());
        isbnInput.setText("ISBN " + listing.getISBN());
    }

    public HashMap<String, String> getFields() {


        String title = titleInput.getText().toString();
        String price = priceInput.getText().toString().substring(2);
        Log.e("Wow", "Price: " + price);
        String isbn = isbnInput.getText().toString().substring(5);
        String description = descriptionInput.getText().toString();

        if (title.isEmpty()) {
            titleInput.setError("enter a title for your listing");
            return null;
        } else {
            titleInput.setError(null);
        }

        if (description.isEmpty()) {
            descriptionInput.setError("Enter a description");
            return null;
        } else {
            titleInput.setError(null);
        }

        if (price.isEmpty()) {
            priceInput.setError("enter a sale price");
            return null;
        } else {
            priceInput.setError(null);
        }

        if (isbn.isEmpty() || (isbn.length() != 10 && isbn.length() != 13)) {
            isbnInput.setError("enter a 10- or 13-digit ISBN");
            return null;
        } else {
            isbnInput.setError(null);
        }

        HashMap<String, String> fields = new HashMap<>();
        fields.put(DatabaseContract.FIREBASE_LISTING_NAME, title);
        fields.put(DatabaseContract.FIREBASE_LISTING_PRICE, price);
        fields.put(DatabaseContract.FIREBASE_LISTING_ISBN, isbn);
        fields.put(DatabaseContract.FIREBASE_LISTING_DESCRIPTION, description);

        return fields;
    }


}
