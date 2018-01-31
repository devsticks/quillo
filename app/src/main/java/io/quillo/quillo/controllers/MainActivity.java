package io.quillo.quillo.controllers;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

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
    private Fragment lastFragment = null;
    private BottomNavigationView bottomNavigation;
    private Toolbar toolbar;

    public QuilloDatabase quilloDatabase;

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
        changeFragment(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       MenuInflater menuInflater = getMenuInflater();
       menuInflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(searchFragment);

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
            showLoginRegisterScreen(searchFragment, true);
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
                        //.addToBackStack(null) we don't want to be able to go back to this...
                        .commit();

                hideBottomNavBar();

                //hide notification bar and toolbar
                toolbar.setVisibility(View.GONE);
                View decorView = this.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);

            } else {
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
                    if (userIsLoggedIn()) {
                        selectedFragment = bookmarksFragment;
                    } else {
                        showLoginAlert(selectedFragment, (Fragment)bookmarksFragment);
                    }
                    break;

                case R.id.btn_add_listing:
                    if (userIsLoggedIn()) {
                        selectedFragment = AddEditListingFragment.newInstance();
                    } else {
                        showLoginAlert(selectedFragment, (Fragment)AddEditListingFragment.newInstance());
                    }
                    break;


                case R.id.btn_profile:
                    if (userIsLoggedIn()) {
                        selectedFragment = profileFragment;
                    } else {
                        showLoginAlert(selectedFragment, (Fragment)profileFragment);
                    }
                    break;

            }
            changeFragment(true);
            return true;
        }
    };

    //Always call before changeFragment
    public void setSelectedFragment(Fragment selectedFragment) {
        lastFragment = this.selectedFragment;
        this.selectedFragment = selectedFragment;
    }

    public void changeFragment(boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_holder, selectedFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (addToBackStack) {
            transaction.addToBackStack(getSupportFragmentManager().findFragmentById(R.id.content_holder).getClass().getName());
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1);
        String backFragmentName = backStackEntry.getName();
        String searchFragmentClassName = searchFragment.getClass().getName();
        String bookmarksFragmentClassName = bookmarksFragment.getClass().getName();
        String profileFragmentClassName = profileFragment.getClass().getName();

        if (backFragmentName.equals(searchFragmentClassName)) {
            bottomNavigation.getMenu().getItem(0).setChecked(true);
        } else if (backFragmentName.equals(bookmarksFragmentClassName)) {
            bottomNavigation.getMenu().getItem(1).setChecked(true);
        } else if (backFragmentName.equals(profileFragmentClassName)) {
            bottomNavigation.getMenu().getItem(3).setChecked(true);
        }

        super.onBackPressed();
    }

    public boolean userIsLoggedIn(){
        if (FirebaseHelper.getCurrentFirebaseUser()!= null){
            return true;
        } else {
//            showLoginAlert();
//            View view = bottomNavigation.findViewById(R.id.btn_search);
//            view.callOnClick();
            return  false;
        }
    }

    private void showLoginAlert(final Fragment comingFrom, final Fragment goingTo){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        alertDialog.setTitle("Whoops you are not logged in");

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                selectedFragment = comingFrom;
                changeFragment(false);
            }
        });

        alertDialog.setNegativeButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showLoginRegisterScreen(goingTo, true);
                dialogInterface.cancel();
            }
        });

        alertDialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showLoginRegisterScreen(goingTo, false);
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    public void showListingDetailFragment(Listing listing){
        Bundle bundle = new Bundle();

        bundle.putSerializable(IntentExtras.EXTRA_LISTING_UID, listing.getUid());

        ListingDetailFragment listingDetailFragment = new ListingDetailFragment();
        listingDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_holder, listingDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(getSupportFragmentManager().findFragmentById(R.id.content_holder).getClass().getName())
                .commit();

        hideBottomNavBar();
    }

    private void showLoginRegisterScreen(Fragment goingTo, boolean isLoggingIn){
        hideBottomNavBar();
        LoginSignupFragment loginSignupFragment = new LoginSignupFragment();
        loginSignupFragment.setIntentions(goingTo, isLoggingIn);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, loginSignupFragment)
                .addToBackStack(getSupportFragmentManager().findFragmentById(R.id.content_holder).getClass().getName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void showSearchFragment() {
        SearchFragment searchFragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, searchFragment)
                .addToBackStack(getSupportFragmentManager().findFragmentById(R.id.content_holder).getClass().getName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void showSearchFragmentAfterLanding() {
        toolbar.setVisibility(View.VISIBLE);

        SearchFragment searchFragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, searchFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


}
