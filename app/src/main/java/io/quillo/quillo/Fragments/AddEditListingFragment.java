package io.quillo.quillo.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.data.DatabaseContract;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.views.SelectPhotoDialog;

/**
 * Created by shkla on 2018/01/22.
 */

public class AddEditListingFragment extends Fragment implements SelectPhotoDialog.OnPhotoSelectedListener{
    private static final int RC_PERMISSIONS = 1;

    private QuilloDatabase quilloDatabase;
    private Listing listing;
    private Person seller;
    ArrayList<ImageView> listingImageViews;
    ImageView currentPhotoAdder;
    int numberOfPhotos = 0;

    //TODO actually check if a listing is being added or edited
    private boolean isInEditMode = false;

    @BindView(R.id.input_title)
    TextInputEditText titleInput;
    @BindView(R.id.input_description)
    TextInputEditText descriptionInput;
    @BindView(R.id.input_author)
    TextInputEditText authorInput;
    @BindView(R.id.input_edition)
    TextInputEditText editionInput;
    @BindView(R.id.input_isbn)
    TextInputEditText isbnInput;
    @BindView(R.id.input_price)
    TextInputEditText priceInput;
    @BindView(R.id.imv_listing_photo_1)
    ImageView photo1;
//    @BindView(R.id.imv_listing_photo_2)
//    ImageView photo2;
//    @BindView(R.id.imv_listing_photo_3)
//    ImageView photo3;

    public static AddEditListingFragment newInstance(){
        AddEditListingFragment addEditListingFragment = new AddEditListingFragment();
        return  addEditListingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quilloDatabase = new QuilloDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_edit_listing, container, false);
        ButterKnife.bind(this, view);
        setUpView(view);
        return view;
    }

    @Override
    public void getImagePath(Uri imagePath) {
        Glide.with(this).load(imagePath).into(photo1);
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        photo1.setImageBitmap(bitmap);
    }


    @OnClick({R.id.imv_listing_photo_1})//, R.id.imv_listing_photo_2, R.id.imv_listing_photo_3})
    public void handleAddPhotoClick(View v) {
//        if (currentPhotoAdder != null && v.getId() == currentPhotoAdder.getId()) {
//            showPhotoDialog();
//            numberOfPhotos++;
//            updatePhotoButtons();
//            if (numberOfPhotos > 2) {
//                currentPhotoAdder = null;
//            }
//        }

        showPhotoDialog();
    }

    private void showPhotoDialog(){
        SelectPhotoDialog dialog = new SelectPhotoDialog();
        dialog.show(getFragmentManager(), "Select Photo");
    }

    private void verifyPermissions() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {


        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    @OnClick(R.id.btn_publish)
    public void handlePublishClick(View v) {
        HashMap<String, String> fields = getFields();
        if (isInEditMode) { // Editing Listing

        } else { // Adding Listing
            if (fields == null) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            long secondsSince1970 = calendar.getTimeInMillis();

            Listing newListing = new Listing(fields.get(DatabaseContract.FIREBASE_LISTING_NAME),
                    fields.get(DatabaseContract.FIREBASE_LISTING_AUTHOR),
                    Integer.parseInt(fields.get(DatabaseContract.FIREBASE_LISTING_EDITION)),
                    fields.get(DatabaseContract.FIREBASE_LISTING_DESCRIPTION),
                    fields.get(DatabaseContract.FIREBASE_LISTING_SELLERUID),
                    Integer.parseInt(fields.get(DatabaseContract.FIREBASE_LISTING_PRICE)),
                    fields.get(DatabaseContract.FIREBASE_LISTING_ISBN),
                    secondsSince1970);
            listing = newListing;
            quilloDatabase.addListing(newListing, getBytesFromBitmap(getBitmapFromPhoto(), 50));
        }

        startListingDetailFragment(v);
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

    public void startListingDetailFragment(View viewRoot) {
        //TODO Use fragments

        /*Intent intent = new Intent(this, ListingDetailActivity.class);
        intent.putExtra(IntentExtras.EXTRA_LISTING, listing);
        intent.putExtra(IntentExtras.EXTRA_SELLER, seller);
        startActivity(intent);*/
    }

    private void updatePhotoButtons() {
        // TODO For when you can add more photos...
        // numberOfPhotos = listing.getNumberOfPhotos();
        // ArrayList<int> listingPhotoResIds = listing.getPhotos();

        // Fill pics if they already exist
//        for (int i = 0; i < 3; i++) {
//            if (i < numberOfPhotos) {
//                //listingImageViews.get(i).setImageResource(listingPhotoResIds.get(i));
//                //TODO this is a dummy, get rid of it
//                listingImageViews.get(i).setImageResource(R.drawable.ic_photo_primary_light_24dp);
//            } else if (i == numberOfPhotos) {
//                listingImageViews.get(i).setImageResource(R.drawable.ic_add_photo_white_24dp);
//                currentPhotoAdder = listingImageViews.get(i);
//            } else {
//                listingImageViews.get(i).setImageResource(R.drawable.ic_photo_primary_light_24dp);
//            }
//        }
    }

    private void setUpView(View view) {

        listingImageViews = new ArrayList<>(3);
        listingImageViews.add(photo1);

        //TODO setImageResource here can go when we can add 3 photos. Happens in updatePhotoButtons
        listingImageViews.get(0).setImageResource(R.drawable.ic_add_photo_primary_24dp);
//        listingImageViews.add(photo2);
//        listingImageViews.add(photo3);
//        currentPhotoAdder = listingImageViews.get(0);

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

        if (isInEditMode) { //We're editing
            fillViewFields(listing);
        }

    }

    private void fillViewFields(Listing listing) {
        titleInput.setText(listing.getName());
        authorInput.setText(listing.getAuthor());
        editionInput.setText(listing.getEdition());
        descriptionInput.setText(listing.getDescription());
        priceInput.setText("R " + listing.getPrice());
        isbnInput.setText("ISBN " + listing.getIsbn());
    }

    public HashMap<String, String> getFields() {


        String title = titleInput.getText().toString();
        String author = authorInput.getText().toString();
        String edition = editionInput.getText().toString();
        String price = priceInput.getText().toString().substring(2);
        String isbn = isbnInput.getText().toString().substring(5);
        String description = descriptionInput.getText().toString();

        if (title.isEmpty()) {
            titleInput.setError("enter a title for your listing");
            return null;
        } else {
            titleInput.setError(null);
        }

        if (author.isEmpty()) {
            authorInput.setError("enter an author for your listing");
            return null;
        } else {
            authorInput.setError(null);
        }

        if (edition.isEmpty()) {
            editionInput.setError("enter an edition for your listing");
            return null;
        } else {
            editionInput.setError(null);
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
        fields.put(DatabaseContract.FIREBASE_LISTING_AUTHOR, author);
        fields.put(DatabaseContract.FIREBASE_LISTING_EDITION, edition);
        fields.put(DatabaseContract.FIREBASE_LISTING_PRICE, price);
        fields.put(DatabaseContract.FIREBASE_LISTING_ISBN, isbn);
        fields.put(DatabaseContract.FIREBASE_LISTING_DESCRIPTION, description);

        return fields;
    }


}
