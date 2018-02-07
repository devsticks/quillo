package io.quillo.quillo.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.cketti.mailto.EmailIntentBuilder;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.OneTimeListingListener;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.utils.FirebaseHelper;

/**
 * Created by shkla on 2018/01/25.
 */

public class ListingDetailFragment extends Fragment  {
    //TODO: Fix image scaling

    public static String FRAGMENT_NAME = ListingDetailFragment.class.getName();

    private Person seller;
    private Listing listing;
    private String listingUid;

    private boolean isViewingOwnListing = false;

    @BindView(R.id.fab_listing_action)
    FloatingActionButton listingActionFAB;
    @BindView(R.id.btn_seller_profile)
    View sellerContainerButton;
    @BindView(R.id.div_seller)
    View sellerContainer;
    @BindView(R.id.imv_seller_profile_pic)
    ImageView sellerProfilePic;
    @BindView(R.id.lbl_seller_name)
    TextView sellerNameTV;
    @BindView(R.id.lbl_seller_university)
    TextView sellerUniversityTV;
    @BindView(R.id.div_delete_options)
    View deleteOptionsContainer;

    @BindView(R.id.btn_call)
    View call;
    @BindView(R.id.btn_email)
    View email;
    @BindView(R.id.btn_text)
    View text;
    @BindView(R.id.btn_share)
    View share;
    @BindView(R.id.btn_delete)
    View delete;
    @BindView(R.id.btn_sold)
    View sold;

    @BindView(R.id.lbl_title)
    TextView title;
    @BindView(R.id.lbl_author)
    TextView author;
    @BindView(R.id.lbl_description)
    TextView description;
    @BindView(R.id.lbl_price)
    TextView price;
    @BindView(R.id.imv_listing_image)
    ImageView listingImage;

    @BindView(R.id.ic_text)
    ImageView messageButton;
    @BindView(R.id.lbl_text)
    TextView messageText;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_detail, container, false);

        Bundle bundle = this.getArguments();
        ButterKnife.bind(this, view);
        setupViews();
        listingUid = bundle.getString(IntentExtras.EXTRA_LISTING_UID);
        loadListing();

        return view;
    }

    private void setupViews(){
        listingActionFAB.setVisibility(View.INVISIBLE);
        //sellerContainer.setVisibility(View.GONE);
        deleteOptionsContainer.setVisibility(View.GONE);

        if (whatsAppIsInstalled()){
            messageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_whatsapp));
            messageText.setText("WhatsApp");
        }

    }

    private void bindSellerToViews() {

        sellerNameTV.setText(seller.getName());
        sellerUniversityTV.setText(seller.getUniversityUid());

        if (seller.getImageUrl() != null) {
            Glide.with(getContext()).load(seller.getImageUrl()).into(sellerProfilePic);
        }else{
            sellerProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_24dp));
        }

        setupSellerContainerButtons();

    }

    @OnClick(R.id.fab_listing_action)
    public void handleListingActionClick() {
        if (isViewingOwnListing) {
            //Edit listing action
            AddEditListingFragment addEditListingFragment = new AddEditListingFragment();
            addEditListingFragment.setTargetFragment(this, 1);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentExtras.EXTRA_LISTING, listing);
            addEditListingFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_holder, addEditListingFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_holder).getClass().getName())
                    .commit();

        } else {
            //Bookmark action
            if (listing.isBookmarked()) {

                listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                listing.setBookmarked(false);
                ((MainActivity) getActivity()).quilloDatabase.removeBookmark(listing);
                Toast.makeText(getContext(), "Removed from bookmarks", Toast.LENGTH_SHORT).show();

            } else {

                listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                listing.setBookmarked(true);
                ((MainActivity) getActivity()).quilloDatabase.addBookmark(listing);
                Toast.makeText(getContext(), "Added to bookmarks", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @OnClick(R.id.seller_view_container)
    public void handleSellerProfileClick() {
        Log.d(ListingDetailFragment.class.getName(), "Seller profile click");

        int index = getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (getActivity().getSupportFragmentManager().getBackStackEntryAt(index).getName().equals(ProfileFragment.FRAGMENT_NAME)){
            getActivity().getSupportFragmentManager().popBackStack();
        }else {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentExtras.EXTRA_SELLER, seller);
            profileFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_holder, profileFragment)
                    .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                    .addToBackStack(FRAGMENT_NAME)
                    .commit();
        }
    }

    @OnClick(R.id.btn_delete)
    public void handleDeleteListingClick() {
        ((MainActivity)getActivity()).quilloDatabase.deleteListing(listing);

        Toast.makeText(getContext(), "Listing deleted", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.btn_sold)
    public void handleSoldListingClick() {
        ((MainActivity)getActivity()).quilloDatabase.deleteListing(listing);

        Toast.makeText(getContext(), "Listing marked sold", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    private boolean whatsAppIsInstalled(){
        PackageManager pm = getActivity().getPackageManager();
        boolean appInstalled;

        try{
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        }catch (PackageManager.NameNotFoundException e){
            appInstalled = false;
        }

        return appInstalled;

    }

    private void setupSellerContainerButtons() {
        sellerContainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (seller.getPhoneNumber() != null &&
                !seller.getPhoneNumber().isEmpty() &&
                !seller.getPhoneNumber().equals("")) {

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
                        if (whatsAppIsInstalled()){
                            String number = "+27" + seller.getPhoneNumber().substring(1);
                            String queryText = "Hi " + seller.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo. For R" + listing.getPrice();
                            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+number+"&text="+queryText);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }else{
                            Uri uri = Uri.parse("smsto:" + seller.getPhoneNumber());
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                            smsIntent.putExtra("sms_body", "Hi " + seller.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo.");
                            view.getContext().startActivity(smsIntent);
                        }


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
                        .body("Hi " + seller.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo for R" + listing.getPrice())
                        .start();
                if (!success) {
                    Toast.makeText(view.getContext(),
                            "Email failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        return;

                    }else{
                       shareListing();
                    }
                }



            }
        });
    }
    private void shareListing(){
        String text = "Look at this listing I found on quillo";
        Uri imageUri = getImageUri(getBitmapFromPhoto());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share listing"));
    }

    public Uri getImageUri(Bitmap bitmap){
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Listing", null);
        return Uri.parse(path);
    }

    private void verifyPermissions() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {


        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, 2);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
    private Bitmap getBitmapFromPhoto(){
        listingImage.setDrawingCacheEnabled(true);
        listingImage.buildDrawingCache();
        return listingImage.getDrawingCache();
    }

    private void bindListingToViews() {
        title.setText(listing.getName() + ", " + listing.getEditionOrdinal() + " Edition");
        author.setText(listing.getAuthor());
        description.setText(listing.getDescription());
        price.setText("R " + listing.getPrice());

        if (listing.isBookmarked()) {
            listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
        }

        Glide.with(getContext()).load(listing.getImageUrl()).into(listingImage);
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public void loadListing() {
        ((MainActivity) getActivity()).quilloDatabase.loadListing(listingUid, new OneTimeListingListener() {
            @Override
            public void onListingLoaded(Listing listing) {
                setListing(listing);
                loadSeller();
                bindListingToViews();
            }

            @Override
            public void onListingLoadFail() {
                Toast.makeText(getContext(), "Uh oh something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }
    
    public void loadSeller() {
        if (listing != null) {
            ((MainActivity) getActivity()).quilloDatabase.loadPerson(listing.getSellerUid(), new PersonListener() {
                @Override
                public void onPersonLoaded(Person person) {
                    seller = person;
                    String currentUserUid = FirebaseHelper.getCurrentUserUid();
                    if (currentUserUid != null ){
                        isViewingOwnListing = FirebaseHelper.getCurrentUserUid().equals(listing.getSellerUid());
                    }else{
                        isViewingOwnListing = false;
                        listingActionFAB.setVisibility(View.GONE);
                    }

                    if (isViewingOwnListing) {
                        sellerContainer.setVisibility(View.GONE);
                        deleteOptionsContainer.setVisibility(View.VISIBLE);
                        listingActionFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                    } else {
                        sellerContainer.setVisibility(View.VISIBLE);
                        deleteOptionsContainer.setVisibility(View.GONE);
                        bindSellerToViews();
                    }
                }
            });
        }
        listingActionFAB.setVisibility(View.VISIBLE);
    }
}


