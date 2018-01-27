package io.quillo.quillo.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.quillo.quillo.Fragments.AddEditListingFragment;
import io.quillo.quillo.Fragments.BookmarksFragment;
import io.quillo.quillo.Fragments.LandingFragment;
import io.quillo.quillo.Fragments.ListingDetailFragment;
import io.quillo.quillo.Fragments.LoginSignupFragment;
import io.quillo.quillo.Fragments.ProfileFragment;
import io.quillo.quillo.Fragments.SearchFragment;
import io.quillo.quillo.R;
import io.quillo.quillo.data.FirebaseHelper;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.interfaces.PersonListener;
import io.quillo.quillo.utils.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {
    private static final int RC_PERMISSIONS = 1;

    private SearchFragment searchFragment;
    private BookmarksFragment bookmarksFragment;
    private AddEditListingFragment addEditListingFragment;
    private ProfileFragment profileFragment;
    private Fragment selectedFragment = null;
    private BottomNavigationView bottomNavigation;
    private Toolbar toolbar;


    private QuilloDatabase quilloDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNavBar();
        initFragments();
        quilloDatabase = new QuilloDatabase();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkIfUniversityIsKnown();
    }


    public void hideBottomNavBar(){
        bottomNavigation.setVisibility(View.GONE);
    }

    public void showBottomNavbar(){
        bottomNavigation.setVisibility(View.VISIBLE);
    }

    private void initBottomNavBar(){
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);
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
            FirebaseAuth.getInstance().signOut();
            showLoginScreen();
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    public void checkIfUniversityIsKnown(){
        FirebaseUser currentUser = FirebaseHelper.getCurrentFirebaseUser();
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        final String universityUid = sharedPreferences.getString(getString(R.string.shared_pref_university_key), null);

        if (universityUid == null){
            //Uni is not saved in shared pref
            if(currentUser == null){
                //User is not logged in
                LandingFragment landingFragment = new LandingFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_holder, landingFragment)
                        .addToBackStack(null)
                        .commit();

                hideBottomNavBar();

            }else{
                //User is logged in get uni from firebase
                quilloDatabase.loadPerson(currentUser.getUid(), new PersonListener() {
                    @Override
                    public void onPersonLoaded(Person person) {
                        String personUniversityUid = person.getUniversityUid();
                        saveUniversityUidToSharedPrefrences(personUniversityUid);
                    }
                });

            }
        }
    }

    public void saveUniversityUidToSharedPrefrences(String universityUid){
        SharedPreferences.Editor editor = this.getPreferences(Context.MODE_PRIVATE).edit();
        Log.i(MainActivity.class.getName(), "Saving Uni to shared prefrences: "+ universityUid);
        editor.putString(getString(R.string.shared_pref_university_key), universityUid );
        editor.commit();
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
        if (FirebaseHelper.getCurrentFirebaseUser()!= null){
            return true;
        }else{

            showLoginAlert();
            //TODO: Find out how to manualy reset the selected tab button to search
            View view = bottomNavigation.findViewById(R.id.btn_search);
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
        transaction.replace(R.id.content_holder, listingDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("Search Fragment")
                .commit();

        hideBottomNavBar();
    }

    //TODO: Make one method for this stuff with a fragment as the argument
    private void showLoginScreen(){
        hideBottomNavBar();
        LoginSignupFragment loginSignupFragment = new LoginSignupFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, loginSignupFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showRegisterScreen(){
        hideBottomNavBar();
        LoginSignupFragment loginSignupFragment = new LoginSignupFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, loginSignupFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }


}
