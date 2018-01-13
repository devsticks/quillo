package io.quillo.quillo.data;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by Stickells on 13/01/2018.
 */

public class Listing {
    private String name;
    private String description;
    private String sellerId;
    private String listingId;
    private int price;
    private String ISBN;

    private int dateListed;
    private int numberOfViews;

    private int colorResource;

    //    public Listing (String name, String description, String sellerId, String listingId, int price, String ISBN, int dateListed, int numberOfViews, int colorResource) {
    public Listing (String name, String description, int colorResource) {
        this.name = name;
        this.description = description;
//        this.sellerId = sellerId;
//        this.listingId = listingId;
//        this.price = price;
//        this.ISBN = ISBN;
//        Date date = new Date();
//        this.dateListed = date.hashCode();
//        this.numberOfViews = numberOfViews;
        this.colorResource = colorResource;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
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

    public int getColorResource() {
        return colorResource;
    }

    public void setColorResource(int colorResource) {
        this.colorResource = colorResource;
    }
}
