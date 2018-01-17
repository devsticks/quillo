package io.quillo.quillo.handlers;

import android.view.View;

import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.views.ListingDetailActivity;

/**
 * Created by Stickells on 15/01/2018.
 */

public class ListingDetailController {

    private ListingDetailActivity view;
    private Database database; // ??????

    public ListingDetailController(ListingDetailActivity view, Database database) {
        this.view = view;
        this.database = database;

        getSellerFromDatabase();
    }

    public void getSellerFromDatabase() {
        view.onSellerLoaded(new Person("arra", "Amy Stickells", "akjsdlkj@lkjf.com", "080351"));
    }

    public void handleSellerDetailButtonClick (Person person, View viewRoot) {
        view.startProfileActivity(person, viewRoot);
    }

}
