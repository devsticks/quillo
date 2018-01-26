package io.quillo.quillo.controllers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;


import io.quillo.quillo.Fragments.AddEditListingFragment;
import io.quillo.quillo.Fragments.BookmarksFragment;
import io.quillo.quillo.Fragments.ProfileFragment;
import io.quillo.quillo.Fragments.SearchFragment;
import io.quillo.quillo.R;
import io.quillo.quillo.utils.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {

    private SearchFragment searchFragment;
    private BookmarksFragment bookmarksFragment;
    private AddEditListingFragment addEditListingFragment;
    private ProfileFragment profileFragment;
    private Fragment selectedFragment = null;
    private BottomNavigationView navigation;
    private Toolbar toolbar;

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
                    selectedFragment = bookmarksFragment;
                    break;

                case R.id.btn_add_listing:
                    selectedFragment = AddEditListingFragment.newInstance();
                    break;


                case R.id.btn_profile:
                    selectedFragment = profileFragment;
                    break;

            }
            changeFragment();
            return true;
        }
    };

    private void changeFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNavBar();
        initFragments();

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

    private void loadFirstFragment(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_drawer_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}
