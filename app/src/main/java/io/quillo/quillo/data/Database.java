package io.quillo.quillo.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.quillo.quillo.R;

/**
 * Created by Stickells on 13/01/2018.
 */

public class Database implements Serializable {

    private static final int sizeOfCollection = 12;
    private Random random;

    private final String[] textbookIds = {
            "1",
            "2",
            "3",
            "4"
    };

    private final String[] textbookNames = {
            "Calculus 101",
            "Intro to Signals & Systems",
            "Philosophy for Geniuses",
            "A Guide to Cryptocurrencies",
    };

    private final String[] textbookDescriptions = {
            "The only maths textbook you'll ever need.",
            "The textbook for the hardest course you're going to do in your life. Ever.",
            "Blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah.",
            "Cryptos are the future. Learn how to HODL to the moon, buy your lambo, invest in ICOs and sell during a crash."
    };

    private final int[] drawables = {
            R.drawable.green_drawable,
            R.drawable.red_drawable,
            R.drawable.blue_drawable,
            R.drawable.yellow_drawable
    };

    private final String[] names = {
            "Dev",
            "Tom",
            "Senyo",
            "Tamir"
    };

    public Database() {
        random = new Random();
    }

    public List<Listing> getListings() {

        // TODO Pull listings from database
        ArrayList<Listing> listings = new ArrayList<>();

        // TODO change to actual number of listings to load
        int numberOfListingsToLoad = 4;

        for (int i=0; i<numberOfListingsToLoad; i++) {
            listings.add(createNewListing());
        }

        return listings;
    }

    // Fetches the info for a specific listing from Firebase and returns a Listing object
    public Listing getListing(String listingId) {
        int randint = random.nextInt(4);

        //TODO Hook this up to work with DB
        // String textbookName =
        // String description =
        // int price =
        // String sellerID =

        // Listing listing = new Listing(textbookName, description, price, sellerId);

        // TODO Erase this fakery
        Listing listing = new Listing(textbookNames[randint], textbookDescriptions[randint], drawables[randint]);

        return listing;
    }

    public Listing createNewListing() {

        int randOne = random.nextInt(4);

        Listing listing = new Listing(textbookNames[randOne], textbookDescriptions[randOne], drawables[randOne]);

        return listing;

    }

    public void deleteListing(Listing listing) {

    }

    public void insertListing(Listing listing) {

    }


}
