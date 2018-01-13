package io.quillo.quillo.data;

/**
 * Created by Stickells on 13/01/2018.
 */

public class User {

    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String universityId;
    private String majorId;

    //TODO Add in universityId and majorId when these get implemented
    public User(String userId, String name, String email, String phoneNumber) { //, String universityId, String majorId) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
//        this.universityId = universityId;
//        this.majorId = majorId;

}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

}
