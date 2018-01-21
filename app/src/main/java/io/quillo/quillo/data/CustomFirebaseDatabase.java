package io.quillo.quillo.data;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import io.quillo.quillo.interfaces.ListingsListener;
import io.quillo.quillo.interfaces.SellerListener;
import io.quillo.quillo.interfaces.SellerListingsListener;

/**
 * Created by Stickells on 13/01/2018.
 */

public class CustomFirebaseDatabase {

    private List<Listing> listings;
    private List<Person> users;

    private ListingsListener listingsListener;
    private SellerListener sellerListener;
    private SellerListingsListener sellerListingsListener;

    private DatabaseReference database;
    private DatabaseReference databaseListingsRef;
    private DatabaseReference databaseUserListingsRef;
    private DatabaseReference databaseUserRef;

    public CustomFirebaseDatabase() {
        listings = new ArrayList<Listing> ();
        listings.add( new Listing("1 Calculus 101", "The only maths textbook you'll ever need.", "1", "1", 100, "11111", "Author 1") );
        listings.add( new Listing("Intro to Signals & Systems","The textbook for the hardest course you're going to do in your life. Ever.", "2", "2", 200, "22222", "Author 2"));
        listings.add( new Listing("Philosophy for Geniuses","Blah blah blah blah blah blah blah blah blah", "3", "3", 300, "333333", "Author 3"));
        listings.add( new Listing("A Guide to Cryptocurrencies", "Cryptos are the future. Learn how to HODL to the moon, buy your lambo, invest in ICOs and sell during a crash.", "3", "4", 400, "444444", "Author 4"));
        listings.add( new Listing("Random Book Five", "This is a lengthy description which is intended to fill views and test the UI. lakjsfdlkajsdlkjadlkj lkajsdlk lkjlkj lkd lkj lkj jk lakjs dlkj  ljksadklj  alkja skjdl lksjad lkjsdj alkdjs askdjla lksjadlakjsdlkajsd alksdk", "3", "5", 500, "555555", "Author 5"));

        users = new ArrayList<>();
        users.add(new Person("1", "Dev", "sticks@gmail.com","08321234"));
        users.add(new Person("2", "Amy", "amy@gmail.com","08321234"));
        users.add(new Person("3", "Tom", "tom@gmail.com",null));
        users.add(new Person("4", "Tamir", "tamir@gmail.com","08321234"));
        users.add(new Person("5", "Senyo", "senyo@gmail.com","0234987298"));

        database = FirebaseDatabase.getInstance().getReference();
        databaseListingsRef = database.child(DatabaseContract.FIREBASE_LISTINGS_CHILD_NAME);

    }

    public void queryListings(String selection){
        if (selection.isEmpty()){
            Query emptySearchQuery = databaseListingsRef.limitToFirst(50);
            emptySearchQuery.addChildEventListener(getListingChildEventListener());
        }else{

        }
    }

    private ChildEventListener getListingChildEventListener(){
        final ChildEventListener listingEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Listing newListing = dataSnapshot.getValue(Listing.class);
                Log.d(CustomFirebaseDatabase.class.getName(), "onChildAdded: "+ newListing.getName());
                listingsListener.onListingLoaded(newListing);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        return listingEventListener;
    }




    public void setListingsListener(ListingsListener listingsListener) {
        this.listingsListener = listingsListener;
    }

    public void setSellerListener(SellerListener sellerListener) {
        this.sellerListener = sellerListener;
    }

    public void setSellerListingsListener(SellerListingsListener sellerListingsListener) {
        this.sellerListingsListener = sellerListingsListener;
    }

    // Fetches the info for a specific listing from Firebase and returns a Listing object
    public Listing getListingById(String listingId) {
        for (int i=0; i < listings.size(); i++) {
            if (listings.get(i).getUid().equals(listingId)) {
                return listings.get(i);
            }
        }

        return null;
    }

    public void observeListings() {
        for (int i=0; i < listings.size(); i++) {
            listingsListener.onListingLoaded(listings.get(i));
        }
    }

    public void observeListingsOfSeller(String sellerId) {
        for (int i=0; i < listings.size(); i++) {
            if (listings.get(i).getSellerUid().equals(sellerId)) {
                sellerListingsListener.onSellerListingLoaded(listings.get(i));
            }
        }
    }

    public void observeUser(String userId) {
        for (int i=0; i < users.size(); i++) {
            if (users.get(i).getUid().equals(userId)) {
                sellerListener.onSellerLoaded(users.get(i));
            }
        }
    }

    public void addListing(Listing listing) {
        listings.add(listing);
        listingsListener.onListingLoaded(listing);
        sellerListingsListener.onSellerListingLoaded(listing);
    }

    public void deleteListing(Listing listing) {

    }

    public void insertListing(Listing listing) {

    }

    public void updateListing(Listing listing) {

        listingsListener.onListingUpdated(listing);
        sellerListingsListener.onSellerListingUpdated(listing);

    }

    public void addBookmark(Listing listing) {

    }

    public void removeBookmark(Listing listing) {

    }
}
