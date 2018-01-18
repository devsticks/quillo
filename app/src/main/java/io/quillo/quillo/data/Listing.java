package io.quillo.quillo.data;

import java.io.Serializable;

/**
 * Created by Stickells on 13/01/2018.
 */

public class Listing implements Serializable {
    private String name;
    private String description;
    private String sellerUid;
    private String uid;
    private int price;
    private String ISBN;
    private String author;

    private int dateListed;
    private int numberOfViews;

    public Listing (String name, String description, String sellerUid, String uid, int price, String ISBN, String author) { //}, int dateListed, int numberOfViews, int colorResource) {
        this.name = name;
        this.description = description;
        this.sellerUid = sellerUid;
        this.uid = uid;
        this.price = price;
        this.ISBN = ISBN;
        this.author = author;
//        Date date = new Date();
//        this.dateListed = date.hashCode();
//        this.numberOfViews = numberOfViews;
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

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getDateListed() {
        return dateListed;
    }

    public void setDateListed(int dateListed) {
        this.dateListed = dateListed;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
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

}
