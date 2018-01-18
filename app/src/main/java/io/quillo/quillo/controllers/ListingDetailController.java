package io.quillo.quillo.controllers;

import android.view.View;

import io.quillo.quillo.data.CustomFirebaseDatabase;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.views.ListingDetailActivity;

/**
 * Created by Stickells on 15/01/2018.
 */

public class ListingDetailController {

    private ListingDetailActivity view;
    private CustomFirebaseDatabase customFirebaseDatabase; // ??????

    public ListingDetailController(ListingDetailActivity view, CustomFirebaseDatabase customFirebaseDatabase) {
        this.view = view;
        this.customFirebaseDatabase = customFirebaseDatabase;

        getSellerFromDatabase();
    }

    public void getSellerFromDatabase() {
        view.onSellerLoaded(new Person("arra", "Amy Stickells", "akjsdlkj@lkjf.com", "080351"));
    }

    public void handleSellerDetailButtonClick (Person person, View viewRoot) {
        view.startProfileActivity(person, viewRoot);
    }

}
