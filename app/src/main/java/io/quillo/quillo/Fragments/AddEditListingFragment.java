package io.quillo.quillo.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.DatabaseContract;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.utils.FirebaseHelper;
import io.quillo.quillo.utils.GlideApp;

/**
 * Created by shkla on 2018/01/22.
 */
//TODO: Make this class look nice
public class AddEditListingFragment extends Fragment implements SelectPhotoDialog.OnPhotoSelectedListener{
    private static final int RC_PERMISSIONS = 1;
    public static final String FRAGMENT_NAME = AddEditListingFragment.class.getName();

    private Listing listing;
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
    @BindView(R.id.input_university)
    AutoCompleteTextView universityInput;
    @BindView(R.id.imv_listing_photo_1)
    ImageView photo1;
    @BindView(R.id.btn_publish)
    Button publishButton;
    @BindView(R.id.fab_add_photo)
    FloatingActionButton addPhotoButton;

    private Drawable addPictureIcon;
    private Drawable defaultBookIcon;
    private final String defaultTag = "default";
    private final String notDefaultTag = "notDefault";

    public static AddEditListingFragment newInstance(){
        AddEditListingFragment addEditListingFragment = new AddEditListingFragment();
        return  addEditListingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addPictureIcon = getResources().getDrawable(R.drawable.ic_add_photo_primary_24dp);

        View view = inflater.inflate(R.layout.fragment_add_edit_listing, container, false);
        ButterKnife.bind(this, view);
        setUpView();

        Bundle bundle = getArguments();

        if(bundle != null && bundle.containsKey(IntentExtras.EXTRA_LISTING)){
            listing = (Listing)bundle.getSerializable(IntentExtras.EXTRA_LISTING);
            isInEditMode = true;
            bindListingToViews();

            if (!isInEditMode) {
                photo1.setTag(defaultTag);
            } else {
                photo1.setTag(notDefaultTag);
            }
        }

        return view;
    }



    @Override
    public void getImagePath(Uri imagePath) {
        photo1.setTag("Photo 1 Tag");
        GlideApp.with(this).load(imagePath).into(photo1);
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        photo1.setImageBitmap(bitmap);

    }


    @OnClick({R.id.imv_listing_photo_1, R.id.fab_add_photo})
    public void handleAddPhotoClick() {
        showPhotoDialog();
    }

    private void showPhotoDialog(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        1);
            }
        }

        SelectPhotoDialog dialog = new SelectPhotoDialog();
        dialog.setTargetFragment(AddEditListingFragment.this, 1);
        dialog.show(getActivity().getSupportFragmentManager(), "Select Photo");

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
//        ProgressDialog progressDialog = new ProgressDialog(getActivity(),
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Publishing...");
//        progressDialog.show();

        HashMap<String, String> fields = getFields();
        if (isInEditMode) { // Editing Listing
            if(fields == null){
                return;
            }
            publishButton.setEnabled(false);

            listing.setName(fields.get(DatabaseContract.FIREBASE_LISTING_NAME));
            listing.setAuthor(fields.get(DatabaseContract.FIREBASE_LISTING_AUTHOR));
            listing.setEdition(Integer.parseInt(fields.get(DatabaseContract.FIREBASE_LISTING_EDITION)));
            listing.setDescription(fields.get(DatabaseContract.FIREBASE_LISTING_DESCRIPTION));
            listing.setPrice(Integer.parseInt(fields.get(DatabaseContract.FIREBASE_LISTING_PRICE)));
            listing.setIsbn(fields.get(DatabaseContract.FIREBASE_LISTING_ISBN));
            listing.setUniversityUid(fields.get(DatabaseContract.FIREBASE_LISTING_UNIVERSITY_UID));

            ((MainActivity)getActivity()).quilloDatabase.updateListing(listing, getBytesFromBitmap(getBitmapFromPhoto(), 90), new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getContext(), "Listing updated", Toast.LENGTH_SHORT);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

        } else { // Adding Listing

            if (fields == null) {
                return;
            }

            Calendar calendar = Calendar.getInstance();
            long secondsSince1970 = calendar.getTimeInMillis();



            final Listing newListing = new Listing(fields.get(DatabaseContract.FIREBASE_LISTING_NAME),
                    fields.get(DatabaseContract.FIREBASE_LISTING_AUTHOR),
                    Integer.parseInt(fields.get(DatabaseContract.FIREBASE_LISTING_EDITION)),
                    fields.get(DatabaseContract.FIREBASE_LISTING_DESCRIPTION),
                    fields.get(DatabaseContract.FIREBASE_LISTING_SELLER_UID),
                    Integer.parseInt(fields.get(DatabaseContract.FIREBASE_LISTING_PRICE)),
                    fields.get(DatabaseContract.FIREBASE_LISTING_ISBN),
                    secondsSince1970,
                    fields.get(DatabaseContract.FIREBASE_LISTING_UNIVERSITY_UID));
            listing = newListing;

//            if (photo1.getTag().equals(defaultTag)) {
//                photo1.setImageResource(R.drawable.ic_open_book);
//            }

            final Drawable.ConstantState currentImage = photo1.getDrawable().getConstantState();
            Drawable defaultPicture = getResources().getDrawable(R.drawable.ic_add_photo_primary_24dp);
            final Drawable.ConstantState defaultImage = defaultPicture.getConstantState();

            if (currentImage == defaultImage) {
                photo1.setImageResource(R.drawable.ic_open_book);
            }
            publishButton.setEnabled(false);
            FirebaseHelper.getCurrentFirebaseUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                            ((MainActivity)getActivity()).quilloDatabase.addListing(newListing, getBytesFromBitmap(getBitmapFromPhoto(), 80));

                            ((MainActivity)getActivity()).quilloDatabase.loadPerson(FirebaseHelper.getCurrentUserUid(), new PersonListener() {
                                @Override
                                public void onPersonLoaded(Person person) {
                                    if (person.getPhone() == null || person.getPhone().isEmpty()){
                                        showPhoneInputDialog();
                                    } else{
                                        navigateBack();
                                    }

                                }
                            });


                        }else{
                            showEmailNotVerifiedAlert();
                            publishButton.setEnabled(true);
                        }

                    }else{

                    }

                }
            });




        }
//        progressDialog.cancel();
//        ((MainActivity)getActivity()).pop();


    }

    private void navigateBack(){

        Toast.makeText(getContext(), "Listing saved", Toast.LENGTH_SHORT).show();
        ((MainActivity) getActivity()).showProfileFragment(false);
        photo1.setTag(notDefaultTag);

    }

    private void showPhoneInputDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("You dont have a number");
        builder.setMessage("Buyers are more likely to contact you if you have a phone number");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_number, (ViewGroup)getView(), false);
        final EditText dialogPhoneInput = (EditText)view.findViewById(R.id.input_number);



        builder.setView(view);

        builder.setPositiveButton("Save Number", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final String number = dialogPhoneInput.getText().toString();
                if(number == null || number.isEmpty()){
                    Toast.makeText(getContext(), "Invalid Number", Toast.LENGTH_SHORT).show();
                    showPhoneInputDialog();
                    dialogInterface.cancel();
                    return;
                }
                ((MainActivity)getActivity()).quilloDatabase.updateCurrentUsersNumber(number);
                navigateBack();


            }
        });

        builder.setNegativeButton("Don't save number", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                navigateBack();
            }
        });

        builder.show();
    }

    private void showEmailNotVerifiedAlert(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alertDialog.setTitle("Email not verified");
        alertDialog.setMessage("We have sent an email to: " + FirebaseHelper.getCurrentFirebaseUser().getEmail() + "\nPlease verify your email before adding a listing");
        alertDialog.setPositiveButton("Send Verification again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseHelper.getCurrentFirebaseUser().sendEmailVerification();
            }
        });
        alertDialog.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
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


    private void setupPriceInput(){
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
    }

    private void setupUniversityInput(){
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String universityUid = sharedPreferences.getString(getString(R.string.shared_pref_university_key), null);
        if(universityUid != null){
            universityInput.setText(universityUid);
        }
        universityInput.setAdapter(FirebaseHelper.getSupportedUniversitiesAdapter(getActivity()));

    }

    private void setupISBNInput(){
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
    }

    private void setUpView() {
        setupUniversityInput();
        setupPriceInput();
        setupISBNInput();

    }

    private void bindListingToViews() {
        if (listing.getImageUrl() != null){
            Glide.with(getContext()).load(listing.getImageUrl()).into(photo1);
        }

        titleInput.setText(listing.getName());
        authorInput.setText(listing.getAuthor());
        editionInput.setText(String.valueOf(listing.getEdition()));
        descriptionInput.setText(listing.getDescription());
        priceInput.setText("R " + listing.getPrice());
        isbnInput.setText("ISBN " + listing.getIsbn());
        universityInput.setText(listing.getUniversityUid());
    }
    //TODO: Remove this function and replace it with fieldsAreValid -> Bool
    public HashMap<String, String> getFields() {

        //TODO: Verify that a photo was selected

        String title = titleInput.getText().toString();
        String author = authorInput.getText().toString();
        String edition = editionInput.getText().toString();
        String price = priceInput.getText().toString().substring(2);
        String isbn = isbnInput.getText().toString().substring(5);
        String description = descriptionInput.getText().toString();
        String university = universityInput.getText().toString();

        ArrayList<String> supportedUniversities  = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.universities)));

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

        if(university.isEmpty()|| !supportedUniversities.contains(university)){
            universityInput.setError("Invalid University");
            return null;
        }else{
            universityInput.setError(null);
        }

        HashMap<String, String> fields = new HashMap<>();
        fields.put(DatabaseContract.FIREBASE_LISTING_NAME, title);
        fields.put(DatabaseContract.FIREBASE_LISTING_AUTHOR, author);
        fields.put(DatabaseContract.FIREBASE_LISTING_EDITION, edition);
        fields.put(DatabaseContract.FIREBASE_LISTING_PRICE, price);
        fields.put(DatabaseContract.FIREBASE_LISTING_ISBN, isbn);
        fields.put(DatabaseContract.FIREBASE_LISTING_SELLER_UID, FirebaseHelper.getCurrentUserUid());
        fields.put(DatabaseContract.FIREBASE_LISTING_DESCRIPTION, description);
        fields.put(DatabaseContract.FIREBASE_LISTING_UNIVERSITY_UID, university);

        return fields;
    }

    //Image handling

}
