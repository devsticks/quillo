package io.quillo.quillo.controllers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import io.quillo.quillo.Fragments.AddEditListingFragment;
import io.quillo.quillo.Fragments.BookmarksFragment;
import io.quillo.quillo.Fragments.ListingDetailFragment;
import io.quillo.quillo.Fragments.LoginSignupFragment;
import io.quillo.quillo.Fragments.ProfileFragment;
import io.quillo.quillo.Fragments.SearchFragment;
import io.quillo.quillo.R;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.utils.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {

    private SearchFragment searchFragment;
    private BookmarksFragment bookmarksFragment;
    private AddEditListingFragment addEditListingFragment;
    private ProfileFragment profileFragment;
    private Fragment selectedFragment = null;
    private BottomNavigationView navigation;
    private Toolbar toolbar;

    private QuilloDatabase quilloDatabase;

    private FirebaseAuth auth;


    public void hideNavBar(){
        navigation.setVisibility(View.GONE);
    }

    public void showNavbar(){
        navigation.setVisibility(View.VISIBLE);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.btn_search:
                    selectedFragment =  searchFragment;
                    break;
                case R.id.btn_bookmarks:
                    if(userIsLoggedIn()) {
                        selectedFragment = bookmarksFragment;
                    }
                    break;

                case R.id.btn_add_listing:
                    if (userIsLoggedIn()) {
                        selectedFragment = AddEditListingFragment.newInstance();
                    }
                    break;


                case R.id.btn_profile:
                    if (userIsLoggedIn()) {
                        selectedFragment = profileFragment;
                    }
                    break;

            }
            changeFragment();
            return true;
        }
    };

    private void changeFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_holder, selectedFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private boolean userIsLoggedIn(){
        if (auth.getCurrentUser() != null){
            return true;
        }else{

            showLoginAlert();
            //TODO: Find out how to manualy reset the selected tab button to search
            View view = navigation.findViewById(R.id.btn_search);
            view.performClick();
            showLoginAlert();
            return  false;
        }
    }

    //TODO: Made a nice looking dialog
    private void showLoginAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Whoops you are not logged in");

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showLoginScreen();
                dialogInterface.cancel();

            }
        });

        alertDialog.setNeutralButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showRegisterScreen();
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    public void showListingDetailFragment(Listing listing){
        Bundle bundle = new Bundle();

        bundle.putSerializable(IntentExtras.EXTRA_LISTING, listing);

        ListingDetailFragment listingDetailFragment = new ListingDetailFragment();
        listingDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, listingDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("Search Fragment")
                .commit();

        hideNavBar();
    }


    private void showLoginScreen(){
        hideNavBar();
        LoginSignupFragment loginSignupFragment = new LoginSignupFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, loginSignupFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showRegisterScreen(){
        hideNavBar();
        LoginSignupFragment loginSignupFragment = new LoginSignupFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, loginSignupFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNavBar();
        initFragments();

        auth = FirebaseAuth.getInstance();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initBottomNavBar(){
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

    private void initFragments(){
        searchFragment = SearchFragment.newInstance();
        bookmarksFragment = BookmarksFragment.newInstance();
        profileFragment = ProfileFragment.newInstance();

        selectedFragment = searchFragment;
        changeFragment();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar_overflow_menu, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout){
            auth.signOut();
            showLoginScreen();
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}
