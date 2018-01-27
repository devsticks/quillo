package io.quillo.quillo.data;

/**
 * Created by shkla on 2018/01/21.
 */

public class DatabaseContract {

    public static final String FIREBASE_LISTINGS_CHILD_NAME = "listings";
    public static final String FIREBASE_LISTING_NAME = "name";
    public static final String FIREBASE_LISTING_AUTHOR = "author";
    public static final String FIREBASE_LISTING_EDITION = "edition";
    public static final String FIREBASE_LISTING_DESCRIPTION = "description";
    public static final String FIREBASE_LISTING_SELLER_UID = "sellerUid";
    public static final String FIREBASE_LISTING_PRICE =  "price";
    public static final String FIREBASE_LISTING_UID =  "uid";
    public static final String FIREBASE_LISTING_ISBN = "isbn";
    public static final String FIREBASE_LISTING_DATELISTED = "dateListed";
    public static final String FIREBASE_LISTING_UNIVERSITY_UID = "universityUid";
    public static final String FIREBASE_LISTING_IMAGE_URL = "imageUrl";

    public static final String FIREBASE_PERSON_CHILD_NAME = "people";
    public static final String FIREBASE_PERSON_EMAIL = "email";
    public static final String FIREBASE_PERSON_UID = "uid";
    public static final String FIREBASE_PERSON_NAME = "name";
    public static final String FIREBASE_PERSON_PHONE = "phone";

    public static final String FIREBASE_PERSON_LISTINGS_CHILD_NAME = "user_listings";
    public static final String FIREBASE_USER_BOOKMARKS_CHILD_NAME = "user_bookmarks";

    public static final String FIREBASE_STORAGE_LISTING_PHOTOS_CHILD_NAME = "listings";
    public static final String FIREBASE_STORAGE_PEOPLE_PHOTOS_CHILD_NAME = "people";
}
