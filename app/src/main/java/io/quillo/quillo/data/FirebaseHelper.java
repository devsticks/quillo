package io.quillo.quillo.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

}
