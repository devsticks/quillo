package io.quillo.quillo.ui;

import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import io.quillo.quillo.R;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.logic.Controller;

/**
 * Created by Stickells on 14/01/2018.
 */

public class ProfileActivity {
//
//    private Controller controller;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_search);
//
//        FloatingActionButton fabulous = (FloatingActionButton) findViewById(R.id.fab_create_new_item);
//        fabulous.setOnClickListener(this);
//    }
//
//    // Handles click of "Add Listing" button / fab
//    @Override
//    public void onClick(View view) {
//        int viewId = view.getId();
//
//        if (viewId == R.id.fab_create_new_item) {
//            controller.createNewListing();
//        }
//    }
//
//    @Override
//    public void addNewListingToView(Listing newListing) {
//        listOfData.add(newListing);
//        int endOfList = listOfData.size() - 1;
//        adapter.notifyItemInserted(endOfList);
//        //listingRecyclerView.smoothScrollToPosition(endOfList);
//    }
//
//    @Override
//    public void deleteListingCellAt(int position) {
//        listOfData.remove(position);
//        adapter.notifyItemRemoved(position);
//    }
//
//    @Override
//    public void showUndoSnackBar() {
//        Snackbar.make(
//                findViewById(R.id.root_home_search_activity),
//                getString(R.string.action_delete_item),
//                Snackbar.LENGTH_LONG
//        ).setAction(R.string.action_undo, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                controller.onUndoConfirmed();
//            }
//        })
//                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                    @Override
//                    public void onDismissed(Snackbar transientBottomBar, int event) {
//                        super.onDismissed(transientBottomBar, event);
//
//                        controller.onSnackbarTimeout();
//                    }
//                })
//                .show();
//    }
//
//    @Override
//    public void insertListingCellAt(int position, Listing listItem) {
//        listOfData.add(position, listItem);
//        adapter.notifyItemInserted(position);
//    }
//
//    // Handles swiping Listing cells
//    private ItemTouchHelper.Callback createHelperCallback () {
//                /*First Param is for Up/Down motion, second is for Left/Right.
//        Note that we can supply 0, one constant (e.g. ItemTouchHelper.LEFT), or two constants (e.g.
//        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) to specify what directions are allowed.
//        */
//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
//                0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            //not used, as the first parameter above is 0
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                                  RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//
//            @Override
//            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                int position = viewHolder.getAdapterPosition();
//                controller.onListingSwiped(
//                        position,
//                        listOfData.get(position)
//                );
//            }
//        };
//
//        return simpleItemTouchCallback;
//    }
}
