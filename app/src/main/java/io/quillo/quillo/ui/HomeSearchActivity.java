package io.quillo.quillo.ui;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import io.quillo.quillo.R;
import io.quillo.quillo.data.FakeDatabase;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.logic.Controller;

import java.util.List;

public class HomeSearchActivity extends AppCompatActivity implements HomeSearchInterface, View.OnClickListener {

    private static final String EXTRA_TEXTBOOK_NAME = "EXTRA_TEXTBOOK_NAME";
    private static final String EXTRA_TEXTBOOK_DESCRIPTION = "EXTRA_TEXTBOOK_DESCRIPTION";
    private static final String EXTRA_DRAWABLE = "EXTRA_DRAWABLE";

    private List<Listing> listOfData;

    private LayoutInflater layoutInflater;
    private RecyclerView listingRecyclerView;
    private ListingRecyclerAdapter adapter;
    private android.support.v7.widget.Toolbar toolbar;

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

//        listingRecyclerView = (RecyclerView) findViewById(R.id.rec_home_search_listing_holder);
//        layoutInflater = getLayoutInflater();

//        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tlb_home_search_activity);
//        toolbar.setTitle(R.string.title_toolbar);
////        toolbar.setLogo(R.drawable.ic_view_list_white_24dp);
//        toolbar.setTitleMarginStart(72);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ListingRecyclerFragment fragment = new ListingRecyclerFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

}
