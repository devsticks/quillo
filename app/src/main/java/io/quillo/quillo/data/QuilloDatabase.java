package io.quillo.quillo.data;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import io.quillo.quillo.interfaces.BookmarkListener;
import io.quillo.quillo.interfaces.ListingsListener;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.interfaces.PersonListingsListener;

/**
 * Created by Stickells on 13/01/2018.
 */

public class QuilloDatabase {

    private List<Listing> listings;
    private List<Person> users;

    private ListingsListener listingsListener;
    private PersonListener personListener;
    private PersonListingsListener personListingsListener;
    private BookmarkListener bookmarkListener;

    private DatabaseReference database;
    private DatabaseReference databaseListingsRef;
    private DatabaseReference databasePersonListingsRef;
    private DatabaseReference databasePersonRef;
    private DatabaseReference databaseBookmarksRef;

    private StorageReference storageReference;
    private StorageReference storageListingRef;

    public QuilloDatabase() {
        database = FirebaseDatabase.getInstance().getReference();
        databaseListingsRef = database.child(DatabaseContract.FIREBASE_LISTINGS_CHILD_NAME);
        databasePersonRef = database.child(DatabaseContract.FIREBASE_PERSON_CHILD_NAME);
        databasePersonListingsRef = database.child(DatabaseContract.FIREBASE_PERSON_LISTINGS_CHILD_NAME);
        databaseBookmarksRef = database.child(DatabaseContract.FIREBASE_USER_BOOKMARKS_CHILD_NAME);

        storageReference = FirebaseStorage.getInstance().getReference();
        storageListingRef = storageReference.child(DatabaseContract.FIREBASE_STORAGE_LISTING_PHOTOS_CHILD_NAME);

    }

    public void setListingsListener(ListingsListener listingsListener) {
        this.listingsListener = listingsListener;
    }

    public void setPersonListener(PersonListener personListener) {
        this.personListener = personListener;
    }

    public void setPersonListingsListener(PersonListingsListener personListingsListener) {
        this.personListingsListener = personListingsListener;
    }

    public void setBookmarkListener(BookmarkListener bookmarkListener){
        this.bookmarkListener = bookmarkListener;
    }


    public void observeListings(String universityUid){
        Query query = databaseListingsRef.limitToFirst(50);


        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Listing listing = dataSnapshot.getValue(Listing.class);

                String currentUserUid = FirebaseHelper.getCurrentUserUid();

                if (currentUserUid != null){
                    databaseBookmarksRef.child(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(listing.getUid())){
                                listing.setBookmarked(true);
                            }else{
                                listing.setBookmarked(false);
                            }
                            listingsListener.onListingLoaded(listing);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    listingsListener.onListingLoaded(listing);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listingsListener.onListingRemoved(dataSnapshot.getValue(Listing.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addListing(final Listing listing, byte[] uploadBytes) {
        final String listingUid = databaseListingsRef.push().getKey();
        listing.setUid(listingUid);
        storageListingRef.child(listingUid).putBytes(uploadBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listing.setImageUrl(downloadUrl.toString());
                databaseListingsRef.child(listingUid).setValue(listing);
                addListingToPersonListingTree(listingUid);
            }
        });

    }

    private void addListingToPersonListingTree(String listingUid){
        String currentUserUid = FirebaseHelper.getCurrentUserUid();
        if (currentUserUid != null){
            databasePersonListingsRef.child(currentUserUid).child(listingUid).setValue(listingUid);
        }
    }

    public void deleteListing(Listing listing) {

    }

    public void updateListing(Listing listing) {

        listingsListener.onListingUpdated(listing);
        personListingsListener.onPersonListingUpdated(listing);

    }

    public void observePersonListings(String personUid){
        databasePersonListingsRef.child(personUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Listing listing = dataSnapshot.getValue(Listing.class);
                personListingsListener.onPersonListingLoaded(listing);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Listing listing = dataSnapshot.getValue(Listing.class);
                personListingsListener.onPersonListingRemoved(listing);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void observeBookmarks(){
        FirebaseUser currentUser = FirebaseHelper.getCurrentFirebaseUser();
        if (currentUser!= null) {
            final ChildEventListener bookmarkEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    databaseListingsRef.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Listing listing = dataSnapshot.getValue(Listing.class);
                            bookmarkListener.onBookmarkAdded(listing);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String listingUid = dataSnapshot.getKey();
                    bookmarkListener.onBookmarkRemoved(listingUid);


                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseBookmarksRef.child(currentUser.getUid()).addChildEventListener(bookmarkEventListener);
        }

    }


    public void addBookmark(Listing listing) {
        FirebaseUser user = FirebaseHelper.getCurrentFirebaseUser();
        if (user != null) {
            databaseBookmarksRef.child(user.getUid()).child(listing.getUid()).setValue(listing.getUid());
        }
    }


    public void removeBookmark(Listing listing) {
        String currentUserUid = FirebaseHelper.getCurrentUserUid();
        if (currentUserUid != null){
            databaseBookmarksRef.child(currentUserUid).child(listing.getUid()).removeValue();
        }
    }

    public void observePerson(String personUid) {
        ValueEventListener personValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                personListener.onPersonLoaded(person);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databasePersonRef.child(personUid).addValueEventListener(personValueEventListener);

    }

    public void loadPerson(String personUid, final PersonListener oneTimePersonListener){

        ValueEventListener personValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                oneTimePersonListener.onPersonLoaded(person);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        databasePersonRef.child(personUid).addListenerForSingleValueEvent(personValueListener);

    }

    public void addPerson(Person person){
        FirebaseUser currentUser = FirebaseHelper.getCurrentFirebaseUser();


        if (currentUser != null){
            databasePersonRef.child(person.getUid()).setValue(person);
        }



    }

}
