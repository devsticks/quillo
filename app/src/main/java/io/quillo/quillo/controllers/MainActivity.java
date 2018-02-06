package io.quillo.quillo.controllers;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import io.quillo.quillo.Fragments.AddEditListingFragment;
import io.quillo.quillo.Fragments.BookmarksFragment;
import io.quillo.quillo.Fragments.LandingFragment;
import io.quillo.quillo.Fragments.ListingDetailFragment;
import io.quillo.quillo.Fragments.LoginSignupFragment;
import io.quillo.quillo.Fragments.ProfileFragment;
import io.quillo.quillo.Fragments.SearchFragment;
import io.quillo.quillo.R;
import io.quillo.quillo.utils.FirebaseHelper;
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
    public Toolbar toolbar;
    private boolean mustShowBottomNavBar = true;
    MaterialSearchView searchView;

    public QuilloDatabase quilloDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quilloDatabase = new QuilloDatabase();
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        initBottomNavBar();
        initFragments();
        initSearchBar();
        setSupportActionBar(toolbar);

    }

    public void hideBottomNavBar() {
        mustShowBottomNavBar = false;
        bottomNavigation.setVisibility(View.GONE);
    }

    public void showBottomNavBar() {
        mustShowBottomNavBar = true;
        bottomNavigation.setVisibility(View.VISIBLE);
    }

    public void setupToolbar(){
        toolbar.getBackground().setAlpha(0);
    }

    private void initBottomNavBar(){
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);

        KeyboardVisibilityEvent.setEventListener(this,
            new KeyboardVisibilityEventListener() {
                @Override
                public void onVisibilityChanged(boolean isOpen) {
                    if (mustShowBottomNavBar) {
                        bottomNavigation.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                    }
                }
            });
    }

    private void initFragments(){
        searchFragment = SearchFragment.newInstance();
        bookmarksFragment = BookmarksFragment.newInstance();
        profileFragment = ProfileFragment.newInstance();

        selectedFragment = searchFragment;
        changeFragment(false);
    }

    private void initSearchBar(){
        searchView = (MaterialSearchView)findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                if (selectedFragment != searchFragment){
                    setSelectedFragment(searchFragment);
                    changeFragment(true);
//                    updateTabBar(selectedFragment.getTag());
                }
            }

            @Override
            public void onSearchViewClosed() {
                searchFragment.onQueryTextChange("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);
        menuInflater.inflate(R.menu.app_bar_overflow_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

//        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);

        searchView.setOnQueryTextListener(searchFragment);

        return true;
    }

    public void resetPersonFragment(){
        profileFragment = new ProfileFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //TODO Get rid of this for launch (along with menu item...
        if (id == R.id.landing){
            showLandingFragment();
        } else if (id == R.id.legal) {
            //TODO make legal page and call it here
        } else if (id == R.id.logout){
            FirebaseAuth.getInstance().signOut();

            showLoginRegisterScreen(searchFragment, true);
        }

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

                showLandingFragment();

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
            boolean addToBackStack = false;
            showToolbar();

            switch (item.getItemId()) {
                case R.id.btn_search:
                    if (selectedFragment.getClass().getName().equals(searchFragment.getClass().getName())) {
                        addToBackStack = false;
                    } else {
                        addToBackStack = true;
                    }
                    toolbar.getBackground().setAlpha(255);
                    selectedFragment =  searchFragment;
                    changeFragment(addToBackStack);
                    break;

                case R.id.btn_bookmarks:
                    if (userIsLoggedIn()) {
                        if (selectedFragment.getClass().getName().equals(bookmarksFragment.getClass().getName())) {
                            addToBackStack = false;
                        } else {
                            addToBackStack = true;
                        }
                        toolbar.getBackground().setAlpha(255);
                        selectedFragment = bookmarksFragment;
                        changeFragment(addToBackStack);
                    } else {
                        showLoginAlert(selectedFragment, (Fragment)bookmarksFragment);
                    }
                    break;

                case R.id.btn_add_listing:
                    if (userIsLoggedIn()) {
                        if (selectedFragment.getClass().getName().equals(AddEditListingFragment.class.getName())) {
                            addToBackStack = false;
                        } else {
                            addToBackStack = true;
                        }
                        hideToolbar();
                        selectedFragment = AddEditListingFragment.newInstance();
                        changeFragment(addToBackStack);
                    } else {
                        showLoginAlert(selectedFragment, (Fragment)AddEditListingFragment.newInstance());
                    }
                    break;


                case R.id.btn_profile:
                    if (userIsLoggedIn()) {
                        if (selectedFragment.getClass().getName().equals(profileFragment.getClass().getName())) {
                            addToBackStack = false;
                        } else {
                            addToBackStack = true;
                        }
                        toolbar.getBackground().setAlpha(0);
                        selectedFragment = profileFragment;
                        changeFragment(addToBackStack);
                    } else {
                        showLoginAlert(selectedFragment, (Fragment)profileFragment);
                    }
                    break;

            }

            return true;
        }
    };

    //Always call before changeFragment
    public void setSelectedFragment(Fragment selectedFragment) {
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
        if (backStackCount > 0) {
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1);
            String backFragmentName = backStackEntry.getName();
            updateTabBar(backFragmentName);
        }
        super.onBackPressed();
    }

    public void updateTabBar(String fragmentName) {
        String searchFragmentClassName = searchFragment.getClass().getName();
        String bookmarksFragmentClassName = bookmarksFragment.getClass().getName();
        String profileFragmentClassName = profileFragment.getClass().getName();

        if (fragmentName.equals(searchFragmentClassName)) {
            bottomNavigation.getMenu().getItem(0).setChecked(true);
        } else if (fragmentName.equals(bookmarksFragmentClassName)) {
            bottomNavigation.getMenu().getItem(1).setChecked(true);
        } else if (fragmentName.equals(profileFragmentClassName)) {
            bottomNavigation.getMenu().getItem(3).setChecked(true);
        }
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
                selectedFragment = comingFrom;
                changeFragment(false);
                updateTabBar(comingFrom.getClass().getName());

                dialogInterface.dismiss();
            }
        });

        alertDialog.setNegativeButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showLoginRegisterScreen(goingTo, true);
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showLoginRegisterScreen(goingTo, false);
                dialogInterface.dismiss();
            }
        });

        alertDialog.setOnCancelListener(
            new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    selectedFragment = comingFrom;
                    changeFragment(false);
                    updateTabBar(comingFrom.getClass().getName());

                    dialogInterface.dismiss();
                }
            }
        );

        alertDialog.show();
    }
    public void showProfileUpdateSuccess(){
        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
    }

    public void showLandingFragment() {
        LandingFragment landingFragment = new LandingFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, landingFragment)
                .commit();

        hideBottomNavBar();

        //hide notification bar and toolbar
        toolbar.setVisibility(View.GONE);
        View decorView = this.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
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
        toolbar.getBackground().setAlpha(0);
        //hideBottomNavBar();
    }

    private void showLoginRegisterScreen(Fragment goingTo, boolean isLoggingIn){
        hideBottomNavBar();
        hideToolbar();

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
        showToolbar();
        SearchFragment searchFragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, searchFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void hideToolbar () {
        toolbar.setVisibility(View.GONE);
    }

    public void showToolbar () {
        toolbar.setVisibility(View.VISIBLE);
    }

    public void showProfileFragment(boolean addToBackStack) {
        showToolbar();
        selectedFragment = profileFragment;
        changeFragment(addToBackStack);
        toolbar.getBackground().setAlpha(0);
    }
}
