package io.quillo.quillo.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Stickells on 13/01/2018.
 */

public class Listing implements Serializable {


    private String name;
    private String author;
    private int edition;
    private String description;
    private String sellerUid;
    private String universityUid;
    private String uid;
    private int price;
    private String isbn;
    private long dateListed;
    private String imageUrl;

    private boolean isBookmarked;


//    private int numberOfViews;

    public Listing (String name, String author, int edition, String description, String sellerUid, String universityUid, String uid, int price, String isbn, long dateListed, String imageUrl) { //}, int dateListed, int numberOfViews, int colorResource) {
        this.name = name;
        this.author = author;
        this.edition = edition;
        this.description = description;
        this.sellerUid = sellerUid;
        this.universityUid = universityUid;
        this.uid = uid;
        this.price = price;
        this.isbn = isbn;
        this.dateListed = dateListed;
        this.imageUrl = imageUrl;
//        Date date = new Date();
//        this.dateListed = date.hashCode();
//        this.numberOfViews = numberOfViews;
    }

    public Listing (String name, String author, int edition, String description, String sellerUid, int price, String isbn, long dateListed, String universityUid) { //}, int dateListed, int numberOfViews, int colorResource) {
        this.name = name;
        this.author = author;
        this.edition = edition;
        this.description = description;
        this.sellerUid = sellerUid;
        this.price = price;
        this.isbn = isbn;
        this.dateListed = dateListed;
        this.universityUid = universityUid;

    }

    public String getUniversityUid() {
        return universityUid;
    }

    public void setUniversityUid(String universityUid) {
        this.universityUid = universityUid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public Listing(){

    }

    public String getSellerUid() {
        return sellerUid;
    }

    public void setSellerUid(String sellerUid) {
        this.sellerUid = sellerUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public long getDateListed() {
        return dateListed;
    }

    public void setDateListed(long dateListed) {
        this.dateListed = dateListed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(DatabaseContract.FIREBASE_LISTING_UID, uid);
        map.put(DatabaseContract.FIREBASE_LISTING_ISBN, isbn);
        map.put(DatabaseContract.FIREBASE_LISTING_SELLER_UID, sellerUid);
        map.put(DatabaseContract.FIREBASE_LISTING_UNIVERSITY_UID, universityUid);
        map.put(DatabaseContract.FIREBASE_LISTING_PRICE, price);
        map.put(DatabaseContract.FIREBASE_LISTING_DATELISTED, dateListed);
        map.put(DatabaseContract.FIREBASE_LISTING_DESCRIPTION, description);
        map.put(DatabaseContract.FIREBASE_LISTING_IMAGE_URL, imageUrl);
        map.put(DatabaseContract.FIREBASE_LISTING_NAME, name);
        map.put(DatabaseContract.FIREBASE_LISTING_AUTHOR, author);
        map.put(DatabaseContract.FIREBASE_LISTING_EDITION, edition);

        return map;
    }

    public String getEditionOrdinal() {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
            switch (edition % 100) {
                case 11:
                case 12:
                case 13:
                    return edition + "th";
                default:
                    return edition + suffixes[edition % 10];
            }
    }
}
