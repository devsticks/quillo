package io.quillo.quillo.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.quillo.quillo.interfaces.PersonListener;

/**
 * Created by shkla on 2018/01/26.
 */

public class FirebaseHelper {

    public static FirebaseUser getCurrentFirebaseUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getCurrentUserUid(){
        FirebaseUser user = getCurrentFirebaseUser();
        if(user != null){
            return user.getUid();
        }else{
            return null;
        }
    }

    public static void loadPerson(String personUid, final PersonListener oneTimePersonListener){

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
        DatabaseReference databasePersonRef = FirebaseDatabase.getInstance().getReference().child(DatabaseContract.FIREBASE_PERSON_CHILD_NAME);

        databasePersonRef.child(personUid).addListenerForSingleValueEvent(personValueListener);

    }


}
