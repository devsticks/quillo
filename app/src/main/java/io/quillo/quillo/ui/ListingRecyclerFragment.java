package io.quillo.quillo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.quillo.quillo.R;
import io.quillo.quillo.data.FakeDatabase;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.logic.Controller;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RadioButton;

import java.util.List;

/**
 * Created by Stickells on 14/01/2018.
 */

public class ListingRecyclerFragment extends Fragment {

// TODO IF ISSUES WITH ROTATING SCREEN, CHECK THIS OUT - SEE BODY PART FRAGMENT EXAMPLE
//        if (savedInstanceState != null) {
//            mImageIds = savedInstanceState.getIntegerArrayList(IMAGE_ID_LIST);
//            mListIndex = savedInstanceState.getInt(LIST_INDEX);
//        }

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;

    private static final String EXTRA_TEXTBOOK_NAME = "EXTRA_TEXTBOOK_NAME";
    private static final String EXTRA_TEXTBOOK_DESCRIPTION = "EXTRA_TEXTBOOK_DESCRIPTION";
    private static final String EXTRA_DRAWABLE = "EXTRA_DRAWABLE";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected ListingRecyclerAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected FakeDatabase mDataset;
    private List<Listing> listOfData;
    private Controller controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();
        controller = new Controller(this, mDataset);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listing_recycler, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rec_listing_recycler);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new ListingRecyclerAdapter();// mDataset);
        // Set ListingRecyclerAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mDataset = new FakeDatabase();
        listOfData = mDataset.getListings()

//                String[DATASET_COUNT];
//        for (int i = 0; i < DATASET_COUNT; i++) {
//            mDataset[i] = "This is element #" + i;
//        }
    }

    public void startListingDetailActivity(String textbookName, String textbookDescription, int colorResource, View viewRoot) {
        Intent i = new Intent(viewRoot.getContext(),ListingDetailActivity.class);
        i.putExtra(EXTRA_TEXTBOOK_NAME, textbookName);
        i.putExtra(EXTRA_TEXTBOOK_DESCRIPTION, textbookDescription);
        i.putExtra(EXTRA_DRAWABLE, colorResource);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setEnterTransition(new Fade(Fade.IN));
//            getWindow().setEnterTransition(new Fade(Fade.OUT));
//
//            ActivityOptions options = ActivityOptions
//                    .makeSceneTransitionAnimation(this,
//                            new Pair<View, String>(viewRoot.findViewById(R.id.imv_list_item_circle),
//                                    getString(R.string.transition_drawable)),
//                            new Pair<View, String>(viewRoot.findViewById(R.id.lbl_message),
//                                    getString(R.string.transition_message)),
//                            new Pair<View, String>(viewRoot.findViewById(R.id.lbl_date_and_time),
//                                    getString(R.string.transition_time_and_date)));
//
//            startActivity(i, options.toBundle());
//
//        } else {
                startActivity(i);
//        }
    }

    public void addNewListingToView(Listing newListing) {
        listOfData.add(newListing);
        int endOfList = listOfData.size() - 1;
        adapter.notifyItemInserted(endOfList);
        //listingRecyclerView.smoothScrollToPosition(endOfList);
    }
//
//    public void deleteListingCellAt(int position) {
//        listOfData.remove(position);
//        adapter.notifyItemRemoved(position);
//    }
//
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
//    public void insertListingCellAt(int position, Listing listItem) {
//        listOfData.add(position, listItem);
//        adapter.notifyItemInserted(position);
//    }

}