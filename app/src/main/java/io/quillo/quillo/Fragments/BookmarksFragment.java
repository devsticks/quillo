package io.quillo.quillo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.quillo.quillo.Enums.RecyclerViewState;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.ListingAdapter;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.QuilloDatabase;
import io.quillo.quillo.interfaces.BookmarkListener;
import io.quillo.quillo.interfaces.ListingCellListener;

/**
 * Created by shkla on 2018/01/22.
 */

public class BookmarksFragment extends Fragment implements ListingCellListener, BookmarkListener {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ListingAdapter adapter;
    private QuilloDatabase quilloDatabase;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewState recyclerViewState;
    @BindView(R.id.loading_state)
    View loadingState;
    @BindView(R.id.empty_state)
    View emptyState;

    public static BookmarksFragment newInstance(){
        BookmarksFragment bookmarksFragment = new BookmarksFragment();
        return  bookmarksFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quilloDatabase = new QuilloDatabase();
        adapter = new ListingAdapter(this, getContext(), false);
        setupDatabase();
    }

    public void setupDatabase(){
        quilloDatabase.setBookmarkListener(this);
        quilloDatabase.observeBookmarks();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ButterKnife.bind(this, view);
        setupView(view);



        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupView(view);
            }
        });

        return view;
    }
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).toolbar.setVisibility(View.GONE);
        ((MainActivity)getActivity()).showBottomNavBar();
        if(adapter.getItemCount() == 0){
            changeState(RecyclerViewState.EMPTY);
        }else{
            changeState(RecyclerViewState.NORMAL);
        }
    }

    @Override
    public void onListingClick(Listing listing) {
        ((MainActivity)getActivity()).showListingDetailFragment(listing);
    }

    @Override
    public void onUnBookmarkClick(Listing listing) {
        quilloDatabase.removeBookmark(listing);

    }

    @Override
    public void onBookmarkClick(Listing listing) {

    }

    @Override
    public void onBookmarkAdded(Listing listing) {
        listing.setBookmarked(true);
        adapter.addListing(listing);
    }

    @Override
    public void onBookmarkRemoved(String listingUid) {
        adapter.removeListing(listingUid);
        if(adapter.getItemCount() == 0){
            changeState(RecyclerViewState.EMPTY);
        }

    }

    private void setupView(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_white));
        recyclerView.addItemDecoration(itemDecoration);

        onRefreshComplete();
    }

    private void onRefreshComplete() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void changeState(RecyclerViewState recyclerViewState){
        switch (recyclerViewState){
            case NORMAL:
                recyclerView.setVisibility(View.VISIBLE);
                loadingState.setVisibility(View.GONE);
                emptyState.setVisibility(View.GONE);
                break;
            case LOADING:
                //Glide.with(getContext()).load(R.drawable.porkie_loader).into(loadingImageView);
                recyclerView.setVisibility(View.GONE);
                loadingState.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
                break;
            case EMPTY:
                recyclerView.setVisibility(View.GONE);
                loadingState.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d(BookmarksFragment.class.getName(), "Invalid fragment state:  "+ recyclerViewState);


        }

    }

}
