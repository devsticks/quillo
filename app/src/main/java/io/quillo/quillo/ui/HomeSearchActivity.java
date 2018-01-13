package io.quillo.quillo.ui;

import android.content.Intent;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.quillo.quillo.R;
import io.quillo.quillo.data.FakeDatabase;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.logic.Controller;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeSearchActivity extends AppCompatActivity implements HomeSearchInterface, View.OnClickListener {

    private static final String EXTRA_TEXTBOOK_NAME = "EXTRA_TEXTBOOK_NAME";
    private static final String EXTRA_TEXTBOOK_DESCRIPTION = "EXTRA_TEXTBOOK_DESCRIPTION";
    private static final String EXTRA_DRAWABLE = "EXTRA_DRAWABLE";

    private List<Listing> listOfData;

    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private android.support.v7.widget.Toolbar toolbar;

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        recyclerView = (RecyclerView) findViewById(R.id.rec_home_search_listing_holder);
        layoutInflater = getLayoutInflater();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tlb_home_search_activity);
        toolbar.setTitle(R.string.title_toolbar);
//        toolbar.setLogo(R.drawable.ic_view_list_white_24dp);
        toolbar.setTitleMarginStart(72);

        controller = new Controller(this, new FakeDatabase());

        FloatingActionButton fabulous = (FloatingActionButton) findViewById(R.id.fab_create_new_item);
        fabulous.setOnClickListener(this);
    }

    @Override
    public void startListingDetailActivity(String textbookName, String textbookDescription, int colorResource, View viewRoot) {
        Intent i = new Intent(this, ListingDetailActivity.class);
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

    @Override
    public void setUpAdapterAndView(List<Listing> listOfData) {
        this.listOfData = listOfData;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(HomeSearchActivity.this, R.drawable.divider_white));

        recyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void addNewListingToView(Listing newListing) {
        listOfData.add(newListing);
        int endOfList = listOfData.size() - 1;
        adapter.notifyItemInserted(endOfList);
        //recyclerView.smoothScrollToPosition(endOfList);
    }

    @Override
    public void deleteListingCellAt(int position) {
        listOfData.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void showUndoSnackBar() {
        Snackbar.make(
                findViewById(R.id.root_home_search_activity),
                getString(R.string.action_delete_item),
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onUndoConfirmed();
            }
        })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        controller.onSnackbarTimeout();
                    }
                })
                .show();
    }

    @Override
    public void insertListingCellAt(int position, Listing listItem) {
        listOfData.add(position, listItem);
        adapter.notifyItemInserted(position);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.fab_create_new_item) {
            controller.createNewListing();
        }
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ListingCellViewHolder> {

        @Override
        public ListingCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.listing_cell, parent, false);

            return new ListingCellViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ListingCellViewHolder holder, int position) {
            Listing currentItem = listOfData.get(position);

            holder.mTextbookIcon.setImageResource(currentItem.getColorResource());
            holder.mTextbookDescription.setText(currentItem.getDescription());
            holder.mTextbookName.setText(currentItem.getName());
            holder.mLoading.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return listOfData.size();
        }

        class ListingCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private CircleImageView mTextbookIcon;
            private TextView mTextbookDescription;
            private TextView mTextbookName;
            private ViewGroup mContainer;
            private ProgressBar mLoading;

            public ListingCellViewHolder(View itemView) {
                super(itemView);

                this.mTextbookIcon = (CircleImageView) itemView.findViewById(R.id.imv_cell_listing_icon);
                this.mTextbookDescription = (TextView) itemView.findViewById(R.id.lbl_cell_textbook_description);
                this.mTextbookName = (TextView) itemView.findViewById(R.id.lbl_cell_textbook_name);
                this.mContainer = (ViewGroup) itemView.findViewById(R.id.root_list_item);
                this.mLoading = (ProgressBar) itemView.findViewById(R.id.pro_item_data);

                this.mContainer.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Listing listing = listOfData.get(this.getAdapterPosition());

                controller.onListingCellClick(listing, view);
            }
        }

    }

    private ItemTouchHelper.Callback createHelperCallback () {
                /*First Param is for Up/Down motion, second is for Left/Right.
        Note that we can supply 0, one constant (e.g. ItemTouchHelper.LEFT), or two constants (e.g.
        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) to specify what directions are allowed.
        */
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            //not used, as the first parameter above is 0
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                controller.onListingSwiped(
                        position,
                        listOfData.get(position)
                );
            }
        };

        return simpleItemTouchCallback;
    }

}
