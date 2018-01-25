package io.quillo.quillo.data;

import java.io.Serializable;

/**
 * Created by Stickells on 13/01/2018.
 */

public class Person implements Serializable {

    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private String universityUid;

    //TODO Add in universityUid and majorId when these get implemented
    public Person(String uid, String name, String email, String phoneNumber) { //, String universityUid, String majorId) {

        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
//        this.universityUid = universityUid;
//        this.majorId = majorId;

}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber () {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUniversityUid() {
        return universityUid;
    }

    public void setUniversityUid(String universityUid) {
        this.universityUid = universityUid;
    }

}
