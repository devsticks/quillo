package io.quillo.quillo.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import io.quillo.quillo.interfaces.PasswordListener;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.interfaces.PersonListingsListener;
import io.quillo.quillo.utils.FirebaseHelper;

/**
 * Created by Stickells on 13/01/2018.
 */

public class QuilloDatabase {

    private List<Listing> listings;
    private List<Person> users;




    private BookmarkListener bookmarkListener;

    private DatabaseReference database;
    private DatabaseReference databaseListingsRef;
    private DatabaseReference databasePersonListingsRef;
    private DatabaseReference databasePersonRef;
    private DatabaseReference databaseBookmarksRef;
    private DatabaseReference databaseElasticSearchRef;

    private StorageReference storageReference;
    private StorageReference storageListingRef;
    private StorageReference storagePeopleRef;

    private String mElasticSearchPassword;

    public QuilloDatabase() {
        database = FirebaseDatabase.getInstance().getReference();
        databaseListingsRef = database.child(DatabaseContract.FIREBASE_LISTINGS_CHILD_NAME);
        databasePersonRef = database.child(DatabaseContract.FIREBASE_PERSON_CHILD_NAME);
        databasePersonListingsRef = database.child(DatabaseContract.FIREBASE_PERSON_LISTINGS_CHILD_NAME);
        databaseBookmarksRef = database.child(DatabaseContract.FIREBASE_USER_BOOKMARKS_CHILD_NAME);
        databaseElasticSearchRef = database.child(DatabaseContract.FIREBASE_ELASTIC_SEARCH_CHILD_NAME);

        storageReference = FirebaseStorage.getInstance().getReference();
        storageListingRef = storageReference.child(DatabaseContract.FIREBASE_STORAGE_LISTING_PHOTOS_CHILD_NAME);
        storagePeopleRef = storageReference.child(DatabaseContract.FIREBASE_STORAGE_PEOPLE_PHOTOS_CHILD_NAME);

    }

    public void setBookmarkListener(BookmarkListener bookmarkListener){
        this.bookmarkListener = bookmarkListener;
    }

    private Query listingQuery;
    private ChildEventListener listingChildEventListener;

    public void observeListings(String universityUid, final ListingsListener listingsListener){
       listingQuery = databaseListingsRef.limitToFirst(50);


        listingChildEventListener =  new ChildEventListener() {
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
        };

        listingQuery.addChildEventListener(listingChildEventListener);

    }



    public void loadListing(String listingUid, final ListingsListener listingsListener){
        databaseListingsRef.child(listingUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentUserUid = FirebaseHelper.getCurrentUserUid();
                final Listing listing = dataSnapshot.getValue(Listing.class);
                if (listing == null){
                    return;
                }
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
                databaseListingsRef.child(listingUid).setValue(listing.toMap());
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
        String currentUserUid = FirebaseHelper.getCurrentUserUid();

        if(currentUserUid != null){
            databaseListingsRef.child(listing.getUid()).removeValue();
            databasePersonListingsRef.child(currentUserUid).child(listing.getUid()).removeValue();
            storageListingRef.child(listing.getUid()).delete();
            deleteListingFromBookmarks(listing);
        }

        //TODO do we need to tell the recyclerView adapters that the listing was removed?
        //adapter.notifyItemRemoved vibe

    }

    private void deleteListingFromBookmarks(final Listing listing){
        databaseBookmarksRef.orderByChild(listing.getUid()).equalTo(listing.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    databaseBookmarksRef.child(child.getKey()).child(listing.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateListing(final Listing listing, byte[] uploadBytes, final OnSuccessListener successListener){
        storageListingRef.child(listing.getUid()).putBytes(uploadBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listing.setImageUrl(downloadUrl.toString());
                databaseListingsRef.child(listing.getUid()).setValue(listing.toMap());
                successListener.onSuccess(true);
            }
        });

    }


    private ChildEventListener personListingsEventListener;

    public void observePersonListings(String personUid, final PersonListingsListener personListingsListener){


         personListingsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                databaseListingsRef.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Listing listing = dataSnapshot.getValue(Listing.class);
                        if (listing == null){
                            return;
                        }
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
                                    personListingsListener.onPersonListingLoaded(listing);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else{
                            personListingsListener.onPersonListingLoaded(listing);
                        }
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
                databaseListingsRef.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Listing listing = dataSnapshot.getValue(Listing.class);
                        personListingsListener.onPersonListingRemoved(listing);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

         databasePersonListingsRef.child(personUid).addChildEventListener(personListingsEventListener);

    }

    public void stopObservingPersonListings(String personUid){
        databasePersonListingsRef.child(personUid).removeEventListener(personListingsEventListener);
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
                            if (listing != null) {
                                bookmarkListener.onBookmarkAdded(listing);
                            } else { //listing likely deleted, but still held in bookmarks
                                //TODO clean up user bookmarks and delete listings that no longer exist
                            }
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

    public void observePerson(String personUid, final PersonListener personListener) {
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
                if (person != null) {
                    oneTimePersonListener.onPersonLoaded(person);
                } else {
                    //TODO catch this as far as UI goes...
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        databasePersonRef.child(personUid).addListenerForSingleValueEvent(personValueListener);

    }

    public void addPerson(final Person person){
        FirebaseUser currentUser = FirebaseHelper.getCurrentFirebaseUser();

        if (currentUser != null){
            databasePersonRef.child(person.getUid()).setValue(person);
        }

    }

    public void updatePerson(final Person person, byte[] uploadBytes, final OnSuccessListener onSuccessListener){
        final FirebaseUser currentUser = FirebaseHelper.getCurrentFirebaseUser();

        if(!currentUser.getDisplayName().equals(person.getName())){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(person.getName())
                    .build();
           currentUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {

                    }
                }
            });
        }

        if(!currentUser.getEmail().equals(person.getEmail())){
            currentUser.updateEmail(person.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    currentUser.sendEmailVerification();
                                }
                            }
                        });
                    }else{
                        Log.e(QuilloDatabase.class.getName(), "Could not updaate email : ");
                        onSuccessListener.onSuccess(false);
                        return;

                    }
                }
            });

        }

        storagePeopleRef.child(person.getUid()).putBytes(uploadBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                person.setImageUrl(downloadUrl.toString());
                databasePersonRef.child(person.getUid()).setValue(person);
                onSuccessListener.onSuccess(true);
            }
        });

    }

    //TODO: check for better cloud auth methods && consider safety of this method
    public void getElasticSearchPassword(final PasswordListener passwordListener){
        databaseElasticSearchRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    passwordListener.onPasswordLoaded((String) dataSnapshot.getValue());
                }else{
                    passwordListener.onPasswordLoadFailed();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                passwordListener.onPasswordLoadFailed();
            }
        });




    }


    public void loadListingImageData(final Listing listing){
        DatabaseReference imageref = databaseListingsRef.child(listing.getUid()).child("imageUrl");

        imageref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listing.setImageUrl(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
