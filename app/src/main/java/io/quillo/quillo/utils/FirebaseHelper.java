package io.quillo.quillo.utils;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.quillo.quillo.R;

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

    public static ArrayAdapter<String> getSupportedUniversitiesAdapter(Activity activity){
        String[] universities = activity.getResources().getStringArray(R.array.universities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1,universities);
        return adapter;
    }

}
